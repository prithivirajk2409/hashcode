package com.hashcode.codeconsumer.vertical;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.CodeConsumerVertical;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

public abstract class AbstractCodeConsumerVertical {

	private static final Logger logger = LogManager.getLogger(AbstractCodeConsumerVertical.class);

	public abstract String getConcurrencyString();

	public abstract CodeConsumerVertical getVertical();

	@Autowired
	private CodeMessageProcessor codeMessageProcessor;

	private CodeConsumerMessage messagePayload;

	public void processMsg(@Payload SQSTextMessage message) throws Exception {
		try {
			this.messagePayload = CommonUtilities.getObjectMapper().readValue(message.getText(),
					CodeConsumerMessage.class);
			codeMessageProcessor.processMsg(messagePayload);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			throw new Exception(e);
		}

	}

}
