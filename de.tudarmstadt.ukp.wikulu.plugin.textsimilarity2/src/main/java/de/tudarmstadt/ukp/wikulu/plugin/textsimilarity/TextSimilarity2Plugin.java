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
package de.tudarmstadt.ukp.wikulu.plugin.textsimilarity;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.SemEvalCorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.string.GreedyStringTilingMeasureResource;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;


public class TextSimilarity2Plugin
	extends Plugin
{
	private File OUTPUT_FILE = new File("target/" + this.getClass().getSimpleName());
	
	public TextSimilarity2Plugin(Wiki wiki)
	{
		super(wiki);
		this.wiki = wiki;
	}
	
	@Override
	public String run(String text) throws AnalysisEngineProcessException,
			ResourceInitializationException, JSONException,
			ResourceConfigurationException, IOException, CASException,
			JDOMException
	{
		JSONObject args = new JSONObject(text);

		if (args.has("dataset"))
		{
			return runOnDataset(args.getString("dataset"), args.getString("measure"));
		}
		else
		{
			return runFreeText(args.getString("text1"), args.getString("text2"), args.getString("measure"));
		}
	}

	public String runOnDataset(String dataset, String measure)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException, CASException,
		JDOMException
	{
		// Feature Config
		FeatureConfig config = FeatureConfigFactory.getConfig(
				FeatureConfigFactory.DemoTextSimilarityMeasure.valueOf(measure));
		
		// Reader
		CollectionReader reader = getReader(dataset);

		// Tokenization
		AnalysisEngineDescription seg = createPrimitiveDescription(
				BreakIteratorSegmenter.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_seg = builder.createAggregate();
		
		// POS Tagging
		AnalysisEngineDescription pos = createPrimitiveDescription(
				OpenNlpPosTagger.class,
				OpenNlpPosTagger.PARAM_LANGUAGE, "en");		
		builder = new AggregateBuilder();
		builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_pos = builder.createAggregate();
		
		// Lemmatization
		AnalysisEngineDescription lem = createPrimitiveDescription(
				GateLemmatizer.class);
		builder = new AggregateBuilder();
		builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_lem = builder.createAggregate();
		
		// Similarity Scorer
		AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
		    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
		    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
		    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
		    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
		    );
		
		// Output Writer
		AnalysisEngine writer = createPrimitive(HtmlResultsWriter.class,
			HtmlResultsWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE.getAbsolutePath());

		try {
			SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, scorer, writer);
		} catch (UIMAException e) {
			throw new IOException(e);
		}		
		
		return FileUtils.readFileToString(OUTPUT_FILE);
	}
	
	public String runFreeText(String text1, String text2, String measure)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException, CASException,
		JDOMException
	{
		// Feature Config
		FeatureConfig config = FeatureConfigFactory.getConfig(
				FeatureConfigFactory.DemoTextSimilarityMeasure.valueOf(measure));
		
		// Save texts as temporary dataset
		String data = text1 + HtmlResultsWriter.LF + text2;
		FileUtils.writeStringToFile(new File("target/freetext/1.txt"), data);
		
		// Reader
		CollectionReader reader = createCollectionReader(PlainTextCombinationReader.class,
				PlainTextCombinationReader.PARAM_INPUT_DIR, "target/freetext",
				PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());

		// Tokenization
		AnalysisEngineDescription seg = createPrimitiveDescription(
				BreakIteratorSegmenter.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_seg = builder.createAggregate();
		
		// POS Tagging
		AnalysisEngineDescription pos = createPrimitiveDescription(
				OpenNlpPosTagger.class,
				OpenNlpPosTagger.PARAM_LANGUAGE, "en");		
		builder = new AggregateBuilder();
		builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_pos = builder.createAggregate();
		
		// Lemmatization
		AnalysisEngineDescription lem = createPrimitiveDescription(
				GateLemmatizer.class);
		builder = new AggregateBuilder();
		builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_lem = builder.createAggregate();
		
		// Similarity Scorer
		AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
		    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
		    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
		    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
		    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
		    );
		
		// Output Writer
		AnalysisEngine writer = createPrimitive(HtmlResultsWriter.class,
			HtmlResultsWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE.getAbsolutePath());

		try {
			SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, scorer, writer);
		} catch (UIMAException e) {
			throw new IOException(e);
		}		
		
		return FileUtils.readFileToString(OUTPUT_FILE);
	}
	
	private CollectionReader getReader(String dataset)
		throws ResourceInitializationException
	{
		if (dataset.equals("li"))
		{
			return createCollectionReader(PlainTextCombinationReader.class,
				PlainTextCombinationReader.PARAM_INPUT_DIR, "src/main/resources/datasets/li06",
				PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		}
		else if (dataset.equals("semeval-2012-msrpar"))
		{
			return createCollectionReader(SemEvalCorpusReader.class,
				SemEvalCorpusReader.PARAM_INPUT_FILE, "src/main/resources/datasets/semeval-2012/STS.input.MSRpar.excerpt.txt",
				SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		}
		else if (dataset.equals("semeval-2012-msrvid"))
		{
			return createCollectionReader(SemEvalCorpusReader.class,
				SemEvalCorpusReader.PARAM_INPUT_FILE, "src/main/resources/datasets/semeval-2012/STS.input.MSRvid.excerpt.txt",
				SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		}
		else if (dataset.equals("semeval-2012-onwn"))
		{
			return createCollectionReader(SemEvalCorpusReader.class,
				SemEvalCorpusReader.PARAM_INPUT_FILE, "src/main/resources/datasets/semeval-2012/STS.input.OnWN.excerpt.txt",
				SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		}
		return null;
	}
}
