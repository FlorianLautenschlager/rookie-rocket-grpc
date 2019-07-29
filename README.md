# rookie-rocket-grpc

A small repository with a GRPC server and a client. In addition to the typical GRPC code, there is also observability code that exports metrics, distributed traces, and JSON logs.

## Structure

### grpc-api
The grpc-api package contains the observability-code and the service description (Protocol Buffers).

#### Observability
The class `de.qaware.rookie.cloud.grpc.diagnosability.DiagnosabilityControl` contains two methods:

1) `enableTracing`: to activate distributed tracing using OpenCensus and send them to a Zipkin-Server.
2) `enablePrometheusEndpoint`: that registers all RPCViews exports grpc-metrics in the Prometheus format.

The package also defines a logging configuration for log4j2. The logging configuration uses a JSON appender and writes the fields:
- `timestamp`: Timestamp with milliseconds
- `log-event`: Actual log message
- `traceId`: Unique trace identifier
- `spanId`: Unique span id within a trace
- `hostname`: On which the service runs

Path to the logging configuration: `rookie-rocket-grpc/grpc-api/src/main/resources/log4j2.xml`

**Note:** One must set the system property `-Dlog4j2.ContextDataInjector="io.opencensus.contrib.logcorrelation.log4j2.OpenCensusTraceContextDataInjector"` in order to enable the log correlation based on the traceId and spanId. 

#### GRPC Protocol Buffers
All Protocol Buffers files are located in the default directory of the Gradle Protocol Buffer plugin in `rookie-rocket-grpc/grpc-api/src/main/proto`.
The directory contains the following files:
- `server_errors.proto`: Defines a custom server error to show how on can use the richer error model of GRPC.
- `service_dtos.proto`: Defines the data transfer objects used by the `HelloProto` service. They are defined in separate file to show the ability to `import` other *.proto files.
- `service.proto`: Defines the `HelloProto` service with a single method. 

The class `de.qaware.rookie.cloud.grpc.server.ErrorKey` defines the error key for the custom server error which is used on the client and the server side.

### grpc-client
The client package implements a simple client and a main class that call the server. The server is called once blocking and once asynchronously.
See the classes: 
- `de.qaware.rookie.cloud.grpc.client.GRPCClient` and
- `de.qaware.rookie.cloud.grpc.client.MainClient`.

### grpc-server
The server package implements the server part of GRPC. To do this, it implements the service, inserts it into a GRPC server that is started with the help of the main class.
See the classes:
- `GRPCService` for the implementation of the GRPC Service
- `GRPCServer` for the implementation of the GRPC Server
- `MainClient` for putting everything together

## Activate the Observability code
Just copy and past the following code snippet into the `MainServer` and `MainClient` class.

```java
DiagnosabilityControl.enableTracing(
         "main-server",
         "http://localhost:9411/api/v2/spans",
         Samplers.alwaysSample(),
         false
);

DiagnosabilityControl.enablePrometheusEndpoint(
         8080,
         true
);
```

## Build the project
To build the project simple run the following command:
````bash
gradlew clean build
````

Everything (Protocol Buffers dependencies + protoc plugin, OpenCensus dependencies) are configured with the `build.gradle` files.
Also the resulting jars contains everything and can be executed by `java -jar ...`

**Note:** The project used Java version 12.

## Run with log correlation
```
java -Dlog4j2.ContextDataInjector="io.opencensus.contrib.logcorrelation.log4j2.OpenCensusTraceContextDataInjector" ...
```

## Zipkin
The `zipkin` directory contains a README.md that describes how to start the zipkin-server.