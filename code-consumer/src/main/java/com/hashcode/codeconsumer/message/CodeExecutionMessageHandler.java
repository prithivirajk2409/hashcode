package com.hashcode.codeconsumer.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.service.CodeExecutionService;
import com.hashcode.entity.CodeExecutionRequest;
import com.hashcode.utils.CommonUtilities;

@Service
public class CodeExecutionMessageHandler implements MessageHandler {

	private static final Logger logger = LogManager.getLogger(CodeExecutionMessageHandler.class);

	@Autowired
	private CodeExecutionService codeExecutionService;

	@Override
	public void processMsg(CodeConsumerMessage message) throws Exception {
		logger.info("Recieved Message : {}", message);
		try {
			CodeExecutionRequest request = CommonUtilities.getObjectMapper().readValue(message.getData(),
					CodeExecutionRequest.class);
			switch (message.getTaskType()) {
			case TEST:
				codeExecutionService.processCodeExecutionRequestForTest(request);
				break;
			case SUBMIT:
				codeExecutionService.processCodeExecutionRequestForSubmit(request);
				break;
			default:
				throw new Exception("Unsupported task type");

			}
		} catch (Exception e) {
			throw e;
		}
	}

}