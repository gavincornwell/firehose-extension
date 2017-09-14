# Welcome

This project contains an [Alfresco](https://www.alfresco.com) extension that sends repository events to Kinesis Firehose and a custom model for use in demonstrations.

The platform extension defines a Camel route from the ActiveMQ queue used by desktop sync to [Kinesis Firehose](https://aws.amazon.com/kinesis/firehose/) which pushes all events into the AWS eco-system. A custom model representing a simple insurance claim scenario is also defined and registered.

The Share extension defines the necessary form configuration to display the type and aspect provided by the custom model.

# Pre-requisites

As this extension makes use of the [ActiveMQ](http://activemq.apache.org/) messaging capabilities an Enterprise Edition of 5.2 or higher is required.

ActiveMQ needs to be installed and running on the same machine as the repository.

The [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/installing.html) needs to be present and configured with an access ID/key that has permissions to post records to Kinesis Firehose.

A configured [Kinesis Firehose](https://aws.amazon.com/kinesis/firehose/) stream named "alfresco-events" to send the events to.

# Build & Deploy

This project is based on the [Alfresco SDK](https://github.com/Alfresco/alfresco-sdk) and produces simple JAR modules for the Repository and Share. To make deployment easier, the Repository JAR also includes the required Camel AWS classes and it's dependencies i.e. the AWS SDK.

To build the project simply run

    mvn clean install

To build and run the system use the SDKs run.sh/bat or debug.sh/bat scripts.

# Configuration

The platform extension exposes the Kinesis Firehose stream name as a property so that it can be overridden by alfresco-global.properties. The property is named <code>firehose.extension.stream.name</code> and set to <code>alfresco-events</code> by default.

To see what is happening within the repository and to check everything is working as it should the following debug settings are recommended:

    log4j.logger.org.alfresco.messaging.camel.routes.KinesisFirehoseRouteBuilder=debug
    log4j.logger.org.apache.camel.component.aws=debug
    log4j.logger.com.amazonaws.request=debug

# Demo

A fully working demonstration that leverages this extension and incorporates the AWS Rekognition service can be found here: https://github.com/gavincornwell/firehose-rekognition-demo