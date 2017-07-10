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

    public String sourceQueue = "amqp://topic:alfresco.repo.events.nodes?jmsMessageType=Text";

    public String targetStream = "aws-kinesis-firehose://alfresco-events?amazonKinesisFirehoseClient=#kinesisFirehoseClient";

    @Override
    public void configure() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Kinesis target is "+ targetStream);
        }

        from(sourceQueue).to(targetStream);

        if (logger.isDebugEnabled())
        {
            logger.debug("Finished configuring kinesis firehose route");
        }
    }


}
