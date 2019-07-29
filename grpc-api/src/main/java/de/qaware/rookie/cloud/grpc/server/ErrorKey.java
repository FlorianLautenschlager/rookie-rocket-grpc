package de.qaware.rookie.cloud.grpc.server;

import de.qaware.rookie.cloud.grpc.proto.ValidationError;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;

public class ErrorKey {

    public static final Metadata.Key<ValidationError> VALIDATION_ERROR_KEY = ProtoUtils.keyForProto(ValidationError.getDefaultInstance());

    private ErrorKey() {
        //avoid instance
    }

}
