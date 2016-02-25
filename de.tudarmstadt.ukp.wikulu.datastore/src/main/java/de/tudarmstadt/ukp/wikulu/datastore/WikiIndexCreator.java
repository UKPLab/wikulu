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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Abstract class that provides indexing for wikis(TWiki, Mediawiki)
 * 
 * @author a_vovk
 * 
 */
public abstract class WikiIndexCreator
{

	// Logger for output information
	protected Logger logger = Logger.getLogger(MediaWikiIndexCreator.class
			.getName());
	// console handler for logger
	protected ConsoleHandler ch = new ConsoleHandler();
	// url to the wiki
	protected String wikiURL = null;
	// stream with stop words
	protected InputStream stopWordsStream = null;
	// url to the api
	protected String apiURL = null;
	// Lucene Index Directory(RAM or FS)
	// protected Directory luceneDir;
	// lucene index writer
	protected IndexWriter writer;

	/**
	 * Constructor(initialization, logger)
	 * 
	 * @param wikiURL
	 *            URL to the wiki page
	 * @param apiURL
	 *            URL to the wiki API
	 * @param targetDirectory
	 *            directory for index files
	 * @param stopWordsStream
	 *            Stream of stopwords
	 * @param dirType
	 *            Type of Lucene Directory to use
	 * @throws IOException
	 */
	public WikiIndexCreator(String wikiURL, String apiURL,
			String targetDirectory, InputStream stopWordsStream, String dirType)
		throws IOException
	{
		// init
		this.wikiURL = wikiURL;
		this.apiURL = apiURL;
		this.stopWordsStream = stopWordsStream;

		Directory luceneDir;
		File targetDir = new File(targetDirectory);
		if (dirType.equals("ram")) {
			// TODO:luceneDir = new RAMDirectory(targetDir);
			luceneDir = FSDirectory.open(targetDir);
		}
		else {
			// TODO: use lock factory??

			luceneDir = FSDirectory.open(targetDir);
		}

		// init writer
		// TODO: Use Another Analyser?
		writer = new IndexWriter(luceneDir, new DKProAnalyzer(
				this.stopWordsStream), true,
				IndexWriter.MaxFieldLength.UNLIMITED);

		// use logger for output
		logger.setUseParentHandlers(false);
		ch.setFormatter(new Formatter()
		{
			public String format(LogRecord record)
			{
				return record.getLevel() + "  :  " + record.getMessage() + "\n";
			}
		});
		logger.addHandler(ch);
	}

	/**
	 * Method that creates index
	 */
	public abstract void createIndex();

	/**
	 * Add page to the index
	 * 
	 * @param writer
	 *            writer to use
	 * @param pageUrl
	 *            url of the page to add
	 * @param pageText
	 *            text of the page to add
	 */
	protected void indexPage(String pageUrl, String pageText)
	{
		if ((pageUrl == null) || (pageText == null))
			return;
		logger.info("Indexing " + pageUrl);
		Document doc = new Document();
		doc.add(new Field("docno", pageUrl, Field.Store.YES, Field.Index.NO));
		doc.add(new Field("token", pageText, Field.Store.YES,
				Field.Index.ANALYZED, Field.TermVector.YES));
		try {
			writer.addDocument(doc);
		}
		catch (CorruptIndexException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void closeWriter()
		throws CorruptIndexException, IOException
	{
		writer.close();
	}
}
