# DigitalHub CORE

Core is a Java-based project that integrates various technologies. Its primary purpose is to provide an interface for deploying and running serverless functions, run jobs, deploying services, create workflows and managing resources in Kubernetes.


## Description
DH Core enables management of resources and operations in a simple, scalable and production ready environment fully focused on developers.
It leverages data repositories, compute resources (Kubernetes) and serverless technologies to write, orchestrate and execute operations on data such as ingestion, validation, transformation, tracking etc.

### The Domain model
DH Core offers an API interface that give us the possibility to formalize the entities involved in the execution system as following:


1. **Projects** is the context in which all the object below are living.
2. **Functions (FN)** are the logical description of an executable. They are associated with a given runtime (RUNTIME), which implements the actual execution and determines which are the actions available. Examples are dbt, nuclio, mlrun, python,spark.
3. **Tasks (TASK)** are the logical representations of an action which is performed via a given function, by a runtime, within a framework. As such, they define both the actual scope of the execution and its context, in terms of environment, dependencies, resources etc.
4. **Runs (RUN)** are the representation of the execution of a given task with the given function, on (a set of) inputs to deliver (a set of) outputs. At a high level, they can be seen both as a summary of the union between a function and a task in a single instance, and as the representation of the actual execution phase, with parameters, status, results etc.

5. Dataitems
6. Artifacts
7. Models
8. Workflows

The project is essentially divided into the following parts:

1. Core
2. Modules
3. Frontend

Core manages all the basic elements that are the foundation of the project.
Modules include:

1. Common objects shared across the project and utilized by other modules.
2. K8s Framework defines our framework. We currently support Job, CronJob, Deployment, and Serve frameworks, as well as Monitor functionality for our running objects in Kubernetes.
3. The Kaniko module, integrated with Core, provides the capability to build images for running specific functions.
4. The Fsm module is the state machine integrated with Core that ensures a function run progresses correctly. This prevents a function's execution from ending in an invalid state.
5. OpenMetadata module....//todo
6. Runtimes modules. These runtimes are essentially integrations with external tools that enable the execution of serverless functions. Currently, we support the execution of functions.
   1. Dbt database transformation function
   2. Kfp workflow execution
   3. Mlrun function
   4. Nefertem function for data validation, inference and profiling
   5. Python function
   6. Container


## Configuration

You can locate the project configuration file in the **application [core]** module, named **_resources/application.yml_**. This file specifies configurations for various sections. 

Each section allows configurations to be written directly in the YAML file or through environment variables (ENV).

1. Server configuration
```yml
server:
  host: ${SERVER_HOST:localhost}
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: ${SERVER_CONTEXT:/}
  tomcat:
    remoteip:
      remote_ip_header: ${SERVER_TOMCAT_REMOTE_IP_HEADER:x-forwarded-for}
      protocol_header: ${SERVER_TOMCAT_PROTOCOL_HEADER:x-forwarded-proto}
  max-http-request-header-size: 32000
  error:
    include-stacktrace: never
```

2. SpringBoot configuration
```yml
spring:
  sql:
    init:
      schema-locations: classpath:db/schema-${spring.sql.init.platform}.sql
      mode: always
      platform: ${JDBC_PLATFORM:h2}
  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
  batch:
    jdbc:
      initialize-schema: always
  jpa:
    generate-ddl: true
    hibernate:
      ddl-auto: update
    # database-platform: ${JDBC_DIALECT:org.hibernate.dialect.H2Dialect}
  datasource:
    driverClassName: ${JDBC_DRIVER:org.h2.Driver}
    password: ${JDBC_PASS:password}
    username: ${JDBC_USER:sa}
    url: ${JDBC_URL:jdbc:h2:file:./data/db}

  h2:
    console:
      enabled: ${H2_CONSOLE:false}
```
3. Actuator configuration
```yml
management:
  server:
    port: ${MANAGEMENT_PORT:8081}
  endpoints:
    enabled-by-default: false
    web:
      base-path:
      exposure.include: "health,info,metrics"
  endpoint:
    info:
      enabled: true
    health:
      enabled: true
```
4. Runtimes supportati da Core
```yml
runtime:
  nefertem:
    image: ${RUNTIME_NEFERTEM_IMAGE:ghcr.io/scc-digitalhub/digitalhub-sdk/wrapper-nefertem:latest}
  dbt:
    image: ${RUNTIME_DBT_IMAGE:ghcr.io/scc-digitalhub/digitalhub-sdk/wrapper-dbt:latest}
  mlrun:
    image: ${RUNTIME_MLRUN_IMAGE:ghcr.io/scc-digitalhub/digitalhub-core-wrapper-mlrun:latest}
  kfp:
    image: ${RUNTIME_KFP_IMAGE:ghcr.io/scc-digitalhub/digitalhub-core-wrapper-kfp:latest}
  python:
    image: ${RUNTIME_PYTHON_IMAGE:ghcr.io/scc-digitalhub/digitalhub-serverless-python-3.9:latest}
    command: /usr/local/bin/processor
```
5. SpringDoc configuration
6. Kaniko configuration
```yml
kaniko:
  image: ${KANIKO_IMAGE:gcr.io/kaniko-project/executor:latest}
  init-image: ${KANIKO_INIT_IMAGE:ghcr.io/scc-digitalhub/digitalhub-core-builder-tool:latest}
  image-prefix: ${KANIKO_IMAGE_PREFIX:dhcore}
  image-registry: ${KANIKO_IMAGE_REGISTRY:${registry.name}}
  secret: ${KANIKO_SECRET:${registry.secret}}
  args: ${KANIKO_ARGS:}    
```
7. Mlrun configuration
```yml
mlrun:
  base-image: ${MLRUN_BASE_IMAGE:mlrun/mlrun}
  image-prefix: ${MLRUN_IMAGE_PREFIX:dhcore}
  image-registry: ${MLRUN_IMAGE_REGISTRY:}
```
8. Docker Registry configuration
```yml
registry:
  name: ${DOCKER_REGISTRY:}
  secret: ${DOCKER_REGISTRY_SECRET:}
```
9. Kubernetes configuration
```yml
kubernetes:
  namespace: ${K8S_NAMESPACE:default}
  logs: ${K8S_ENABLE_LOGS:true}
  metrics: ${K8S_ENABLE_METRICS:true}
  config:
    config-map: ${DH_CONFIG_COMMON_MAPS:}
    secret: ${DH_CONFIG_COMMON_SECRETS:}
```
10. Application, quindi la configurazione relativa a Core
```yml
application:
  endpoint: ${DH_ENDPOINT:http://localhost:8080}
  name: @project.name@
  description: @project.description@
  version: @project.version@
  profiles: ${spring.profiles.active:default}
```
11. Log configuration
```yml
logging:
  level:
    ROOT: INFO
    it.smartcommunitylabdhub: ${LOG_LEVEL:INFO}
```
12. Spring Security configuration
```yml
security:
  api:
    cors:
      origins: ${DH_CORS_ORIGINS:http://localhost:5173}
  basic:
    username: ${DH_AUTH_BASIC_USER:admin}
    password: ${DH_AUTH_BASIC_PASSWORD:}
  jwt:
    issuer-uri: ${DH_AUTH_JWT_ISSUER_URI:${security.oidc.issuer-uri}}
    audience: ${DH_AUTH_JWT_AUDIENCE:${security.oidc.client-id}}
    claim: ${DH_AUTH_JWT_CLAIM:roles}
  oidc:
    issuer-uri: ${DH_AUTH_OIDC_ISSUER_URI:}
    client-id: ${DH_AUTH_OIDC_CLIENT_ID:}
    scope: ${DH_AUTH_OIDC_SCOPE:openid,email,profile}
```
13. Event Queue configuration
```yml
event-queue:
  enabled: ${ENABLE_EVENTS:false}
  services:
    rabbit:
      connection:
        host: ${RABBITMQ_HOST:}
        port: ${RABBITMQ_PORT:}
        username: ${RABBITMQ_USER:}
        password: ${RABBITMQ_PASSWORD:}
        virtual-host: ${RABBITMQ_VHOST:/}
      queue-name: ${RABBITMQ_QUEUE:}
      entity-topic: ${RABBITMQ_TOPIC:entityTopic}
      entity-routing-key: ${RABBITMQ_ROUTING_KEY:entityRoutingKey}
      enabled: ${RABBITMQ_ENABLE:false}
```
14. Core Monitors configuration
```yml
monitors:
  min-delay: 60
  delay: ${MONITORS_DELAY:60}
```
15. Solr configuration
```yml
solr:
  enabled: ${ENABLE_SOLR:false}
  url: ${SOLR_URL:http://localhost:8983/solr}
  collection: ${SOLR_COLLECTION:dhcore}
  reindex: ${SOLR_REINDEX:never}
```

### How to run db?

To start the database for Core using Docker, execute the following command:
```bash
docker run -d --name <dbname> -p 5434:5432 -e POSTGRES_PASSWORD=<password> postgres
```
Once the database container is started, you can launch your application. 

It's important to configure the database parameters either via ENV variables or directly in the application.yml file. You can find those settings in the SpringBoot configuration section  

**IMPORTANT:** If no database is created locally or through Docker, an H2 database will be automatically created when the application starts.

### How to run core?
To run core there are different possibilities:

1. You can download directly the image from GitHub and run it
```bash
docker pull ghcr.io/scc-digitalhub/digitalhub-core:<version>
docker run digitalhub-core:<version>

```
2. You can build your own image and run it
```bash
docker build -t ghcr.io/scc-digitalhub/digitalhub-core:<version> .
docker run digitalhub-core:<version>
```

2. You can run core locally using Maven
```bash
# Download DH Core from GitHub
git clone https://github.com/scc-digitalhub/digitalhub-core.git

# Update all submodules
git submodule init
git sumodule update

# Run the project
mvn spring-boot:run -pl application
```
In case of problem once you have updated all submodules you can clean and install all dependencies 
running the following command 
```bash
mvn clean install -DskipTests 
```

### web interface
TODO

### kubernetes
TODO

```bash
docker build .
docker run
```
