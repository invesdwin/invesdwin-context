package de.invesdwin.context.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.stereotype.Service;

@Service
public interface IIntegrationTestService {

    String HELLO_WORLD_REQUEST_CHANNEL = "helloWorld";
    String HELLO_WORLD_MIT_ANSWER_REQUEST_CHANNEL = "helloWorldWithAnswer";

    @Gateway(requestChannel = HELLO_WORLD_REQUEST_CHANNEL)
    void helloWorld(String request);

    @Gateway(requestChannel = HELLO_WORLD_MIT_ANSWER_REQUEST_CHANNEL)
    String helloWorldWithAnswer(String request);

}
