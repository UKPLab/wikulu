/*******************************************************************************
 * Copyright 2010-2016 Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *******************************************************************************/
package de.tudarmstadt.ukp.wikulu.plugin.addcontent;



import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.Hit;
//import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.CasUtil;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.ir.type.IndexTerm;
import de.tudarmstadt.ukp.dkpro.ir.type.Query;
import de.tudarmstadt.ukp.dkpro.ir.type.SearchResult;

/**
 * Takes the String in {@link Query} annotation (terms are combined by OR operator) and
 * searches on the given index. For each result a {@link SearchResult} annotation is
 * added. If there is no "topic" view, the standard view of the CAS is used.
 */
public class LuceneSearcher extends JCasAnnotator_ImplBase {
	//VIEW NAMES
	public static final String TOPIC_VIEW = "topic";
	public static final String DOC_VIEW = "doc";

	public static final String TERM_FIELD = "token";
	public static final String DOC_FIELD = "docno";

	public static final String PARAM_INDEX_PATH = "IndexPath";
	@ConfigurationParameter(name=PARAM_INDEX_PATH, mandatory=true)
	private String indexPath;

	/**
	 * If true then the index will be kept in RAM, otherwise kept on disk.
	 * Trade-off between speed and memory usage.
	 */
	public static final String PARAM_USE_RAM_INDEX = "UseRamIndex";
	@ConfigurationParameter(name=PARAM_USE_RAM_INDEX, mandatory=true, defaultValue="true")
	private boolean useRamIndex;

	public static final String PARAM_TERM_FIELD_NAME = "TermFieldName";
	@ConfigurationParameter(name=PARAM_TERM_FIELD_NAME, mandatory=true, defaultValue=TERM_FIELD)
	private String termFieldName;

	public static final String PARAM_DOC_ID_FIELD_NAME = "DocIdFieldName";
	@ConfigurationParameter(name=PARAM_DOC_ID_FIELD_NAME, mandatory=true, defaultValue=DOC_FIELD)
	private String docIdFieldName;

	//public static final String PARAM_FIELDS = "Fields";
	//public static final String PARAM_FIELD_WEIGHTS = "FieldWeights";

	private IndexSearcher searcher;
	private QueryParser queryParser;
	
	private static final int AMOUNT_OF_HITS = 50;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			if (useRamIndex) {
				dir = new RAMDirectory(dir);
			}

			if(docIdFieldName==null || docIdFieldName.length()==0) {
				docIdFieldName = DOC_FIELD;
			}

			if(termFieldName==null || termFieldName.length()==0) {
				termFieldName = TERM_FIELD;
			}

			searcher = new IndexSearcher(dir, true);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		queryParser = new QueryParser(Version.LUCENE_30, termFieldName, new WhitespaceAnalyzer());
		// TODO needs update to newer Lucene version in order to use field weights
		//queryParser = new MultiFieldQueryParser()
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas topic = JCasUtil.getView(jcas, TOPIC_VIEW, jcas);

		Query query = JCasUtil.selectSingle(topic, Query.class);
		org.apache.lucene.search.Query luceneQuery = null;

		try {
			luceneQuery = queryParser.parse(query.getQuery());
			//luceneQuery = getOldQuery(topic);
			
			TopDocs topDocs = searcher.search(luceneQuery, AMOUNT_OF_HITS);
			
			//Hits hits = searcher.search(luceneQuery);
			//Iterator hitIt = hits.iterator();
			//FSArray targets = new FSArray(jcas, topDocs.totalHits);
			int rank = 0;
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				SearchResult searchResult = new SearchResult(topic);
				searchResult.setRank(rank);
				searchResult.setScore(scoreDoc.score);
				searchResult.setDocId(doc.get(docIdFieldName));
//				searchResult.setTopicId(MetaData.get(topic).getDocumentId());
				searchResult.setBegin(rank);
				searchResult.setEnd(rank);
				searchResult.addToIndexes();
				rank++;
			}
			
//			while(hitIt.hasNext()) {
//				Hit hit = (Hit)hitIt.next();
//			//for(int i=0;i<hits.length();i++) {
//				//String docId = hits.doc(i).get(DOC_FIELD);
//				//float score = hits.score(i);
//				SearchResult searchResult = new SearchResult(topic);
//				searchResult.setRank(rank);
//				searchResult.setScore(hit.getScore());
//				searchResult.setDocId(hit.get(docIdFieldName));
//				searchResult.setTopicId(getMetaData(topic).getDocumentId());
//				searchResult.setBegin(rank);
//				searchResult.setEnd(rank);
//				searchResult.addToIndexes();
//				rank++;
//			}

		} catch (ParseException e) {
			throw new AnalysisEngineProcessException(e);

		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Returns a query as implemented in visir (should be same as above)
	 * @param jcas
	 * @return
	 */
	private org.apache.lucene.search.Query getOldQuery(JCas jcas) {
		BooleanQuery bq = new BooleanQuery();
		AnnotationIndex termIndex = jcas.getAnnotationIndex(IndexTerm.type);
		FSIterator termIt = termIndex.iterator();
		int nClauses = 0;
		String fieldName = "token";
		while(termIt.hasNext()) {
			String term = ((IndexTerm)termIt.next()).getTerm();
			TermQuery tq = new TermQuery(new Term(fieldName,term));
			//for 1.9.1
//			bq.add(tq, false,false);
			//for 2.3.1
			bq.add(tq, Occur.SHOULD);

			/*
			if(QUERY_EXPANSION__HYPONYME.equals(qeStatus)) {
				Set<String> hypos = qe.getHyponyms(term);
				logger.info("hypos:  "+CollectionUtil.print(hypos));
				for(String hypo : hypos) {
					if(!term.equals(hypo)) {
						tq = new TermQuery(new Term(fieldName,hypo));
						bq.add(tq,false,false);
					}
				}
			} else if(QUERY_EXPANSION__SYNONYME.equals(qeStatus)) {
				Set<String> syns = qe.getSynonyms(term);
				logger.info("syns:  "+CollectionUtil.print(syns));
				for(String syn : syns) {
					if(!term.equals(syn)) {
						tq = new TermQuery(new Term(fieldName,syn));
						bq.add(tq,false,false);
					}
				}
			} else if(QUERY_EXPANSION__SYNONYME_HYPONYME.equals(qeStatus)) {
				Set<String> syns = qe.getSynonyms(term);
				Set<String> hypos = qe.getHyponyms(term);
				Set<String> synhypos = new HashSet<String>();
				synhypos.addAll(syns);
				synhypos.addAll(hypos);
				//logger.info("synhypos:  "+CollectionUtil.print(synhypos));
				for(String synhypo : synhypos) {
					if(!term.equals(synhypo)) {
						tq = new TermQuery(new Term(fieldName,synhypo));
						bq.add(tq,false,false);
					}
				}
				for(String hypo : hypos) {
					if(!term.equals(hypo)) {
						tq = new TermQuery(new Term(fieldName,hypo));
						bq.add(tq,false,false);
					}
				}
			}
			*/
			//logger.info("added clauses: "+nClauses);
		}
		//logger.info("query: "+bq.toString());
		return bq;

	}
}
