package de.qaware.rookie.cloud.grpc.client;

import de.qaware.rookie.cloud.grpc.proto.Feature;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.samplers.Samplers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainClient.class);

    public static void main(String[] args) {

        //Configure tracing
        ZipkinTraceExporter.createAndRegister("http://localhost:9411/api/v2/spans", "client::tracing-to-zipkin-service");

        // For demo purposes, always sample
        TraceConfig traceConfig = Tracing.getTraceConfig();
        traceConfig.updateActiveTraceParams(
                traceConfig.getActiveTraceParams()
                        .toBuilder()
                        .setSampler(Samplers.alwaysSample())
                        .build());

        // Registers logging trace exporter.
        LoggingTraceExporter.register();

        GRPCClient client = new GRPCClient("localhost", 8612);
        Feature feature = client.getFeature();

        LOGGER.info("Feature '{}'", feature);

        Tracing.getExportComponent().shutdown();
    }
}
