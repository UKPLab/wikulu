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
import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;

import de.tudarmstadt.ukp.wikiapi.MediaWiki;
import de.tudarmstadt.ukp.wikiapi.textconverter.TextConverter;

/**
 * @author caro
 * 
 */
public class MediaWikiPage
	extends Page
{

	/**
	 * @param wiki
	 * @param relativeUrl
	 */
	public MediaWikiPage(MediaWiki wiki, String relativeUrl)
	{
		super(wiki, relativeUrl);
	}

	@Override
	public String getPlainContentFromWikiSyntax()
		throws IOException
	{
		/*MarkupParser markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(new MediaWikiLanguage());

		StringBuffer out = new StringBuffer();
		markupParser.setBuilder(new PlainTextDocumentBuilder(out));

		String wikiContent = this.getWikiSyntaxContent();
		markupParser.parse(wikiContent);

		return out.toString();*/
		// Set up wiki syntax parser
		SimpleWikiConfiguration config = new SimpleWikiConfiguration(
		               "classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
		org.sweble.wikitext.engine.Compiler comp = new org.sweble.wikitext.engine.Compiler(config);

		// Some input text
		String wikiSyntaxText = this.getWikiSyntaxContent();
		
		String subOne = "";
		String subTwo = "";
		int pos;
		int posEnd;
		// remove german image links
		while(wikiSyntaxText.contains("[[Bild")) {
			pos = wikiSyntaxText.indexOf("[[Bild");
			posEnd = wikiSyntaxText.indexOf("]]", pos);
			subOne = wikiSyntaxText.substring(0, pos);
			subTwo = wikiSyntaxText.substring(posEnd+2);
			wikiSyntaxText = subOne + subTwo;
		}
		
		CompiledPage compPage = null;
		// Retrieve a page
		PageTitle pageTitle= null;
		try {
			pageTitle = PageTitle.make(config, "temp");
			PageId pageId = new PageId(pageTitle, -1);
			// Compile the retrieved page
			compPage = comp.postprocess(pageId, wikiSyntaxText, null);
		}
		catch (LinkTargetException e) {
			e.printStackTrace();
		}
		catch (CompilerException e) {
			e.printStackTrace();
		}

		// Render the compiled page as text
		TextConverter textConverter = new TextConverter(config, 0);
		String plainText = (String) textConverter.go(compPage.getPage());
		return plainText;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getWikiSyntaxContent()
		throws IOException
	{
		String request = getWiki().apiURL.toExternalForm()
				+ "?action=query&prop=revisions&rvlimit=1&rvprop=content&format=xml&titles="
				+ ((MediaWiki) getWiki()).getCurrentPageTitle(this
						.getArticleUrl()) + "&redirects"; // resolve
		// redirection
		// automatically
		// somehow the request URL for wikipedia (and sometimes
		// for twiki on the wikulu server) contains the wiki's
		// base URL two times. can't find the reason why, so
		// this solves the problem
		if(request.split("http").length > 2) {
			System.out.println("REQUEST URL CORRUPTED!: " + request);
			request = "http" + request.split("http")[2];
		}
		
		URL requestUrl = new URL(request);
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(requestUrl);
		}
		catch (JDOMException e) {
			throw new IOException(e);
		}
		Iterator itr = doc.getDescendants(new Filter()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o)
			{
				if (o instanceof Element) {
					Element e = (Element) o;

					return e.getName().equals("rev");
				}

				return false;
			}
		});

		String wikiContent = null;

		if (itr.hasNext()) {
			Element e = (Element) itr.next();
			wikiContent = e.getText();
		}

		if (wikiContent == null) {
			wikiContent = "";
		}

		return wikiContent;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getRelevantContent(Document doc)
		throws IOException
	{
		Iterator itr = doc.getDescendants();
		// String content = null;
		Element contentEl = null;
		Element delEl = null;
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof Element) {
				Element e = (Element) o;
				if (e.getAttribute("id") != null
						&& e.getAttributeValue("id").equals("bodyContent")) {
					contentEl = e;
				}
				else if (e.getAttribute("class") != null
						&& e.getAttributeValue("class").equals("printfooter")) {
					delEl = e;
					break;
				}
			}
		}
		// erase footer from content
		delEl.detach();

		String content = xmlUtils.elementAsString(contentEl);

		assert (content != null);
		return content.toString();
	}

	@Override
	public String getArticleTitle()
	{
		// TODO implement this
		return null;
	}
}
