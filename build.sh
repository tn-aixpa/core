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


# Build Root Project
echo "Building project"
./mvnw clean install -DskipTests
