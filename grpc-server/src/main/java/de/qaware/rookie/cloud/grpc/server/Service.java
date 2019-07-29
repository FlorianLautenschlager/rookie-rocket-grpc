package de.qaware.rookie.cloud.grpc.server;


import de.qaware.rookie.cloud.grpc.proto.HelloReply;
import de.qaware.rookie.cloud.grpc.proto.HelloRequest;
import de.qaware.rookie.cloud.grpc.proto.HelloServiceGrpc;
import de.qaware.rookie.cloud.grpc.proto.ValidationError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;

public class Service extends HelloServiceGrpc.HelloServiceImplBase {

    private static final Tracer tracer = Tracing.getTracer();

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

        try (Scope ss = tracer.spanBuilder("service.getFeature").startScopedSpan()) {
            responseObserver.onNext(HelloReply.newBuilder()
                    .setRequest(request)
                    .setSalutation("Hello '" + request.getUser() + "'. Yeah are awesome.")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            metadata.put(ErrorKey.VALIDATION_ERROR_KEY, ValidationError.newBuilder().setMessage("Username not allowed").build());
            responseObserver.onError(Status.INTERNAL.withDescription("detailed error description").asRuntimeException(metadata));
        }
    }
}
