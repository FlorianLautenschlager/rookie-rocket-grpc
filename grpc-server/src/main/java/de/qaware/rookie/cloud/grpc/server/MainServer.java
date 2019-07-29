package de.qaware.rookie.cloud.grpc.server;

public class MainServer {

    public static void main(String[] args) throws Exception {
        GRPCServer server = new GRPCServer(8612);
        server.start();
        server.blockUntilShutdown();
    }
}
