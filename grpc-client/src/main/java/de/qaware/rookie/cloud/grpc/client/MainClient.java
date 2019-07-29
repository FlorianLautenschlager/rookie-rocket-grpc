package de.qaware.rookie.cloud.grpc.client;

import de.qaware.rookie.cloud.grpc.proto.dto.HelloReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainClient.class);

    public static void main(String[] args) {
        GRPCClient client = new GRPCClient("localhost", 8612);

        LOGGER.info("Staring server RPC :-)");

        client.sayHelloAsync(helloReply -> LOGGER.info("Async HelloRequest '{}'", helloReply));

        HelloReply blockingHello = client.sayHelloBlocking();
        LOGGER.info("Blocking HelloRequest '{}'", blockingHello);
    }
}
