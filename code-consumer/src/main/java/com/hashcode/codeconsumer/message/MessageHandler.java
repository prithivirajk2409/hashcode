package com.hashcode.codeconsumer.message;

import com.hashcode.codeconsumer.CodeConsumerMessage;

public interface MessageHandler {
	void processMsg(CodeConsumerMessage message) throws Exception;
}