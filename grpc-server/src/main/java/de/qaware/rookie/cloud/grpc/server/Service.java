package de.qaware.rookie.cloud.grpc.server;


import de.qaware.rookie.cloud.grpc.proto.Feature;
import de.qaware.rookie.cloud.grpc.proto.Point;
import de.qaware.rookie.cloud.grpc.proto.RouteGuideGrpc;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;

public class Service extends RouteGuideGrpc.RouteGuideImplBase {

    private static final Tracer tracer = Tracing.getTracer();


    @Override
    public void getFeature(Point location, StreamObserver<Feature> responseObserver) {
        Scope ss = tracer.spanBuilder("service.getFeature").startScopedSpan();

        try {
            responseObserver.onNext(checkFeature(location));
            responseObserver.onCompleted();
        } finally {
            ss.close();
        }

    }

    private Feature checkFeature(Point location) {
        return Feature.newBuilder().setName("").setLocation(location).build();
    }


}
