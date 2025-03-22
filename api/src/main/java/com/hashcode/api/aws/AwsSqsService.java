package com.hashcode.api.aws;

import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.CodeConsumerVertical;
import com.hashcode.enums.AppMode;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AwsSqsService {

	private static final Logger logger = LogManager.getLogger(AwsSqsService.class);
	private SqsClient sqsClient;

	private Map<CodeConsumerVertical, String> queueUrlMap;

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.secret.access.key}")
	private String awsSecretAccessKey;

	@PostConstruct
	private void init() {
		AwsCredentialsProvider credentialsProvider = Constants.APP_MODE.equals(AppMode.DEVELOPMENT)
				? StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretAccessKey))
				: DefaultCredentialsProvider.create();

		this.sqsClient = SqsClient.builder().region(Region.of(Constants.AWS_REGION))
				.credentialsProvider(credentialsProvider).build();
		this.queueUrlMap = new HashMap<>();
		for (CodeConsumerVertical vertical : CodeConsumerVertical.values()) {
			String queueName = vertical.getQueueName() + "-" + Constants.APP_MODE.toString().toLowerCase();
			logger.info(queueName);
			try {
				GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
				String queueUrl = sqsClient.getQueueUrl(request).queueUrl();
				logger.info("Successfully fetched Queue for vertical : {}, queueName : {}, queueUrl : {}", vertical,
						queueName, queueUrl);
				this.queueUrlMap.put(vertical, queueUrl);
			} catch (Exception e) {
				logger.warn(Constants.KEY_EXCEPTION, e);
			}
		}
	}

	public void sendSqsMessage(CodeConsumerMessage message) {
		try {
			if (sqsClient == null) {
				init();
			}
			message.setQueueInsertTime(System.currentTimeMillis());
			SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
					.queueUrl(queueUrlMap.get(message.getTaskType().getVertical()))
					.messageBody(CommonUtilities.getObjectMapper().writeValueAsString(message)).build();
			sqsClient.sendMessage(sendMsgRequest);
			logger.info("Sqs Message successfully Pushed");

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

}