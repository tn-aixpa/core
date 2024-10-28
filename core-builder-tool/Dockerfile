# Use a base image with necessary tools
FROM alpine:latest

ARG USER=builder
ARG USER_ID=1000
ARG USER_GROUP=builder
ARG USER_GROUP_ID=1000

# Install dependencies
RUN apk update && \
    apk add --no-cache \
        rsync \
        git \
        curl \
        bash \
        unzip

# Install MinIO client (mc)
RUN curl -sL https://dl.min.io/client/mc/release/linux-amd64/mc -o /usr/bin/mc && \
    chmod +x /usr/bin/mc

# create user
RUN addgroup -g ${USER_GROUP_ID} ${USER_GROUP} && adduser -D -u ${USER_ID} -G ${USER_GROUP} ${USER}

# Set the working directory
WORKDIR /app

# Copy your bash script and context-refs.txt into the Docker container
COPY builder-tool.sh .

# Make the bash script executable
RUN chmod +x builder-tool.sh

USER ${USER_GROUP_ID}
