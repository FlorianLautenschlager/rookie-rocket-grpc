package de.qaware.rookie.cloud.grpc.server;

import de.qaware.rookie.cloud.grpc.diagnosability.DiagnosabilityControl;
import io.opencensus.trace.samplers.Samplers;

public class MainServer {

    public static void main(String[] args) throws Exception {

        DiagnosabilityControl.enableTracing(
                "main-server",
                "http://localhost:9411/api/v2/spans",
                Samplers.alwaysSample(),
                false
        );

        DiagnosabilityControl.enablePrometheusEndpoint(
                8080,
                true
        );

        GRPCServer server = new GRPCServer(8612);
        server.start();
        server.blockUntilShutdown();
    }


}
