package de.qaware.rookie.cloud.grpc.server;


import de.qaware.rookie.cloud.grpc.proto.dto.HelloReply;
import de.qaware.rookie.cloud.grpc.proto.dto.HelloRequest;
import de.qaware.rookie.cloud.grpc.proto.HelloServiceGrpc;
import de.qaware.rookie.cloud.grpc.proto.ValidationError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service extends HelloServiceGrpc.HelloServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private static final Tracer tracer = Tracing.getTracer();

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

        try (Scope ss = tracer.spanBuilder("service.getFeature").startScopedSpan()) {
            LOGGER.info("Received request");
            responseObserver.onNext(HelloReply.newBuilder()
                    .setRequest(request)
                    .setSalutation("Hello '" + request.getUser() + "'. Yeah you are awesome.")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            metadata.put(ErrorKey.VALIDATION_ERROR_KEY, ValidationError.newBuilder().setMessage("Username not allowed").build());
            responseObserver.onError(Status.INTERNAL.withDescription("detailed error description").asRuntimeException(metadata));
        }
    }
}
