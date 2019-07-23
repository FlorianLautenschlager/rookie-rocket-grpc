package de.qaware.rookie.cloud.grpc;

import de.qaware.rookie.cloud.grpc.proto.Feature;
import de.qaware.rookie.cloud.grpc.proto.Point;
import de.qaware.rookie.cloud.grpc.proto.RouteGuideGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GRPCClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GRPCClient.class);

    private final ManagedChannel channel;
    private final RouteGuideGrpc.RouteGuideBlockingStub blockingStub;
    private final RouteGuideGrpc.RouteGuideStub asyncStub;

    public GRPCClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }


    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public GRPCClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = RouteGuideGrpc.newBlockingStub(channel);
        asyncStub = RouteGuideGrpc.newStub(channel);
    }


    public Feature getFeature() {
        Point request = Point.newBuilder()
                .setLatitude(10)
                .setLongitude(11)
                .build();

        Feature feature = null;
        try {
            feature = blockingStub.getFeature(request);
        } catch (StatusRuntimeException e) {
            LOGGER.error("RPC failed:  {}", e.getStatus());
        }

        return feature;
    }
}
