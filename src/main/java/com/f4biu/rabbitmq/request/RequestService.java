package com.f4biu.rabbitmq.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    @Autowired
    private RabbitStreamTemplate rabbitStreamTemplate;

    private static Logger log = LoggerFactory.getLogger(RequestService.class);

    public void publisher(Request request) {
        log.info("Publisher new request: {}", request);
        rabbitStreamTemplate.convertAndSend(request);
    }
}