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

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.IOException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroWinklerSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LevenshteinComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.sound.DoubleMetaphoneComparator;
import de.tudarmstadt.ukp.similarity.algorithms.sound.DoubleMetaphoneComparatorFixed;
import de.tudarmstadt.ukp.similarity.algorithms.sound.SoundexComparator;
import de.tudarmstadt.ukp.similarity.algorithms.sound.SoundexComparatorFixed;
import de.tudarmstadt.ukp.similarity.dkpro.resource.SimpleTextSimilarityResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.WordNGramContainmentResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.WordNGramJaccardResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.string.GreedyStringTilingMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.structure.PosNGramContainmentResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.structure.StopwordNGramContainmentMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.FunctionWordFrequenciesMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.MTLDResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.TypeTokenRatioResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.vsm.VectorIndexSourceRelatednessResource;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;


public class FeatureConfigFactory
{
	public enum DemoTextSimilarityMeasure
	{
		// String
		GreedyStringTiling,
		LongestCommonSubsequence,
		LongestCommonSubstring,
		Jaro,
		JaroWinkler,
		Levenshtein,
		WordNGramContainment_2,
		WordNGramContainment_3,
		WordNGramContainment_4,
		WordNGramContainment_5,
		WordNGramJaccard_2,
		WordNGramJaccard_3,
		WordNGramJaccard_4,
		WordNGramJaccard_5,
		// Semantic
		ESA_WordNet,
		ESA_Wiktionary,
		// Structure
		StopwordNGramContainment_3,
		StopwordNGramContainment_4,
		StopwordNGramContainment_5,
		PosNGramContainment_3,
		PosNGramContainment_4,
		PosNGramContainment_5,
		// Style
		SequentialTTR,
		TTR,
		FunctionWordFrequencies,
		// Sound
		DoubleMetaphone,
		Soundex
	}
	
	public static FeatureConfig getConfig(DemoTextSimilarityMeasure measure)
		throws IOException
	{
		switch(measure)
		{
			case GreedyStringTiling:
				return new FeatureConfig(
					createExternalResourceDescription(
					    	GreedyStringTilingMeasureResource.class,
					    	GreedyStringTilingMeasureResource.PARAM_MIN_MATCH_LENGTH, "3"),
					Document.class.getName(),
					false,
					"string",
					"GreedyStringTiling_3"
					);
			case LongestCommonSubsequence:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceComparator.class.getName()),
						Document.class.getName(),
						false,
						"string",
						"LongestCommonSubsequenceComparator"
						);
			case LongestCommonSubstring:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubstringComparator.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"LongestCommonSubstringComparator"
						);
			case Jaro:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, JaroSecondStringComparator.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"Jaro"
						);
			case JaroWinkler:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, JaroWinklerSecondStringComparator.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"JaroWinkler"
						);
			case Levenshtein:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LevenshteinComparator.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"Levenshtein"
						);
			case WordNGramContainment_2:
			case WordNGramContainment_3:
			case WordNGramContainment_4:
			case WordNGramContainment_5:
				String n = measure.toString().split("_")[1];
				return new FeatureConfig(
						createExternalResourceDescription(
						    	WordNGramContainmentResource.class,
						    	WordNGramContainmentResource.PARAM_N, new Integer(n).toString()),
						Token.class.getName(),
						true,
						"n-grams",
						"WordNGramContainmentMeasure_" + n + "_stopword-filtered"
						);
			case WordNGramJaccard_2:
			case WordNGramJaccard_3:
			case WordNGramJaccard_4:
			case WordNGramJaccard_5:
				n = measure.toString().split("_")[1];
				return new FeatureConfig(
						createExternalResourceDescription(
						    	WordNGramJaccardResource.class,
						    	WordNGramJaccardResource.PARAM_N, new Integer(n).toString()),
						Token.class.getName(),
						false,
						"n-grams",
						"WordNGramJaccardMeasure_" + n
						);
			case ESA_WordNet:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	VectorIndexSourceRelatednessResource.class,
						    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DKProContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wordnet_eng_lem_nc_c"),
						Lemma.class.getName() + "/value",
						false,
						"esa",
						"ESA_WordNet"
						);
			case ESA_Wiktionary:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	VectorIndexSourceRelatednessResource.class,
						    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DKProContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wiktionary_en"),
						Lemma.class.getName() + "/value",
						false,
						"esa",
						"ESA_Wiktionary"
						);
			case StopwordNGramContainment_3:
			case StopwordNGramContainment_4:
			case StopwordNGramContainment_5:
				n = measure.toString().split("_")[1];
				return new FeatureConfig(
						createExternalResourceDescription(
						    	StopwordNGramContainmentMeasureResource.class,
						    	StopwordNGramContainmentMeasureResource.PARAM_N, new Integer(n).toString(),
						    	StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
						Token.class.getName(),
						false,
						"structure",
						"StopwordNGramContainmentMeasure_" + n
						);
			case PosNGramContainment_3:
			case PosNGramContainment_4:
			case PosNGramContainment_5:
				n = measure.toString().split("_")[1];
				return new FeatureConfig(
						createExternalResourceDescription(
						    	PosNGramContainmentResource.class,
						    	PosNGramContainmentResource.PARAM_N, new Integer(n).toString()),
						Document.class.getName(),
						false,
						"structure",
						"PosNGramContainmentMeasure_" + n
						);
			case SequentialTTR:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	MTLDResource.class),
						Document.class.getName(),
						false,
						"style",
						"SequentialTTR"
						);
			case TTR:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	TypeTokenRatioResource.class),
						Document.class.getName(),
						false,
						"style",
						"TTR"
						);
			case FunctionWordFrequencies:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	FunctionWordFrequenciesMeasureResource.class,
						    	FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "classpath:/stopwords/function-words-mosteller-wallace.txt"),
						Document.class.getName(),
						false,
						"style",
						"FunctionWordFrequencies"
						);
			case DoubleMetaphone:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, DoubleMetaphoneComparatorFixed.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"DoubleMetaphone"
						);
			case Soundex:
				return new FeatureConfig(
						createExternalResourceDescription(
						    	SimpleTextSimilarityResource.class,
						    	SimpleTextSimilarityResource.PARAM_MODE, "text",
						    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, SoundexComparatorFixed.class.getName()),
						Document.class.getName(),    	
						false,
						"string",
						"Soundex"
						);
		}
			
		return null;
	}
}
