package it.smartcommunitylabdhub.runtime.container.docker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating instances of DockerfileGenerator to programmatically generate Dockerfiles.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a DockerfileGenerator instance
 * DockerfileGenerator dockerfileGenerator = DockerfileGeneratorFactory.newInstance()
 *     .from("openjdk:11")
 *     .run("apt-get update && apt-get install -y git")
 *     .cmd("java -jar app.jar")
 *     .label("maintainer=\"John Doe <john@example.com>\"")
 *     .copy("target/*.jar", "/app/app.jar")
 *     .expose("8080")
 *     .user("myuser")
 *     .workdir("/app")
 *     .healthcheck("--interval=30s --timeout=30s CMD curl -f http://localhost/ || exit 1")
 *     .addDirective("ARG VERSION=latest\n")
 *     .build();
 * <p>
 * // Generate Dockerfile content
 * String dockerfileContent = dockerfileGenerator.generateDockerfile();
 * System.out.println("Generated Dockerfile:\n" + dockerfileContent);
 * <p>
 * // Write Dockerfile to file
 * dockerfileGenerator.writeToFile("Dockerfile");
 * System.out.println("Dockerfile written to file.");
 * }</pre>
 */
public class DockerfileGeneratorFactory {

    private final DockerfileGenerator generator;

    private DockerfileGeneratorFactory() {
        generator = new DockerfileGenerator();
    }

    public static DockerfileGeneratorFactory newInstance() {
        return new DockerfileGeneratorFactory();
    }

    public DockerfileGeneratorFactory from(String baseImage) {
        generator.addInstruction(DockerfileInstruction.FROM, "FROM " + baseImage);
        return this;
    }

    public DockerfileGeneratorFactory run(String run) {
        generator.addInstruction(DockerfileInstruction.RUN, "RUN " + run);
        return this;
    }

    public DockerfileGeneratorFactory entrypoint(List<String> entrypoint) {
        generator.addInstruction(DockerfileInstruction.ENTRYPOINT, "ENTRYPOINT [" +
                entrypoint
                        .stream()
                        .map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))
                + "]");
        return this;
    }

    public DockerfileGeneratorFactory cmd(List<String> command) {
        generator.addInstruction(DockerfileInstruction.CMD, "CMD [" +
                command
                        .stream()
                        .map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))
                + "]");
        return this;
    }

    public DockerfileGeneratorFactory label(String label) {
        generator.addInstruction(DockerfileInstruction.LABEL, "LABEL " + label);
        return this;
    }

    public DockerfileGeneratorFactory copy(String source, String destination) {
        generator.addInstruction(DockerfileInstruction.COPY, "COPY " + source + " " + destination);
        return this;
    }

    public DockerfileGeneratorFactory expose(String port) {
        generator.addInstruction(DockerfileInstruction.EXPOSE, "EXPOSE " + port);
        return this;
    }

    public DockerfileGeneratorFactory addDirective(String directive) {
        generator.addDirective(directive);
        return this;
    }

    public DockerfileGeneratorFactory user(String user) {
        generator.setUser(user);
        return this;
    }

    public DockerfileGeneratorFactory workdir(String workdir) {
        generator.setWorkdir(workdir);
        return this;
    }

    public DockerfileGeneratorFactory healthcheck(String healthCheck) {
        generator.setHealthCheck(healthCheck);
        return this;
    }

    public DockerfileGenerator build() {
        return generator;
    }
}
