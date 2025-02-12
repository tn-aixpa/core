package it.smartcommunitylabdhub.core.components.lucene;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import it.smartcommunitylabdhub.core.models.indexers.IndexerException;
import it.smartcommunitylabdhub.core.models.indexers.ItemResult;
import it.smartcommunitylabdhub.core.models.indexers.SearchGroupResult;
import it.smartcommunitylabdhub.core.models.indexers.SolrPage;
import it.smartcommunitylabdhub.core.models.indexers.SolrPageImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LuceneManager {

	private LuceneProperties properties;	
	private Analyzer analyzer;
	private Directory directory;
	private IndexWriterConfig config;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private IndexWriter iwriter;

    public LuceneManager(LuceneProperties properties) {
    	Assert.notNull(properties, "lucene properties can not be null");
    	this.properties = properties;
    }
    
    public synchronized void init() throws IndexerException {
    	try {
    		analyzer = new StandardAnalyzer();
    		Path path = Paths.get(properties.getIndexPath());
    		if(!Files.exists(path)) {
    			Files.createDirectory(path);
    		}
	        directory = FSDirectory.open(path);
	        config = new IndexWriterConfig(analyzer);
	        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
	        iwriter = new IndexWriter(directory, config);
	        iwriter.commit();
	        ireader = DirectoryReader.open(directory);
	        isearcher = new IndexSearcher(ireader);
	        log.info("Lucene index initialized");
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}    	
    }
    
	public synchronized void close() throws IndexerException {
		try {
			iwriter.close();
			ireader.close();
			directory.close();
			log.info("Lucene index closed");
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}
	
	public synchronized void indexDoc(Document doc) throws IndexerException {
		log.debug("index doc");
		try {
			iwriter.addDocument(doc);
			iwriter.commit();
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}
	
	public synchronized void removeDoc(String id) throws IndexerException {
		 log.debug("remove doc {}", String.valueOf(id));
		 try {
			 Term term = new Term("id", id);
			 iwriter.deleteDocuments(term);
			 iwriter.commit();
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}

	public synchronized void indexBounce(Iterable<Document> docs) throws IndexerException {
		log.debug("index bounce docs");
		try {
			for(Document doc :  docs) {
				iwriter.addDocument(doc);
			}
			iwriter.commit();			
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}

	public synchronized void clearIndex() throws IndexerException {
		 log.debug("clear index");
		 try {
			 iwriter.deleteAll();
			 iwriter.commit();
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}

	public synchronized void clearIndexByType(String type) throws IndexerException {
		 log.debug("clear index {}", type);
		 try {
			 Term term = new Term("type", type);
			 iwriter.deleteDocuments(term);
			 iwriter.commit();
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}

	public SolrPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest) throws IndexerException {
		log.debug("item search for {} {}", q, fq);
		
		try {
			Map<String, List<String>> filters = new HashMap<>();
			BooleanQuery query = prepareQuery(q, fq, pageRequest, filters, false);
			log.info("query {}", query.toString());
			
			TopDocs topDocs = null;
			
			if (pageRequest.getSort().isSorted()) {
				Sort sort = prepareSorting(pageRequest);
				topDocs = isearcher.search(query, pageRequest.getPageSize(), sort);
			} else {
				topDocs = isearcher.search(query, pageRequest.getPageSize());
			}
			
			StoredFields storedFields = ireader.storedFields();
			List<ItemResult> result = new ArrayList<>();
			for(ScoreDoc hit : topDocs.scoreDocs) {
				Document doc = storedFields.document(hit.doc);
				log.info("doc {} updateLong {}", doc.get("keyGroup"), doc.get("metadata.updatedLong"));
				ItemResult itemResult = LuceneDocParser.parse(doc);
				result.add(itemResult);
			}
			return new SolrPageImpl<ItemResult>(result, pageRequest, topDocs.totalHits.value, filters);
		} catch (Exception e) {
			throw new IndexerException(e.getMessage());
		}
	}

	public SolrPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest) throws IndexerException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BooleanQuery prepareQuery(
		String q,
        List<String> fq,
        Pageable pageRequest,
        Map<String, List<String>> filters,
        boolean grouped) throws Exception {
		Builder builder = new BooleanQuery.Builder();
		if (StringUtils.hasText(q)) {
			filters.put("q", Arrays.asList(q));
			String[] fields = new String[] {"metadata.name", "metadata.description", "metadata.project", "metadata.version", "metadata.labels"};
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
			Query query = parser.parse(q.trim());
			builder.add(query, BooleanClause.Occur.MUST);
		}
		 if (fq != null) {
			 filters.put("fq", fq);
			 StandardQueryParser standardQueryParser = new StandardQueryParser(analyzer);
			 for(String filter : fq) {
				 if (StringUtils.hasText(filter)) {
	                    String field = filter.substring(0, filter.indexOf(':'));
	                    String value = filter.substring(filter.indexOf(':') + 1);
	                    if(field.equals("metadata.updated")) {
	                        SimpleDateFormat sdf = new SimpleDateFormat(LuceneDocParser.dateFormat);
	                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	                    	String s = StringUtils.deleteAny(StringUtils.deleteAny(value, "]"), "[");
	                    	String[] split = s.split(" TO ");
	                    	if(split.length == 2) {
	                    		if(!split[0].equals("*")) {
	                    			String from = StringUtils.deleteAny(split[0], "\"");
	                    			split[0] = String.valueOf(sdf.parse(from).getTime());  
	                    		}
	                    		if(!split[1].equals("*")) {
	                    			String to = StringUtils.deleteAny(split[1], "\"");
	                    			split[1] = String.valueOf(sdf.parse(to).getTime());  
	                    		}
	                    	}
	                    	Query query = standardQueryParser.parse(String.format("[%s TO %s]", split[0], split[1]), "metadata.updatedLong");
	                		builder.add(query, BooleanClause.Occur.MUST);	                    	
	                    } else { 
	                		Query query = standardQueryParser.parse(value, field);
	                		builder.add(query, BooleanClause.Occur.MUST);	                    	
	                    }
	             }
			 }
		 }
		 return builder.build();
	}
	
	private Sort prepareSorting(Pageable pageRequest) {
		List<SortField> sortFields = new ArrayList<>();
		pageRequest.getSort().forEach(order -> {
			String field = order.getProperty().equals("metadata.updated") ? "metadata.updatedLong" : order.getProperty();
			SortField sf = new SortField(field, SortField.Type.STRING, order.getDirection().isDescending()) ;
			sortFields.add(sf);
		});
		return new Sort(sortFields.toArray(new SortField[0]));
	}
}
