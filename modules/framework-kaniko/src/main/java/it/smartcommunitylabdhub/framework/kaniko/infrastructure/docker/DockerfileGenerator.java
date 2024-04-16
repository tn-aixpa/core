package it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * DockerfileGenerator is a utility class for programmatically generating Dockerfiles.
 * It allows users to specify Dockerfile directives and instructions, set configurations such as
 * user, working directory, and health checks, and generate the Dockerfile content either directly
 * or by using a template.
 * <p>
 * Example usage:
 * <pre>{@code
 * DockerfileGenerator dockerfileGenerator = new DockerfileGenerator();
 * dockerfileGenerator.addDirective("ARG VERSION=latest\n");
 * dockerfileGenerator.addInstruction("baseImage", "FROM openjdk:11");
 * dockerfileGenerator.addInstruction("dependency", "RUN apt-get update && apt-get install -y git");
 * dockerfileGenerator.addInstruction("environmentVariable", "ENV JAVA_HOME=/usr/local/openjdk-11");
 * dockerfileGenerator.addInstruction("command", "RUN git clone https://github.com/example/repo.git");
 * dockerfileGenerator.addInstruction("command", "RUN cd repo && mvn clean package");
 * dockerfileGenerator.addInstruction("copiedFile", "COPY target/*.jar /app/app.jar");
 * dockerfileGenerator.addInstruction("exposedPort", "EXPOSE 8080");
 * dockerfileGenerator.setUser("myuser");
 * dockerfileGenerator.setWorkdir("/app");
 * dockerfileGenerator.setHealthCheck("--interval=30s --timeout=30s CMD curl -f http://localhost/ || exit 1");
 * <p>
 * String dockerfileContent = dockerfileGenerator.generateDockerfile();
 * System.out.println("Generated Dockerfile:\n" + dockerfileContent);
 * <p>
 * dockerfileGenerator.writeToFile("Dockerfile");
 * System.out.println("Dockerfile written to file.");
 * }</pre>
 * <p>
 * To use a Dockerfile template, provide the path to the template file when generating the Dockerfile:
 * <pre>{@code
 * String templatePath = "path/to/template/Dockerfile.template";
 * String dockerfileContent = dockerfileGenerator.generateDockerfileFromTemplate(templatePath);
 * }</pre>
 */

@Slf4j
public class DockerfileGenerator {

    private final Map<DockerfileInstruction, List<String>> instructions = new LinkedHashMap<>();
    private final List<String> directives = new ArrayList<>();

    @Getter
    @Setter
    private String user;

    @Getter
    @Setter
    private String workdir;

    @Getter
    @Setter
    private String healthCheck;


    /**
     * Appends an instruction of the given type.
     *
     * @param type        The type of instruction.
     * @param instruction The instruction to append.
     */
    public void addInstruction(DockerfileInstruction type, String instruction) {
        instructions.computeIfAbsent(type, k -> new ArrayList<>()).add(instruction);
    }

    /**
     * Appends a Dockerfile directive.
     *
     * @param directive The Dockerfile directive to append.
     */
    public void addDirective(String directive) {
        directives.add(directive);
    }

    /**
     * Generates the Dockerfile content based on the configured properties.
     *
     * @return The generated Dockerfile content as a string.
     */
    public String generateDockerfile() {
        StringBuilder dockerfileContent = new StringBuilder();

        directives.forEach(dockerfileContent::append);

        instructions.forEach((type, instructionList) -> {
            instructionList.forEach(instruction -> {
                dockerfileContent.append(instruction).append("\n");
            });
        });

        if (user != null) {
            dockerfileContent.append("USER ").append(user).append("\n");
        }

        if (workdir != null) {
            dockerfileContent.append("WORKDIR ").append(workdir).append("\n");
        }

        if (healthCheck != null) {
            dockerfileContent.append("HEALTHCHECK ").append(healthCheck).append("\n");
        }

        return dockerfileContent.toString();
    }

    /**
     * Writes the generated Dockerfile content to a file.
     *
     * @param filePath The path to the file where the Dockerfile will be written.
     */
    public void writeToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(generateDockerfile());
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing dockerfile", e);
        }
    }
}
