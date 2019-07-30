package de.qaware.rookie.cloud.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class GRPCServer {

    private final Logger LOGGER = LoggerFactory.getLogger(GRPCServer.class);

    private final Server server;
    private final int port;

    GRPCServer(int port) {
        this.port = port;
        server = ServerBuilder.forPort(port)
                .addService(new GRPCService())
                .build();
    }


    /**
     * Start serving requests.
     */
    void start() throws IOException {
        LOGGER.info("*** starting server on port '{}'", port);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            LOGGER.error("*** shutting down gRPC server since JVM is shutting down");
            GRPCServer.this.stop();
            LOGGER.error("*** server shut down");
        }));
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


}
