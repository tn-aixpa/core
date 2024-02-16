package it.smartcommunitylabdhub.core.components.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class SolrIndexManager {
	private Http2SolrClient solrClient;
	
	private String solrCollection;
	
	public SolrIndexManager(Http2SolrClient client, String collection) {
		this.solrClient = client;
		this.solrCollection = collection;
	}
	
	public void close() {
		if(solrClient != null) {
			solrClient.close();
		}		
	}
	
	private String getTerm(String term) {
		return ClientUtils.escapeQueryChars(term.trim());
	}

	public Page<SearchGroupResult> search(String q, String fq, Pageable pageRequest) throws Exception {
		Map<String, String> queryParamMap = new HashMap<>();
		if(StringUtils.hasText(q)) {
			String query = String.format("metadata.name:\"%1$s\" OR  metadata.description:\"%1$s\" OR metadata.project:\"%1$s\""
					+ " OR metadata.version:\"%1$s\" OR metadata.labels:\"%1$s\"", getTerm(q));
			queryParamMap.put("q", query);	
		} else {
			queryParamMap.put("q", "*:*");
		}
		if(StringUtils.hasText(fq)) {
			queryParamMap.put("fq", fq);
		}
		queryParamMap.put("group", "true");
		queryParamMap.put("group.field", "keyGroup");
		queryParamMap.put("group.limit", "5");
		
		queryParamMap.put("hl", "true");
		queryParamMap.put("hl.fl", "metadata.name,metadata.description,metadata.project,metadata.version,metadata.labels");
		
		queryParamMap.put("start", String.valueOf(pageRequest.getOffset()));
		queryParamMap.put("rows", String.valueOf(pageRequest.getPageSize()));
		
		MapSolrParams queryParams = new MapSolrParams(queryParamMap);
		QueryResponse response = solrClient.query(solrCollection, queryParams);
				
		Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
		List<SearchGroupResult> result = new ArrayList<>();
		long total = 0;
		GroupResponse groupResponse = response.getGroupResponse();
		if(groupResponse.getValues().size() > 0) {
			GroupCommand groupCommand = groupResponse.getValues().get(0);
			total = groupCommand.getValues().size();
			for(Group group : groupCommand.getValues()) {
				SolrDocumentList documents = group.getResult();
				SearchGroupResult groupResult = new SearchGroupResult();
				groupResult.setKeyGroup(group.getGroupValue());
				groupResult.setNumFound(documents.getNumFound());
				result.add(groupResult);
				for(SolrDocument doc : documents) {
					ItemResult itemResult = SolrDocParser.parse(doc);
					if(highlighting.containsKey(itemResult.getId())) {
						itemResult.getHighlights().putAll(highlighting.get(itemResult.getId()));
					}
					groupResult.getDocs().add(itemResult);
				}
			}
		}
		return new PageImpl<>(result, pageRequest, total);
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
		for(SolrInputDocument doc : docs) {
			solrClient.add(solrCollection, doc);
		}
		solrClient.commit(solrCollection);
	}
	
}
