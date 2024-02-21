FROM maven:3-eclipse-temurin-21-alpine AS build
COPY application /tmp/application
COPY modules /tmp/modules
COPY pom.xml /tmp/pom.xml
WORKDIR /tmp
RUN --mount=type=cache,target=/root/.m2,source=/root/.m2,from=ghcr.io/scc-digitalhub/digitalhub-core:cache \ 
    mvn package -DskipTests

FROM gcr.io/distroless/java21-debian12:nonroot
ENV APP=core.jar
LABEL org.opencontainers.image.source=https://github.com/scc-digitalhub/digitalhub-core
COPY --from=build /tmp/application/target/*.jar /app/${APP}
EXPOSE 8080
CMD ["/app/core.jar"]