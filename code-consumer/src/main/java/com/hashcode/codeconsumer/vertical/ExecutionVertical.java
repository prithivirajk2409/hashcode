package com.hashcode.codeconsumer.vertical;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hashcode.codeconsumer.CodeConsumerVertical;

@Component
public class ExecutionVertical extends AbstractCodeConsumerVertical {

	@Value("${execution_concurrency:3}")
	String concurrency;

	@Override
	public String getConcurrencyString() {
		return concurrency;
	}

	@Override
	public CodeConsumerVertical getVertical() {
		return CodeConsumerVertical.EXECUTION;
	}

}