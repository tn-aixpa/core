package it.smartcommunitylabdhub.core.controllers.v1.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.components.solr.SearchGroupResult;
import it.smartcommunitylabdhub.core.components.solr.SolrComponent;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.components.solr.SearchGroupResult;
import it.smartcommunitylabdhub.core.components.solr.SolrComponent;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.repositories.DataItemRepository;

@RestController
@RequestMapping("/solr")
@ApiVersion("v1")
public class SolrController {
	
	@Autowired
	SolrComponent solrComponent;
	
	@Autowired
	DataItemRepository dataItemRepository;
	
	@Autowired
	DataItemDTOBuilder dataItemDTOBuilder;
	
	@GetMapping(path = "/clear", produces = "application/json; charset=UTF-8")
	public ResponseEntity<Void> clearIndex() {
		solrComponent.clearIndex();
		return ResponseEntity.ok(null);
	}
	
	@GetMapping(path = "/init", produces = "application/json; charset=UTF-8")
	public ResponseEntity<Void> initIndex() {
		solrComponent.clearIndex();
		boolean finished = false;
		int pageNumber = 0;
		do {
			Page<DataItemEntity> page = dataItemRepository.findAll(PageRequest.of(pageNumber, 1000));
			if(page.getContent().size() == 0) {
				finished = true;
			} else {
				List<DataItem> items = new ArrayList<>();
				page.getContent().forEach(e -> items.add(dataItemDTOBuilder.build(e, false)));
				solrComponent.indexBounce(items);
				solrComponent.indexBounce(page.getContent());
			}
			pageNumber++;
		} while (finished);
		
		return ResponseEntity.ok(null);
	}
	
	
	@GetMapping(path = "/search", produces = "application/json; charset=UTF-8")
	public ResponseEntity<Page<SearchGroupResult>> search(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String fq,
			Pageable pageRequest) {
		try {
			Page<SearchGroupResult> page = solrComponent.search(q, fq, pageRequest);
			return ResponseEntity.ok(page);
		} catch (Exception e) {
			
			return ResponseEntity.ok(null);
		}
		
	}

}
