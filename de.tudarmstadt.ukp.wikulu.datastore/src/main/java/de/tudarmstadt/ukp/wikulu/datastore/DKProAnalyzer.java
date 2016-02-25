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
package de.tudarmstadt.ukp.wikulu.datastore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * Analyzer with StemFilter, but without dkpro components! Based on
 * LuceneIndexAnalyzer
 * 
 * 
 */
public class DKProAnalyzer
	extends Analyzer
{
	private Set stopSet;

	/**
	 * Specifies whether deprecated acronyms should be replaced with HOST type.
	 * This is false by default to support backward compatibility.
	 * 
	 * @deprecated this should be removed in the next release (3.0).
	 * 
	 * 
	 */
	private boolean replaceInvalidAcronym = defaultReplaceInvalidAcronym;

	private static boolean defaultReplaceInvalidAcronym;

	// TODO: Default to true (fixed the bug), unless the system prop is set
	static {
		final String v = System
				.getProperty("org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym");
		if (v == null || v.equals("true"))
			defaultReplaceInvalidAcronym = true;
		else
			defaultReplaceInvalidAcronym = false;
	}

	/**
	 * 
	 * @return true if new instances of StandardTokenizer will replace
	 *         mischaracterized acronyms
	 * 
	 * 
	 * @deprecated This will be removed (hardwired to true) in 3.0
	 */
	public static boolean getDefaultReplaceInvalidAcronym()
	{
		return defaultReplaceInvalidAcronym;
	}

	/**
	 * 
	 * @param replaceInvalidAcronym
	 *            Set to true to have new instances of StandardTokenizer replace
	 *            mischaracterized acronyms by default. Set to false to preseve
	 *            the previous buggy behavior. Alternatively, set the system
	 *            property org.apache.lucene.analysis.standard.StandardAnalyzer.
	 *            replaceInvalidAcronym to false.
	 * 
	 * 
	 * @deprecated This will be removed (hardwired to true) in 3.0
	 */
	public static void setDefaultReplaceInvalidAcronym(
			boolean replaceInvalidAcronym)
	{
		defaultReplaceInvalidAcronym = replaceInvalidAcronym;
	}

	/**
	 * An array containing some common English words that are usually not useful
	 * for searching.
	 */
	public static final Set STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

	/** Builds an analyzer with the default stop words ({@link #STOP_WORDS}). */
	public DKProAnalyzer()
	{
		this(STOP_WORDS);
	}

	/** Builds an analyzer with the given stop words. */
	public DKProAnalyzer(Set stopWords)
	{
		stopSet = stopWords;
	}

	/** Builds an analyzer with the given stop words. */
	public DKProAnalyzer(String[] stopWords)
	{
		stopSet = StopFilter.makeStopSet(stopWords);
	}

	/** Builds an analyzer with the given stop words. */
	public DKProAnalyzer(File stopwords)
		throws IOException
	{
		stopSet = WordlistLoader.getWordSet(stopwords);
	}

	/** Builds an analyzer with the given stop words. */
	public DKProAnalyzer(InputStream inputStream)
		throws IOException
	{
		InputStreamReader input = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(input);
		ArrayList<String> words = new ArrayList<String>();
		while (true) {
			String buf = reader.readLine();
			if (buf == null)
				break;
			words.add(buf);
		}
		String[] buf = new String[words.size()];
		buf = words.toArray(buf);
		stopSet = StopFilter.makeStopSet(buf);
	}

	/**
	 * Constructs a {@link StandardTokenizer} filtered by a
	 * {@link StandardFilter}, a {@link LowerCaseFilter} and a
	 * {@link StopFilter}.
	 */
	public TokenStream tokenStream(String fieldName, Reader reader)
	{
		StandardTokenizer tokenStream = new StandardTokenizer(Version.LUCENE_30, reader);
		TokenStream result = new StandardFilter(tokenStream);
		result = new LowerCaseFilter(result);
		result = new StopFilter(true,result, stopSet);
		result = new PorterStemFilter(result);
		return result;
	}

	private static final class SavedStreams
	{
		StandardTokenizer tokenStream;
		TokenStream filteredTokenStream;
	}

	/** Default maximum allowed token length */
	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

	/**
	 * Set maximum allowed token length. If a token is seen that exceeds this
	 * length then it is discarded. This setting only takes effect the next time
	 * tokenStream or reusableTokenStream is called.
	 */
	public void setMaxTokenLength(int length)
	{
		maxTokenLength = length;
	}

	/**
	 * @see #setMaxTokenLength
	 */
	public int getMaxTokenLength()
	{
		return maxTokenLength;
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader)
		throws IOException
	{
		SavedStreams streams = (SavedStreams) getPreviousTokenStream();
		if (streams == null) {
			streams = new SavedStreams();
			setPreviousTokenStream(streams);
			streams.tokenStream = new StandardTokenizer(Version.LUCENE_30, reader);
			streams.filteredTokenStream = new StandardFilter(
					streams.tokenStream);
			streams.filteredTokenStream = new LowerCaseFilter(
					streams.filteredTokenStream);
			streams.filteredTokenStream = new StopFilter(true,
					streams.filteredTokenStream, stopSet);
			streams.filteredTokenStream = new PorterStemFilter(
					streams.filteredTokenStream);
		}
		else {
			streams.tokenStream.reset(reader);
		}
		streams.tokenStream.setMaxTokenLength(maxTokenLength);

		streams.tokenStream.setReplaceInvalidAcronym(replaceInvalidAcronym);

		return streams.filteredTokenStream;
	}

	/**
	 * 
	 * @return true if this Analyzer is replacing mischaracterized acronyms in
	 *         the StandardTokenizer
	 * 
	 * 
	 * @deprecated This will be removed (hardwired to true) in 3.0
	 */
	public boolean isReplaceInvalidAcronym()
	{
		return replaceInvalidAcronym;
	}

	/**
	 * 
	 * @param replaceInvalidAcronym
	 *            Set to true if this Analyzer is replacing mischaracterized
	 *            acronyms in the StandardTokenizer
	 * 
	 * 
	 * @deprecated This will be removed (hardwired to true) in 3.0
	 */
	public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym)
	{
		this.replaceInvalidAcronym = replaceInvalidAcronym;
	}
}
