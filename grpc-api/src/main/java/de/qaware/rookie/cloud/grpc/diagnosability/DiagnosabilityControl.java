package de.qaware.rookie.cloud.grpc.diagnosability;

import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.Sampler;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

public class DiagnosabilityControl {

    private DiagnosabilityControl() {
        //avoid instance
    }

    /**
     * @param serviceName           within the zipkin server
     * @param zipkinUrl             that points to the zipkin server
     * @param sampler               to decide if a trace is send to the zipkin server or not
     * @param debugLogTraceExporter to activate the trace log exporter
     */
    public static void enableTracing(String serviceName, String zipkinUrl, Sampler sampler, boolean debugLogTraceExporter) {
        //Configure tracing
        ZipkinTraceExporter.createAndRegister(zipkinUrl, serviceName);

        // For demo purposes, always sample
        TraceConfig traceConfig = Tracing.getTraceConfig();
        traceConfig.updateActiveTraceParams(
                traceConfig.getActiveTraceParams()
                        .toBuilder()
                        .setSampler(sampler)
                        .build());

        // Registers logging trace exporter.
        if (debugLogTraceExporter) {
            LoggingTraceExporter.register();
        }

        //Add shutdown hook in order to shutdown the tracing component
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Tracing.getExportComponent().shutdown()));
    }

    /**
     * Register Prometheus exporters and export metrics to a Prometheus HTTPServer.
     *
     * @param port  on which the endpoint is exposed
     * @param demon true or false and indicates if the endpoint is a demon of the current thread
     * @throws IOException if something bad happen
     */
    public static void enablePrometheusEndpoint(int port, boolean demon) throws IOException {

        // Registers all RPC views. For demonstration all views are registered. You may want to
        // start with registering basic views and register other views as needed for your application.
        RpcViews.registerAllGrpcViews();

        PrometheusStatsCollector.createAndRegister();
        new HTTPServer(port, demon);
    }
}
