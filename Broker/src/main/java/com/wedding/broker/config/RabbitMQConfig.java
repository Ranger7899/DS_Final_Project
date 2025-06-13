package com.wedding.broker.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;


@Configuration
public class RabbitMQConfig {

    @Value("${broker.cancel.exchange}")   private String cancelExchange;
    @Value("${broker.cancel.queue}")      private String cancelQueue;
    @Value("${broker.cancel.routingKey}") private String cancelRoutingKey;


    @Value("${broker.order.exchange}")   private String orderExchange;
    @Value("${broker.order.queue}")      private String orderQueue;
    @Value("${broker.order.routingKey}") private String orderRoutingKey;

    // ── Main exchange & queue ────────────────────────────────────────────────
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderExchange, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(orderQueue)
                .withArgument("x-dead-letter-exchange", orderExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", orderRoutingKey + ".retry")
                .build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(orderRoutingKey);
    }

    // ── Retry queue (1-min delay) ────────────────────────────────────────────
    @Bean
    public DirectExchange dlx() {
        return new DirectExchange(orderExchange + ".dlx");
    }

    @Bean
    public Queue orderRetryQueue() {
        return QueueBuilder.durable(orderQueue + ".retry")
                .withArgument("x-message-ttl", 60_000)                // 1 min
                .withArgument("x-dead-letter-exchange", orderExchange)
                .withArgument("x-dead-letter-routing-key", orderRoutingKey)
                .build();
    }

    @Bean
    public Binding orderRetryBinding() {
        return BindingBuilder.bind(orderRetryQueue())
                .to(dlx())
                .with(orderRoutingKey + ".retry");
    }

    // ── Cancel exchange & queue ────────────────────────────────────────────────
    @Bean
    public DirectExchange cancelExchange() {
        return new DirectExchange(cancelExchange, true, false);
    }

    @Bean
    public Queue cancelQueue() {
        return QueueBuilder.durable(cancelQueue)
                .withArgument("x-dead-letter-exchange", cancelExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", cancelRoutingKey + ".retry")
                .build();
    }

    @Bean
    public Binding cancelBinding() {
        return BindingBuilder.bind(cancelQueue()).to(cancelExchange()).with(cancelRoutingKey);
    }

    // Retry queue (1-min delay)
    @Bean
    public DirectExchange cancelDlx() { return new DirectExchange(cancelExchange + ".dlx"); }

    @Bean
    public Queue cancelRetryQueue() {
        return QueueBuilder.durable(cancelQueue + ".retry")
                .withArgument("x-message-ttl", 60_000)
                .withArgument("x-dead-letter-exchange", cancelExchange)
                .withArgument("x-dead-letter-routing-key", cancelRoutingKey)
                .build();
    }

    @Bean
    public Binding cancelRetryBinding() {
        return BindingBuilder.bind(cancelRetryQueue())
                .to(cancelDlx())
                .with(cancelRoutingKey + ".retry");
    }

    /* ─────────────────── JSON message converter ─────────────────── */
    @Bean
    public MessageConverter jsonConverter(ObjectMapper mapper) {
        Jackson2JsonMessageConverter conv = new Jackson2JsonMessageConverter(mapper);
        conv.setAlwaysConvertToInferredType(true);   // so listener gets strong typing
        return conv;
    }

    /* ─────────────────── Customize RabbitTemplate ───────────────── */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter jsonConverter) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(jsonConverter);
        return tpl;
    }

    /* ── Make all @RabbitListener containers use JSON automatically ─ */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, MessageConverter jsonConverter) {

        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(jsonConverter);
        return f;
    }


}
