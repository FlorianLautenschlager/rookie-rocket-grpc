package de.qaware.rookie.cloud.grpc.server;


import de.qaware.rookie.cloud.grpc.proto.Feature;
import de.qaware.rookie.cloud.grpc.proto.Point;
import de.qaware.rookie.cloud.grpc.proto.RouteGuideGrpc;
import io.grpc.stub.StreamObserver;

public class Service extends RouteGuideGrpc.RouteGuideImplBase {

    @Override
    public void getFeature(Point location, StreamObserver<Feature> responseObserver) {
        responseObserver.onNext(checkFeature(location));
        responseObserver.onCompleted();
    }

    private Feature checkFeature(Point location) {
        return Feature.newBuilder().setName("").setLocation(location).build();
    }


}
