package tech.shooting.ipsc.config;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.settings.RabbitMqConstants;
import tech.shooting.ipsc.settings.RabbitmqSettings;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
@Slf4j
public class RabbitConfig implements RabbitListenerConfigurer {

    @Autowired
    public RabbitmqSettings rabbitSettings;


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        log.info("Creating a rabbit listener factory");
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConcurrentConsumers(rabbitSettings.getMinConsumers());
//        factory.setMaxConcurrentConsumers(rabbitSettings.getMaxConsumers());
//	    factory.setConsecutiveActiveTrigger(1);
//	    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//	    factory.setErrorHandler(new RabbitErrorHandler());
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }

    @Bean
    public Queue ipscQueue() {
        return new Queue(rabbitSettings.getIpscQueueName(), true, false, false);
    }

    @Bean
    TopicExchange ipscExchange() {
        return new TopicExchange(RabbitMqConstants.ipscExchangeName);
    }

    @Bean
    Binding fimsBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitSettings.getIpscQueueName());
    }


    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }
    
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(JacksonUtils.getMapper());
        return converter;
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

}
