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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikiapi.util.WikiXMLUtils;

/**
 * 
 * 
 * @author Carolin Deeg
 * 
 */
public abstract class Page
{

	/** */
	private Wiki wiki;

	/** */
	private String id;

	/** */
	private String articleUrl;

	/** */
	protected WikiXMLUtils xmlUtils;

	/**
	 * 
	 * 
	 * @param wiki
	 *            the wiki the page belongs to
	 * @param relativeUrl
	 *            the article name with underscores instead of spaces
	 */
	public Page(Wiki wiki, String relativeUrl)
	{
		this.wiki = wiki;
		// this.articleName = relativeUrl;
		if(!wiki.getViewUrl().toExternalForm().endsWith("/") && !relativeUrl.startsWith("/")) {
			this.articleUrl = wiki.getViewUrl().toExternalForm() + "/" + relativeUrl;
		} else {
			this.articleUrl = wiki.getViewUrl().toExternalForm() +  relativeUrl;
		}
		this.xmlUtils = new WikiXMLUtils();
	}

	/**
	 * Returns the plain text page content for further processing
	 * 
	 * @param urlString
	 *            URL to get plain content for
	 * @return Page content as plain text
	 * @throws IOException
	 * @throws IOException
	 */
	public String getPlainPageContent()
		throws IOException
	{
		String htmlContent = getHtmlContent();
		String plainContent;
		try {
			plainContent = xmlUtils.getPlainTextFromHTML(htmlContent);
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		return plainContent;
	}

	/**
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public String getHtmlContent()
		throws IOException
	{
		URLConnection con = new URL(this.articleUrl).openConnection();
		con
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.1.5) Gecko/20091109 Ubuntu/9.10 (karmic) Firefox/3.5.5");
		BufferedReader in = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));

		StringBuffer sbuf = new StringBuffer();
		for (String inputLine; (inputLine = in.readLine()) != null;) {
			sbuf.append(inputLine + "\n");
		}
		in.close();
		return sbuf.toString();
	}

	/**
	 * Gets the content of a page as wiki syntax.
	 * 
	 * @return the content as wiki syntax
	 * @throws IOException
	 */
	public abstract String getWikiSyntaxContent()
		throws IOException;

	/**
	 * Gets the plain content of a page from the page's wiki syntax content.
	 * 
	 * @return the plain content
	 * @throws IOException
	 */
	public abstract String getPlainContentFromWikiSyntax()
		throws IOException;

	/**
	 * Gets the plain content of a page from it's HTML content.
	 * 
	 * @return the plain content
	 * @throws IOException
	 */
	public String getPlainContentFromHtml()
		throws IOException
	{
		// String enhancedURL = this.articleUrl + "?" +
		// "username="+"ArtemVovk"+"&password="+"ia31tanatosx.1986";
		// String data =
		// "username="+"ArtemVovk"+"&password="+"ia31tanatosx.1986&noredirect=on";

		URL url = null; // = new URL(articleUrl);
		try {
			URI uri = new URI(articleUrl);
			url = uri.toURL();
		}
		catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLConnection connection = url.openConnection();
		connection
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.1.5) Gecko/20091109 Ubuntu/9.10 (karmic) Firefox/3.5.5");

		System.out.println("connectionurl: " + connection.getURL().toString());
		InputStream istream = connection.getInputStream();
		SAXBuilder builder = new SAXBuilder(false);
		builder.setEntityResolver(wiki.new W3CResolver());

		Document doc;
		try {
			doc = builder.build(istream);
		}
		catch (JDOMException e) {
			throw new IOException(e);
		}

		String relevantContent = getRelevantContent(doc);

		return relevantContent;

	}

	/**
	 * 
	 * @param doc
	 * @return
	 * @throws IOException
	 */
	protected abstract String getRelevantContent(Document doc)
		throws IOException;

	/**
	 * Gets the title of the Wiki page.
	 * 
	 * @return the article title
	 */
	public abstract String getArticleTitle();

	/**
	 * Returns the page ID of this Page object.
	 * 
	 * @return the id
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * Sets the page ID of this Page object.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setID(String id)
	{
		this.id = id;
	}

	/**
	 * Returns the URL of this Page object.
	 * 
	 * @return the articleUrl
	 * @throws MalformedURLException
	 */
	public URL getArticleUrl()
		throws MalformedURLException
	{
		URL url = new URL(this.articleUrl);
		return url;
	}

	/**
	 * Sets the URL of this Page object. This method needs only the relative
	 * URL.
	 * 
	 * @param articleUrl
	 *            the relative URL of the article
	 */
	public void setArticleUrl(String relativeUrl)
	{
		this.articleUrl = this.wiki.getViewUrl().toExternalForm() + relativeUrl;
	}

	/**
	 * Return the Wiki this Page belongs to.
	 * 
	 * @return the wiki
	 */
	public Wiki getWiki()
	{
		return wiki;
	}

	/**
	 * Set a new Wiki for a Page.
	 * 
	 * @param wiki
	 *            the wiki to set
	 */
	public void setWiki(Wiki wiki)
	{
		this.wiki = wiki;
	}
}
