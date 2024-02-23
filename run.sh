#!/bin/bash
MIN_VERSION=21
CUR_VERSION=`java -version 2>&1 | grep 'version' 2>&1 | awk -F\" '{ split($2,a,"."); print a[1]}'`

if [[ "$CUR_VERSION" ]]; then
    echo "Detected java version $CUR_VERSION."
    if [[ "$CUR_VERSION" -lt "$MIN_VERSION" ]]; then
        echo "Required java version is $MIN_VERSION. Exit."
        exit
    fi
else
    echo "Missing java. Exit"
    exit
fi

PROFILE=$1
if [[ $# == 0 ]]; then
    PROFILE="default"
fi

# cd modules || exit 1

# # Build each modules
# for module in commons fsm framework-k8s runtime-container runtime-dbt runtime-mlrun runtime-nefertem; do
#     echo "Building module: $module"
#     cd "$module" || exit 1
#     mvn clean install -DskipTests
#     cd ..
# done

# cd .. || exit 1

# # Build Application Module
# cd application || exit 1
# echo "Building application module"
# mvn clean install -DskipTests
# cd .. || exit 1

# Build Root Project
echo "Building project"
./mvnw clean install -DskipTests

# Run the application
echo "Running Spring Boot application with profile $PROFILE..."
./mvnw spring-boot:run -pl application  -Dspring-boot.run.profiles=$PROFILE
