package de.qaware.rookie.cloud.grpc;

import de.qaware.rookie.cloud.grpc.proto.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainClient.class);

    public static void main(String[] args) {

        GRPCClient client = new GRPCClient("localhost", 8612);
        Feature feature = client.getFeature();

        LOGGER.info("Feature '{}'", feature);
    }
}
