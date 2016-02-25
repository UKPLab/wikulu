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
/**
 * 
 */
package de.tudarmstadt.ukp.wikiapi.type;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;

import de.tudarmstadt.ukp.wikiapi.TWiki;
import de.tudarmstadt.ukp.wikiapi.textconverter.TextConverter;

/**
 * An article page from TWiki.
 * 
 * @author Carolin Deeg
 * 
 */
public class TWikiPage
	extends Page
{

	/**
	 * @param wiki
	 *            the wiki this page belongs to
	 * @param relativeUrl
	 *            the article name with underscores instead of spaces
	 */
	public TWikiPage(TWiki wiki, String relativeUrl)
	{
		super(wiki, relativeUrl);
	}

	@Override
	public String getPlainContentFromWikiSyntax()
		throws IOException
	{
		/*String wikiContent = this.getWikiSyntaxContent();
		wikiContent = wikiContent.replaceAll("<br/>", " ");
		wikiContent = wikiContent.replaceAll("<br />", " ");

		MarkupParser markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(new TWikiLanguage());
		StringBuffer out = new StringBuffer();
		out.append(markupParser.parseToHtml(wikiContent));
		markupParser.setBuilder(new PlainTextDocumentBuilder(out));

		// System.out.println("URL: " + this.getArticleUrl().toExternalForm());
		markupParser.parse(wikiContent);

		return out.toString();*/
		
		// Set up wiki syntax parser
		SimpleWikiConfiguration config = new SimpleWikiConfiguration(
		               "classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
		org.sweble.wikitext.engine.Compiler comp = new org.sweble.wikitext.engine.Compiler(config);

		// Some input text
		String wikiSyntaxText = this.getWikiSyntaxContent();
		CompiledPage compPage = null;
		// Retrieve a page
		PageTitle pageTitle = null;
		try {
			pageTitle = PageTitle.make(config, "temp");
			PageId pageId = new PageId(pageTitle, -1);

			// Compile the retrieved page
			compPage = comp.postprocess(pageId, wikiSyntaxText, null);
		}
		catch (LinkTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Render the compiled page as text // Render the compiled page as HTML
		TextConverter textConverter = new TextConverter(config, 0);
		
		String plainText = (String) textConverter.go(compPage.getPage());
		return plainText.replace("\n", " ");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getRelevantContent(Document doc)
		throws IOException
	{
		Iterator itr = doc.getDescendants(new Filter()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o)
			{
				if (o instanceof Element) {
					Element e = (Element) o;

					if (e.getAttribute("class") != null
							&& e.getAttributeValue("class").equals(
									"patternContent")) {
						// if (e.getAttribute("id") != null
						// &&
						// e.getAttributeValue("id").equals("patternMainContents"))
						// {
						return true;
					}
				}

				return false;
			}
		});

		String content = null;

		if (itr.hasNext()) {
			Element e = (Element) itr.next();
			content = xmlUtils.elementAsString(e);
		}

		assert (content != null);

		return content;
	}

	@Override
	public String getWikiSyntaxContent()
		throws IOException
	{

		String webTopic = ((TWiki) getWiki()).getWebTopic(this.getArticleUrl());

		URL queryURL = new URL(getWiki().apiURL.toExternalForm()
				+ "wikicontent?topic=" + webTopic);

		URLConnection connection = queryURL.openConnection();

		connection
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.1.5) Gecko/20091109 Ubuntu/9.10 (karmic) Firefox/3.5.5");

		String cookie = getWiki().getAuthData();
		if (cookie != null)
			connection.addRequestProperty("Cookie", cookie);
		InputStream in = connection.getInputStream();

		StringBuilder builder = new StringBuilder();
		byte[] b = new byte[4096];

		for (int n; (n = in.read(b)) != -1;) {
			builder.append(new String(b, 0, n));
		}

		return builder.toString();
	}

	@Override
	public String getArticleTitle()
	{
		return null;
	}

}
