'use strict';

exports.handler = (event, context, callback) => {

    console.log("event: " + JSON.stringify(event, null, 2));

    var output = [];

    event.records.forEach(function(record) {

        var decodedData = Buffer.from(record.data, 'base64');
        var decodedDataObj = JSON.parse(decodedData);
        var result = "Dropped";

        console.log("record: " + JSON.stringify(decodedDataObj, null, 2));

        if (decodedDataObj.type == "NODEADDED" &&
            decodedDataObj.nodeType == "cm:content" &&
            (decodedDataObj.name.endsWith(".jpg")||decodedDataObj.name.endsWith(".png"))) {
            result = "Ok";
            console.log("Accepted record " + record.recordId);
        } else {
            console.log("Dropped record " + record.recordId);
        }

        output.push({
            recordId: record.recordId,
            result: result,
            data: record.data
        });
    });

    console.log(`Successfully processed ${output.length} records.`);

    callback(null, { records: output });
};