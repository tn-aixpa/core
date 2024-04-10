package it.smartcommunitylabdhub.core.components.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SolrIndexManager {

    private Http2SolrClient solrClient;
    private String solrUrl;
    private String solrCollection;
    
    private RestTemplate restTemplate;
    
    private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    public SolrIndexManager(String url, String collection) {
        solrUrl = url;
    	solrCollection = collection;
        solrClient = new Http2SolrClient.Builder(solrUrl)
                .withConnectionTimeout(5000, TimeUnit.MILLISECONDS)
                .build();        
        restTemplate = new RestTemplate();
        initFields();
    }
    
    private void initFields() {
        String baseUri = solrUrl.endsWith("/") ? solrUrl : solrUrl + "/";
        String fieldsUri = baseUri + solrCollection + "/schema/fields";
        //check existing fields
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(fieldsUri, String.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
        	String schemaUri = baseUri + solrCollection + "/schema";
        	try {
				JsonNode wrapperNode = mapper.readTree(responseEntity.getBody());
				JsonNode fieldsNode = wrapperNode.path("fields");
				if(!fieldsNode.isMissingNode()) {
					List<String> names = new ArrayList<>();
					//collect fields names
					fieldsNode.elements().forEachRemaining(node -> names.add(node.get("name").asText()));
					//add the new fields
					if(!names.contains("name")) {
						addField("name", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("project")) {
						addField("project", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("kind")) {
						addField("kind", "string", true, false, true, true, schemaUri);
					}
					if(!names.contains("type")) {
						addField("type", "string", true, false, true, true, schemaUri);
					}
					if(!names.contains("keyGroup")) {
						addField("keyGroup", "string", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.name")) {
						addField("metadata.name", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.description")) {
						addField("metadata.description", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.project")) {
						addField("metadata.project", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.version")) {
						addField("metadata.version", "text_en", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.created")) {
						addField("metadata.created", "pdate", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.updated")) {
						addField("metadata.updated", "pdate", true, false, true, true, schemaUri);
					}
					if(!names.contains("metadata.labels")) {
						addField("metadata.labels", "text_en", true, true, true, true, schemaUri);
					}
				}
			} catch (Exception e) {
				log.error("SolrIndexManager:DELETE", e);
			}
        }
    }    	
    
    private void addField(String name, String type, boolean indexed, boolean multiValued, boolean stored, boolean uninvertible, String uri) {
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode addNode = rootNode.putObject("add-field");
        addNode.put("name", name)
        	.put("type", type)
        	.put("multiValued", multiValued)
        	.put("indexed", indexed)
        	.put("stored", stored)
        	.put("uninvertible", uninvertible);
        HttpEntity<ObjectNode> request = new HttpEntity<>(rootNode);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        if(response.getStatusCode().isError()) {
        	throw new CoreException(String.valueOf(response.getStatusCode().value()), 
        			"SolrIndexManager schema error", HttpStatus.valueOf(response.getStatusCode().value()));
        }
    }

    public void close() {
        if (solrClient != null) {
            solrClient.close();
        }
    }

    @SuppressWarnings("unused")
    private String getTerm(String term) {
        return ClientUtils.escapeQueryChars(term.trim());
    }

    public SolrPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        Map<String, List<String>> filters = new HashMap<>();
        QueryResponse response = prepareQuery(q, fq, pageRequest, filters, true);

        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        List<SearchGroupResult> result = new ArrayList<>();
        long total = 0;
        GroupResponse groupResponse = response.getGroupResponse();
        if (groupResponse.getValues().size() > 0) {
            GroupCommand groupCommand = groupResponse.getValues().get(0);
            total = groupCommand.getValues().size();
            for (Group group : groupCommand.getValues()) {
                SolrDocumentList documents = group.getResult();
                SearchGroupResult groupResult = new SearchGroupResult();
                groupResult.setId(group.getGroupValue());
                groupResult.setKeyGroup(group.getGroupValue());
                groupResult.setNumFound(documents.getNumFound());
                result.add(groupResult);
                for (SolrDocument doc : documents) {
                    ItemResult itemResult = SolrDocParser.parse(doc);
                    if (highlighting.containsKey(itemResult.getId())) {
                        itemResult.getHighlights().putAll(highlighting.get(itemResult.getId()));
                    }
                    groupResult.getDocs().add(itemResult);
                }
            }
        }

        return new SolrPageImpl<>(result, pageRequest, total, filters);
    }

    public void indexDoc(SolrInputDocument doc) throws Exception {
        solrClient.add(solrCollection, doc);
        solrClient.commit(solrCollection);
    }

    public void removeDoc(String id) throws Exception {
        solrClient.deleteById(solrCollection, id);
        solrClient.commit(solrCollection);
    }

    public void clearIndex() throws Exception {
        solrClient.deleteByQuery(solrCollection, "*:*");
        solrClient.commit(solrCollection);
    }

    public void indexBounce(List<SolrInputDocument> docs) throws Exception {
        for (SolrInputDocument doc : docs) {
            solrClient.add(solrCollection, doc);
        }
        solrClient.commit(solrCollection);
    }

    public SolrPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        Map<String, List<String>> filters = new HashMap<>();
        QueryResponse response = prepareQuery(q, fq, pageRequest, filters, false);

        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        List<ItemResult> result = new ArrayList<>();
        SolrDocumentList documents = response.getResults();
        for (SolrDocument doc : documents) {
            ItemResult itemResult = SolrDocParser.parse(doc);
            if (highlighting.containsKey(itemResult.getId())) {
                itemResult.getHighlights().putAll(highlighting.get(itemResult.getId()));
            }
            result.add(itemResult);
        }

        return new SolrPageImpl<>(result, pageRequest, documents.getNumFound(), filters);
    }

    private QueryResponse prepareQuery(
        String q,
        List<String> fq,
        Pageable pageRequest,
        Map<String, List<String>> filters,
        boolean grouped
    ) throws Exception {
        Map<String, String[]> queryParamMap = new HashMap<>();
        if (StringUtils.hasText(q)) {
            filters.put("q", Arrays.asList(q));
            String query = String.format(
                "metadata.name:%1$s OR metadata.description:%1$s OR metadata.project:%1$s" +
                " OR metadata.version:%1$s OR metadata.labels:%1$s",
                q.trim()
            );
            MultiMapSolrParams.addParam("q", query, queryParamMap);
        } else {
            MultiMapSolrParams.addParam("q", "*:*", queryParamMap);
        }
        if (fq != null) {
            filters.put("fq", fq);
            fq.forEach(filter -> {
                if (StringUtils.hasText(filter)) {
                    MultiMapSolrParams.addParam("fq", filter.trim(), queryParamMap);
                }
            });
        }

        if (grouped) {
            MultiMapSolrParams.addParam("group", "true", queryParamMap);
            MultiMapSolrParams.addParam("group.field", "keyGroup", queryParamMap);
            MultiMapSolrParams.addParam("group.limit", "10", queryParamMap);
        }

        MultiMapSolrParams.addParam("hl", "true", queryParamMap);
        MultiMapSolrParams.addParam(
            "hl.fl",
            "metadata.name,metadata.description,metadata.project,metadata.version,metadata.labels",
            queryParamMap
        );
        MultiMapSolrParams.addParam("hl.fragsize", "250", queryParamMap);

        MultiMapSolrParams.addParam("start", String.valueOf(pageRequest.getOffset()), queryParamMap);
        MultiMapSolrParams.addParam("rows", String.valueOf(pageRequest.getPageSize()), queryParamMap);
        if(pageRequest.getSort().isSorted()) {
        	pageRequest.getSort().forEach(order -> {
        		MultiMapSolrParams.addParam("sort", 
        				order.getProperty() + " " + order.getDirection().toString(), queryParamMap);
        	});
        }

        return solrClient.query(solrCollection, new MultiMapSolrParams(queryParamMap));
    }
}
