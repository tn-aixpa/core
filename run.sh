#!/bin/bash

# Change directory to the parent directory of modules
cd modules || exit 1

# Build each module, skipping tests
for module in commons fsm framework-k8s runtime-container runtime-dbt runtime-mlrun runtime-nefertem; do
    echo "Building module: $module"
    cd "$module" || exit 1
    mvn clean install -DskipTests
    cd ..
done

cd .. || exit 1

# Go back to the application folder and build it
cd application || exit 1
echo "Building application module"
mvn clean install -DskipTests

# Go back to the root folder and build it
cd .. || exit 1
echo "Building root project"
mvn clean install -DskipTests

# Run the application
cd application || exit 1
echo "Running Spring Boot application"
mvn spring-boot:run
