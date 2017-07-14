'use strict';

const aws = require('aws-sdk');
const http = require('http');

const s3 = new aws.S3({ apiVersion: '2006-03-01' });
const rekognition = new aws.Rekognition();

exports.handler = (event, context, callback) => {

    console.log('Received event:', JSON.stringify(event, null, 2));

    // grab the REPO_HOST
    var repoHost = process.env.REPO_HOST;

    // Get the object from the event and show its content type
    const bucket = event.Records[0].s3.bucket.name;
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
    const params = {
        Bucket: bucket,
        Key: key,
    };
    s3.getObject(params, (err, data) => {
        if (err) {
            console.log(err);
            const message = `Error getting object ${key} from bucket ${bucket}. Make sure they exist and your bucket is in the same region as this function.`;
            console.log(message);
            callback(message);
        } else {

            // TODO: deal with multiple events on mulitple lines, for now, presume only one

            var alfEventString = data.Body.toString('ascii');
            console.log("alfEventString: " + alfEventString);

            var alfEventJson;
            try {
                alfEventJson = JSON.parse(alfEventString);
                console.log("alfEventJson: ", alfEventJson);
            } catch (e) {
               callback(e);
               return;
            }

            // grab the node id
            var nodeId = alfEventJson.nodeId
            console.log("nodeId: " + nodeId);

            // grab the aspects

            // get the content using REPO_URL
            var nodePath = "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId + "/content";
            console.log("retrieving content from: " + repoHost + nodePath);

            var options = {
                hostname: repoHost,
                path: nodePath,
                auth: "admin:i-0945b3d888a1f0dc8"
            };

            const request = http.request(options, function (response) {
                var bytes = [];
                response.on('data', function (chunk) {
                    bytes.push(chunk);
                });

                response.on('end', function () {
                    console.log('Successfully retrieved content with status: ' + response.statusCode);

                    // call the rekognition API to get suggested labels using bytes of image
                    var params = {
                      Image: {
                        Bytes: Buffer.concat(bytes)
                      },
                      MaxLabels: 25,
                      MinConfidence: 75
                    };

                    console.log("Analysing image...");
                    rekognition.detectLabels(params, function(err, data) {
                        if (err) {
                            callback(err);
                        } else {
                            console.log("Successfully analysed image: " + JSON.stringify(data, null, 2));

                            var isCar = false;
                            var isMotorcycle = false;
                            var isBicycle = false;

                            var labels = data.Labels;
                            labels.forEach(function(entry) {
                                if (entry.Name == "Car") {
                                    isCar = true;
                                } else if (entry.Name == "Motorcycle") {
                                    isMotorcycle = true;
                                } else if (entry.Name == "Bicycle") {
                                    isBicycle = true;
                                }
                            });

                            // call the REST API to set metadata appropriately
                            var nodeInfoPath = "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId;
                            var options = {
                                hostname: repoHost,
                                path: nodeInfoPath,
                                method: "PUT",
                                auth: "admin:i-0945b3d888a1f0dc8",
                                headers: {
                                    "Content-Type": "application/json"
                                }
                            };

                            // define the update request
                            const updateRequest = http.request(options, function (updateResponse) {
                                updateResponse.on('end', function () {
                                    console.log("Successfully updated image with status: " + updateResponse.statusCode);
                                    callback(null, "Processing complete");
                                });
                            });

                            // update request error handler
                            updateRequest.on('error', function (err) {
                                console.log("Failed to update image: " + err);
                                callback(err);
                            });

                            var updateBody = {
                                nodeType: "acme:insuranceClaimImage",
                                properties: {
                                    "acme:imageId": Date.now()
                                }
                            };

                            // update body appropriately
                            if (isCar) {
                                updateBody.properties["acme:claimType"] = "Car";
                            } else if (isMotorcycle) {
                                updateBody.properties["acme:claimType"] = "Motorcycle";
                            } else if (isBicycle) {
                                updateBody.properties["acme:claimType"] = "Bicycle";
                            } else {
                                // add the missing property aspect, note: ideally here
                                // we would retrieve the latest version of the node to
                                // get the current aspect names
                                var aspects = [
                                    "rn:renditioned",
                                    "cm:versionable",
                                    "cm:titled",
                                    "cm:auditable",
                                    "cm:author",
                                    "cm:thumbnailModification",
                                    "exif:exif",
                                    "acme:missingClaimTypeProperty"
                                    ];
                                updateBody.aspectNames = aspects;
                            }

                            // execute the update request
                            var updateBodyString = JSON.stringify(updateBody);
                            console.log("Updating image '" + nodeId + "' with: " + updateBodyString);
                            updateRequest.write(updateBodyString);
                            updateRequest.end();
                        }
                    });
                });
            });

            // content request error handler
            request.on('error', function (err) {
                console.log("Failed to retrieve content: " + err);
                callback(err);
            });

            // make the remote call to get the content
            request.end();
        }
    });
};
