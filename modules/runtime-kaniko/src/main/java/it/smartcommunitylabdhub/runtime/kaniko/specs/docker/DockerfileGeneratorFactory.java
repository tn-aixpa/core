package it.smartcommunitylabdhub.runtime.kaniko.specs.docker;

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

    private DockerfileGenerator generator;

    private DockerfileGeneratorFactory() {
        generator = new DockerfileGenerator();
    }

    public static DockerfileGeneratorFactory newInstance() {
        return new DockerfileGeneratorFactory();
    }

    public DockerfileGeneratorFactory from(String baseImage) {
        generator.addInstruction("baseImage", "FROM " + baseImage);
        return this;
    }

    public DockerfileGeneratorFactory run(String run) {
        generator.addInstruction("run", "RUN " + run);
        return this;
    }

    public DockerfileGeneratorFactory entrypoint(List<String> entrypoint) {
        generator.addInstruction("entrypoint", "ENTRYPOINT [" +
                entrypoint
                        .stream()
                        .map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))
                + "]");
        return this;
    }

    public DockerfileGeneratorFactory cmd(List<String> command) {
        generator.addInstruction("cmd", "CMD [" +
                command
                        .stream()
                        .map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))
                + "]");
        return this;
    }

    public DockerfileGeneratorFactory label(String label) {
        generator.addInstruction("label", "LABEL " + label);
        return this;
    }

    public DockerfileGeneratorFactory copy(String source, String destination) {
        generator.addInstruction("copy", "COPY " + source + " " + destination);
        return this;
    }

    public DockerfileGeneratorFactory expose(String port) {
        generator.addInstruction("expose", "EXPOSE " + port);
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
