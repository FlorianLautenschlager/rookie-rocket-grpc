package de.qaware.rookie.cloud.grpc.client;

import de.qaware.rookie.cloud.grpc.diagnosability.DiagnosabilityControl;
import de.qaware.rookie.cloud.grpc.proto.dto.HelloReply;
import io.opencensus.trace.samplers.Samplers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainClient.class);

    public static void main(String[] args) throws InterruptedException {

        boolean daemonized = args.length > 0 && args[0].equals("--daemon");

        String zipkin_server = System.getenv("ZIPKIN_SERVER");
        if (zipkin_server == null) zipkin_server = "http://localhost:9411/api/v2/spans";

        String grpc_server = System.getenv("GRPC_SERVER");
        if (grpc_server == null) grpc_server = "localhost";

        int grpc_port = 8612;
        String grpc_port_str = System.getenv("GRPC_PORT");
        if (grpc_port_str != null) {
            grpc_port = Integer.parseInt(grpc_port_str);
        }


        DiagnosabilityControl.enableTracing(
                "main-client",
                zipkin_server,
                Samplers.alwaysSample(),
                false
        );

        GRPCClient client = new GRPCClient(grpc_server, grpc_port);

        LOGGER.info("Staring server RPC :-)");

        do {
            client.sayHelloAsync(helloReply -> LOGGER.info("Async HelloRequest '{}'", helloReply));

            HelloReply blockingHello = client.sayHelloBlocking();
            LOGGER.info("Blocking HelloRequest '{}'", blockingHello);
            if (daemonized) {
                Thread.sleep(3000);
            }
        } while (daemonized);
    }
}
