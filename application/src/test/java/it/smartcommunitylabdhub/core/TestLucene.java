package it.smartcommunitylabdhub.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLucene {
	
	private Analyzer analyzer;
	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;

	@BeforeEach
	public void setUp() throws Exception {
		analyzer = new StandardAnalyzer();
		Path path = Paths.get("C:\\home\\dev\\dhcore\\lucene");
		if(!Files.exists(path)) {
			Files.createDirectory(path);
		}
        directory = FSDirectory.open(path);
        ireader = DirectoryReader.open(directory);
        isearcher = new IndexSearcher(ireader);					
	}
	
	@AfterEach
	public void close() throws Exception {
		ireader.close();
		directory.close();
	}
	
	@Test
	public void testQuesry() throws Exception {
		Builder builder = new BooleanQuery.Builder();
		
		StandardQueryParser standardQueryParser = new StandardQueryParser(analyzer);
		
		TermQuery tqProject = new TermQuery(new Term("project", "prj1"));
		//builder.add(tqProject, BooleanClause.Occur.MUST);
		
		Query tqName = standardQueryParser.parse("funct*", "name");
		//builder.add(tqName, BooleanClause.Occur.MUST);

		Query tqLabels = standardQueryParser.parse("(api AND python)", "metadata.labels");
		//builder.add(tqLabels, BooleanClause.Occur.MUST);
		
		Query tqType = standardQueryParser.parse("(function OR dataitem)", "type");
		builder.add(tqType, BooleanClause.Occur.MUST);

		//Query tqUpdated =  TermRangeQuery.newStringRange("metadata.updatedLong", "1738146678501", "173814670000", true, true);
		Query tqUpdated = standardQueryParser.parse("[* TO 173814670000]", "metadata.updatedLong");
		//Query tqUpdated = standardQueryParser.parse("[\"2025-01-28T23:00:00.000Z\" TO \"2025-01-30T12:59:59.000Z\"]", "metadata.updated");
		builder.add(tqUpdated, BooleanClause.Occur.MUST);
		
		BooleanQuery booleanQuery = builder.build();
		System.out.println(booleanQuery.toString());
		TopDocs topDocs = isearcher.search(booleanQuery, 10);
		StoredFields storedFields = ireader.storedFields();
		for(ScoreDoc hit : topDocs.scoreDocs) {
			Document doc = storedFields.document(hit.doc);
			System.out.println(doc.toString());
		}
	}
}
