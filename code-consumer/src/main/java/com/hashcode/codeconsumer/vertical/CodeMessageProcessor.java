package com.hashcode.codeconsumer.vertical;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.CodeConsumerTaskType;
import com.hashcode.codeconsumer.message.CodeExecutionMessageHandler;
import com.hashcode.codeconsumer.message.MessageHandler;
import jakarta.annotation.PostConstruct;

@Component
public class CodeMessageProcessor {
	private static final Logger logger = LogManager.getLogger(CodeMessageProcessor.class);
	private Map<CodeConsumerTaskType, Class<? extends MessageHandler>> messageHandlerBeanMap;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	private void init() {
		messageHandlerBeanMap = new HashMap<>();
		messageHandlerBeanMap.put(CodeConsumerTaskType.TEST, CodeExecutionMessageHandler.class);
		messageHandlerBeanMap.put(CodeConsumerTaskType.SUBMIT, CodeExecutionMessageHandler.class);
	}

	public void processMsg(CodeConsumerMessage message) throws Exception {
		long processingStartTime = System.currentTimeMillis();
		logger.info("Processing Code Event : {}", message.getTaskType());
		try {
			context.getBean(messageHandlerBeanMap.get(message.getTaskType())).processMsg(message);
		} catch (Exception e) {
			throw new Exception();
		}

		logger.info("Total Execution Time(success) for task type : {} is :{}", message.getTaskType(),
				(System.currentTimeMillis() - processingStartTime) / 1000);

	}
}