package org.alfresco.messaging.camel.routes;

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClientBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Route builder for Kinesis Firehose Stream
 *
 * @author Gavin Cornwell
 */
@Component
public class KinesisFirehoseRouteBuilder extends SpringRouteBuilder
{
    private static Log logger = LogFactory.getLog(KinesisFirehoseRouteBuilder.class);

    private static final String SOURCE_QUEUE = "amqp://topic:alfresco.repo.events.nodes?jmsMessageType=Text";
    private static final String TARGET_PREFIX = "aws-kinesis-firehose://";
    private static final String TARGET_SUFFIX = "?amazonKinesisFirehoseClient=#kinesisFirehoseClient";

    @Value("${firehose.extension.stream.name}")
    protected String targetStreamName = "alfresco-events";

    @Override
    public void configure() throws Exception
    {
        String targetStream = TARGET_PREFIX + targetStreamName + TARGET_SUFFIX;

        if (logger.isDebugEnabled())
        {
            logger.debug("Kinesis target is "+ targetStream);
        }

        from(SOURCE_QUEUE).to(targetStream);

        if (logger.isDebugEnabled())
        {
            logger.debug("Finished configuring kinesis firehose route");
        }
    }
}
