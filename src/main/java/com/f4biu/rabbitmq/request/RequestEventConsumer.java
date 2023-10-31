package com.f4biu.rabbitmq.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
class RequestEventConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private Queue streamPartition;

    private static Logger log = LoggerFactory.getLogger(RequestEventConsumer.class);

    @PostConstruct
    void init() {
        rabbitAdmin.declareQueue(streamPartition);
    }

    @Retryable(interceptor = "streamRetryOperationsInterceptorFactoryBean")
    @RabbitListener(
            queues = "#{streamPartition.name}",
            containerFactory = "streamContainerFactory"
    )
    void onConsumer(Message in, MessageHandler.Context context) throws IOException {
        log.info("Stream partition message offset: {}", context.offset());

        var request = objectMapper.readValue(in.getBodyAsBinary(), Request.class);

        log.info("Consumer message: {}", request);

        context.storeOffset();
    }
}