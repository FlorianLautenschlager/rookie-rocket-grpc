package de.qaware.rookie.cloud.grpc.client;

import de.qaware.rookie.cloud.grpc.proto.dto.HelloReply;
import de.qaware.rookie.cloud.grpc.proto.dto.HelloRequest;
import de.qaware.rookie.cloud.grpc.proto.HelloServiceGrpc;
import de.qaware.rookie.cloud.grpc.proto.ValidationError;
import de.qaware.rookie.cloud.grpc.server.ErrorKey;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


class GRPCClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GRPCClient.class);

    private final HelloServiceGrpc.HelloServiceBlockingStub blockingStub;
    private final HelloServiceGrpc.HelloServiceStub asyncStub;

    private static final Tracer tracer = Tracing.getTracer();


    GRPCClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }


    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    private GRPCClient(ManagedChannelBuilder<?> channelBuilder) {
        ManagedChannel channel = channelBuilder.build();
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
        asyncStub = HelloServiceGrpc.newStub(channel);
    }

    /**
     * Class say hello on the blocking stub
     *
     * @return {@link HelloReply}
     */
    HelloReply sayHelloBlocking() {

        Scope ss = tracer.spanBuilder("GRPCClient.sayHelloBlocking").startScopedSpan();


        HelloRequest request = HelloRequest.newBuilder()
                .setUser("Leander")
                .build();

        HelloReply feature = null;
        try {
            feature = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            Metadata trailers = Status.trailersFromThrowable(e);
            ValidationError validationError = trailers.get(ErrorKey.VALIDATION_ERROR_KEY);
            LOGGER.error("RPC failed with status '{}' and validation error '{}'", e.getStatus(), validationError);
        } finally {
            ss.close();
        }

        return feature;
    }

    /**
     * Class say hello on the async stub
     *
     * @return {@link HelloReply}
     */
    public void sayHelloAsync(Consumer<HelloReply> consumer) {

        Scope ss = tracer.spanBuilder("GRPCClient.sayHelloAsync").startScopedSpan();

        HelloRequest request = HelloRequest.newBuilder()
                .setUser("Felix")
                .build();

        FeatureStreamObserver streamObserver = new FeatureStreamObserver(consumer);
        try {
            asyncStub.sayHello(request, streamObserver);

            // Mark the end of requests
            streamObserver.onCompleted();

            // Receiving happens asynchronously
            streamObserver.finishLatch.await(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            streamObserver.onError(e);
            LOGGER.error("Exception", e);

        } finally {
            ss.close();
        }
    }

    static class FeatureStreamObserver implements StreamObserver<HelloReply> {

        private final Consumer<HelloReply> consumer;
        final CountDownLatch finishLatch;


        FeatureStreamObserver(Consumer<HelloReply> consumer) {
            this.consumer = consumer;
            this.finishLatch = new CountDownLatch(1);

        }

        @Override
        public void onNext(HelloReply value) {
            consumer.accept(value);
        }

        @Override
        public void onError(Throwable t) {
            Status status = Status.fromThrowable(t);
            LOGGER.error("sayHelloAsync Failed: {}", status);
            finishLatch.countDown();
        }

        @Override
        public void onCompleted() {
            LOGGER.info("Finished sayHelloAsync");
            finishLatch.countDown();
        }
    }
}
