
package com.hashcode.codeconsumer.config;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.hashcode.codeconsumer.vertical.AbstractCodeConsumerVertical;
import com.hashcode.utils.Constants;

@Configuration
@EnableJms
public class JmsListnerEndpointConfigurer implements JmsListenerConfigurer {

	private static final Logger logger = LogManager.getLogger(JmsConfig.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory;

	@Autowired
	private DefaultMessageHandlerMethodFactory jmsMessageHandlerMethodFactory;

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		try {
			registrar.setContainerFactory(defaultJmsListenerContainerFactory);
			Reflections reflections = new Reflections(new ConfigurationBuilder().setExpandSuperTypes(false)
					.filterInputsBy(new FilterBuilder().includePackage("com.hashcode")).forPackage("com.hashcode")
					.setScanners(Scanners.SubTypes, Scanners.TypesAnnotated));

			for (Class<?> providerClass : reflections.getSubTypesOf(AbstractCodeConsumerVertical.class)) {
				logger.info("Sub type class Found : {}", providerClass);
				Method methodHandlerForProcessMsg = getMethodFromClassOrSuperClass(providerClass, "processMsg");
				AbstractCodeConsumerVertical verticalBean = (AbstractCodeConsumerVertical) context
						.getBean(providerClass);
				// register beans as endpoint
				String queueName = verticalBean.getVertical().getQueueName() + "-"
						+ Constants.APP_MODE.toString().toLowerCase();
				logger.info("Configuring Vertical : {}", verticalBean.getVertical());
				MethodJmsListenerEndpoint endpoint = new MethodJmsListenerEndpoint();
				endpoint.setId(providerClass.getName());
				endpoint.setBean(verticalBean);
				endpoint.setMethod(methodHandlerForProcessMsg);
				endpoint.setConcurrency(verticalBean.getConcurrencyString());
				endpoint.setDestination(queueName);
				endpoint.setSubscription(queueName);
				endpoint.setMessageHandlerMethodFactory(jmsMessageHandlerMethodFactory);
				registrar.registerEndpoint(endpoint);
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public Method getMethodFromClassOrSuperClass(Class<?> providerClass, String methodName) {
		if (providerClass == null || methodName == null) {
			return null;
		}
		try {
			int iterations = 0;
			while (providerClass != null) {
				iterations++;
				for (Method method : providerClass.getDeclaredMethods()) {
					if (method.getName().equals(methodName)) {
						logger.info("Total Iterations to get the method : {}", iterations);
						return method;
					}
				}
				providerClass = providerClass.getSuperclass();
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}

}