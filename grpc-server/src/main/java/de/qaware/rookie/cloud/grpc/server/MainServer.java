package de.qaware.rookie.cloud.grpc.server;

import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.samplers.Samplers;
import io.prometheus.client.exporter.HTTPServer;

public class MainServer {

    public static void main(String[] args) throws Exception {
        // Registers all RPC views. For demonstration all views are registered. You may want to
        // start with registering basic views and register other views as needed for your application.
        RpcViews.registerAllGrpcViews();

        // Register Prometheus exporters and export metrics to a Prometheus HTTPServer.
        PrometheusStatsCollector.createAndRegister();
        new HTTPServer(8080, true);

        //Configure tracing
        ZipkinTraceExporter.createAndRegister("http://localhost:9411/api/v2/spans", "server::tracing-to-zipkin-service");

        // For demo purposes, always sample
        TraceConfig traceConfig = Tracing.getTraceConfig();
        traceConfig.updateActiveTraceParams(
                traceConfig.getActiveTraceParams()
                        .toBuilder()
                        .setSampler(Samplers.alwaysSample())
                        .build());

        // Registers logging trace exporter.
        //LoggingTraceExporter.register();


        GRPCServer server = new GRPCServer(8612);

        server.start();
        server.blockUntilShutdown();

        Tracing.getExportComponent().shutdown();
    }


}
