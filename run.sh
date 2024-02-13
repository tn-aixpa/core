#!/bin/bash


cd modules || exit 1

# Build each modules
for module in commons fsm framework-k8s runtime-container runtime-dbt runtime-mlrun runtime-nefertem; do
    echo "Building module: $module"
    cd "$module" || exit 1
    mvn clean install -DskipTests
    cd ..
done

cd .. || exit 1

# Build Application Module
cd application || exit 1
echo "Building application module"
mvn clean install -DskipTests

# Build Root Project
cd .. || exit 1
echo "Building root project"
mvn clean install -DskipTests

# Run the application
cd application || exit 1
echo "Running Spring Boot application"
mvn spring-boot:run  # -Dspring-boot.run.profiles=local (to pass profile)
