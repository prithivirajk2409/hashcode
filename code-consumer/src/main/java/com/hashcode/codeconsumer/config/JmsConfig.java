package com.hashcode.codeconsumer.config;

import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.hashcode.enums.AppMode;
import com.hashcode.utils.Constants;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class JmsConfig {

	private static final Logger logger = LogManager.getLogger(JmsConfig.class);

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.secret.access.key}")
	private String awsSecretAccessKey;

//	public class CustomMessageListenerContainer extends DefaultMessageListenerContainer {
//		public CustomMessageListenerContainer() {
//			super();
//		}
//
//		protected void rollbackOnExceptionIfNecessary(Session session, Throwable ex) {
//			// do nothing, so that "visibilityTimeout" will stay same
//		}
//	}
//
//	public class CustomJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {
//		@Override
//		protected DefaultMessageListenerContainer createContainerInstance() {
//			return new CustomMessageListenerContainer();
//		}
//	}

	@Bean
	DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConcurrency("10-25");
		factory.setConnectionFactory(sqsConnectionFactory());
		factory.setDestinationResolver(dynamicDestinationResolver());
		factory.setErrorHandler(errorHandler());
		factory.setMessageConverter(messageConverter());
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		return factory;
	}

	@Bean
	DynamicDestinationResolver dynamicDestinationResolver() {
		return new DynamicDestinationResolver();
	}

	@Service
	public class SimpleErrorHandler implements ErrorHandler {
		@Override
		public void handleError(Throwable t) {
			logger.error("Error in listner");
		}
	}

	@Bean
	SimpleErrorHandler errorHandler() {
		return new SimpleErrorHandler();
	}

	@Bean
	SimpleMessageConverter messageConverter() {
		return new SimpleMessageConverter();
	}

	@Bean
	SQSConnectionFactory sqsConnectionFactory() {
		AwsCredentialsProvider credentialsProvider = Constants.APP_MODE.equals(AppMode.DEVELOPMENT)
				? StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretAccessKey))
				: DefaultCredentialsProvider.create();

		SqsClientBuilder sqsClientBuilder = SqsClient.builder().region(Region.of(Constants.AWS_REGION))
				.credentialsProvider(credentialsProvider);
		return new SQSConnectionFactory(new ProviderConfiguration(), sqsClientBuilder);
	}

	@Bean
	DefaultMessageHandlerMethodFactory jmsMessageHandlerMethodFactory() {
		return new DefaultMessageHandlerMethodFactory();
	}

}
