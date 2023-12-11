FROM maven:3.8.2-eclipse-temurin-17 AS build
COPY ./src /tmp/src
COPY ./pom.xml /tmp/pom.xml
WORKDIR /tmp
RUN --mount=type=cache,target=/root/.m2,source=/root/.m2,from=ghcr.io/scc-digitalhub/digitalhub-core:cache \ 
    mvn package -DskipTests

FROM gcr.io/distroless/java17-debian12:nonroot
ENV APP=core-0.0.8.jar
LABEL org.opencontainers.image.source=https://github.com/scc-digitalhub/digitalhub-core
COPY --from=build /tmp/target/*.jar /app/${APP}
EXPOSE 8080
CMD ["/app/core-0.0.3-SNAPSHOT.jar"]