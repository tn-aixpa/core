#VERSION := $(shell cat VERSION)
VERSION := v3.5.11

.PHONY: build
build:
	echo $(VERSION)
	curl https://raw.githubusercontent.com/argoproj/argo-workflows/refs/tags/$(VERSION)/api/openapi-spec/swagger.json | sed 's/io.argoproj.workflow.v1alpha1.//' | sed 's/io.k8s.api.core.v1.//' | sed 's/io.k8s.apimachinery.pkg.apis.meta.v1.//' > swagger.json
	docker run --rm -v `pwd`:/wd openapitools/openapi-generator-cli:v4.3.1 \
		generate \
		-i /wd/swagger.json \
		-g java \
		-o /wd/__openapigen \
		-p hideGenerationTimestamp=true \
		-p dateLibrary=joda \
		--model-package io.argoproj.workflow.models \
		--skip-validate-spec \
		--import-mappings Time=java.time.OffsetDateTime \
		--import-mappings Affinity=io.kubernetes.client.openapi.models.V1Affinity \
		--import-mappings ConfigMapKeySelector=io.kubernetes.client.openapi.models.V1ConfigMapKeySelector \
		--import-mappings Container=io.kubernetes.client.openapi.models.V1Container \
		--import-mappings ContainerPort=io.kubernetes.client.openapi.models.V1ContainerPort \
		--import-mappings EnvFromSource=io.kubernetes.client.openapi.models.V1EnvFromSource \
		--import-mappings EnvVar=io.kubernetes.client.openapi.models.V1EnvVar \
		--import-mappings HostAlias=io.kubernetes.client.openapi.models.V1HostAlias \
		--import-mappings Lifecycle=io.kubernetes.client.openapi.models.V1Lifecycle \
		--import-mappings ListMeta=io.kubernetes.client.openapi.models.V1ListMeta \
		--import-mappings LocalObjectReference=io.kubernetes.client.openapi.models.V1LocalObjectReference \
		--import-mappings ObjectMeta=io.kubernetes.client.openapi.models.V1ObjectMeta \
		--import-mappings ObjectReference=io.kubernetes.client.openapi.models.V1ObjectReference \
		--import-mappings PersistentVolumeClaim=io.kubernetes.client.openapi.models.V1PersistentVolumeClaim \
		--import-mappings PodDisruptionBudgetSpec=io.kubernetes.client.openapi.models.V1beta1PodDisruptionBudgetSpec \
		--import-mappings PodDNSConfig=io.kubernetes.client.openapi.models.V1PodDNSConfig \
		--import-mappings PodSecurityContext=io.kubernetes.client.openapi.models.V1PodSecurityContext \
		--import-mappings Probe=io.kubernetes.client.openapi.models.V1Probe \
		--import-mappings ResourceRequirements=io.kubernetes.client.openapi.models.V1ResourceRequirements \
		--import-mappings SecretKeySelector=io.kubernetes.client.openapi.models.V1SecretKeySelector \
		--import-mappings SecurityContext=io.kubernetes.client.openapi.models.V1SecurityContext \
		--import-mappings Toleration=io.kubernetes.client.openapi.models.V1Toleration \
		--import-mappings Volume=io.kubernetes.client.openapi.models.V1Volume \
		--import-mappings VolumeDevice=io.kubernetes.client.openapi.models.V1VolumeDevice \
		--import-mappings VolumeMount=io.kubernetes.client.openapi.models.V1VolumeMount \
		--generate-alias-as-model

	mkdir -p src/main/java/io/argoproj/workflow/models
	mv __openapigen/src/main/java/io/argoproj/workflow/models/* src/main/java/io/argoproj/workflow/models/
	rm -R __openapigen/
	rm swagger.json
