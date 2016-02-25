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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.tudarmstadt.ukp.wikiapi.MediaWiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;

/**
 * This class gives an API for MediaWiki lucene index creation.
 * 
 * @author a_vovk
 * 
 */
public class MediaWikiIndexCreator
	extends WikiIndexCreator
{

	/**
	 * Constructor
	 * 
	 * @param wikiURL
	 *            URL to the wiki page
	 * @param apiURL
	 *            URL to the wiki api
	 * @param targetDirectory
	 *            directory for index files
	 * @param stopWordsStream
	 *            Stream of stopwords
	 * @param dirType 
	 * 			  Type of Lucene Directory to use           
	 * @throws IOException 
	 */
	public MediaWikiIndexCreator(String wikiURL, String apiURL,
			String targetDirectory, InputStream stopWordsStream,
			String dirType) throws IOException
	{
		super(wikiURL, apiURL, targetDirectory, stopWordsStream, dirType);
	}

	@Override
	public void createIndex()
	{
		try {

			MediaWiki wiki = new MediaWiki(new URL(apiURL), new URL(
					this.wikiURL + "index.php/"));
			int counter = 0;
			
			while (wiki.hasNextPages()) {
				List<Page> mass = wiki.getNextPagesAsObjects();
				//int c =0;
				for (Page i : mass) {
					//c++;
					//if (c%499 != 0)
						//continue;
					counter++;
					String pageContent = null;
					try {
						pageContent = i.getPlainContentFromWikiSyntax();
						indexPage(i.getArticleUrl().toExternalForm(),
								pageContent);
					}
					catch (IOException e) {
						logger.warning("Page " + i + " not found!");
					}
					logger.info(counter + " pages were indexed!");

				}

			}
			closeWriter();
			logger.info("Process is finished!");
		}
		catch (CorruptIndexException e1) {
			e1.printStackTrace();
		}
		catch (LockObtainFailedException e1) {
			e1.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
