# rookie-rocket-grpc

## build

````bash
gradlew clean build
````

## Run with log correlation
```
java -Dlog4j2.ContextDataInjector="io.opencensus.contrib.logcorrelation.log4j2.OpenCensusTraceContextDataInjector" ...
```
