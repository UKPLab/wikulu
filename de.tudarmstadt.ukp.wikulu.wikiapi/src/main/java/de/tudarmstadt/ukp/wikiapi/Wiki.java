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
package de.tudarmstadt.ukp.wikiapi;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jdom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.tudarmstadt.ukp.wikiapi.type.Page;
import de.tudarmstadt.ukp.wikiapi.util.WikiXMLUtils;

/**
 * The Wiki class abstracts over the API of the underlying Wiki systems.
 * Projects using this API are independent of the concrete Wiki system.
 * 
 * @author hoffart
 * 
 */
public abstract class Wiki {

	protected WikiXMLUtils xmlUtils;
	
	protected boolean hasNext = true;

	/** The API URL for the Wiki */
	public URL apiURL;
	
	public URL viewUrl;
	//TODO: deprecated
	protected String login;
	//TODO: deprecated
	protected String password;
	
	private String authData;
	
	public String getAuthData() {
		return authData;
	}

	public void setAuthData(String authData) {
		this.authData = authData;
	}

	public URL getApiURL() {
		return apiURL;
	}
	
	public abstract String getRelativeURL(String fullURLString);

	public void setApiURL(URL apiURL) {
		this.apiURL = apiURL;
	}

	/**
	 * @return the viewUrl
	 */
	public URL getViewUrl() {
		return viewUrl;
	}

	/**
	 * @param viewUrl the viewUrl to set
	 */
	public void setViewUrl(URL viewUrl) {
		this.viewUrl = viewUrl;
	}
	
	/**
	 * 
	 * @param apiURL
	 * @param viewURL
	 */
	public Wiki(URL apiURL, URL viewURL) {
		this.setApiURL(apiURL);
		this.setViewUrl(viewURL);
	}
	
	public Wiki(URL apiURL, URL viewURL, String authData) {
		this.authData = authData;
		this.setApiURL(apiURL);
		this.setViewUrl(viewURL);
	}
	
	/**
	 * Gets plain text from relevant content wiki page (wiki syntax)
	 * Returns the document title for the given url
	 * 
	 * @param url	URL to the document
	 * @return		Document title
	 */
	public abstract String getTitleForURL(String url);
	
	
	
	public abstract String createLink(String word, String linkCandidateName);
	
	/**
	 * Gets plain text from relevant content wiki page(wiki syntax)
	 * 
	 * @return plain text
	 */
	/*@Deprecated
	public abstract String getRelevantPlainPageContentFromWiki(String url)
			throws SAXException, ParserConfigurationException, IOException;*/
		
	/**
	 * Returns the page content as wiki markup.
	 * 
	 * @param urlString
	 *            URL to get page content for
	 * @return Page content in wiki markup
	 * @throws IOException
	 */
	/*@Deprecated
	public abstract String getWikiPageContent(String urlString)
			throws IOException;*/

	/**
	 * Returns the HTML page content for further processing
	 * 
	 * @param urlString
	 *            URL to get plain content for
	 * @return Page content as plain text
	 * @throws IOException
	 */
	/*@Deprecated
	public String getHTMLPageContent(String urlString) throws IOException {
		URLConnection con = new URL(urlString).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));

		StringBuffer sbuf = new StringBuffer();
		for (String inputLine; (inputLine = in.readLine()) != null; ) {
			sbuf.append(inputLine+"\n");
		}
		in.close();
		
		return sbuf.toString();
	}*/
	
	
	/**
	 * Returns the relevant HTML page content for further processing
	 * 
	 * @param urlString
	 *            URL to get plain content for
	 * @return Page content as plain text
	 * @throws IOException
	 */
	/*@Deprecated
	public String getRelevantHTMLPageContent(String urlString) throws IOException {
		 String enhancedURL = urlString + "?" + "username="+"ArtemVovk"+"&password="+"ia31tanatosx.1986";
		 URL url = new URL(enhancedURL);
//		 String data = "username="+"ArtemVovk"+"&password="+"ia31tanatosx.1986&noredirect=on";
		 URLConnection connection = url.openConnection();
		 connection.addRequestProperty("user-agent", "Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/532.0 (KHTML, like Gecko) Chrome/3.0.197.0 Safari/532.0");
		 //HttpURLConnection con = (HttpURLConnection)connection;
		 
	
		 
//		 connection.setDoInput(true);
//		 connection.setDoOutput(true);
//		 OutputStreamWriter writer = new OutputStreamWriter(connection
//					.getOutputStream());
//			writer.write(data);
//			writer.flush();
		 
		 //connection.getContentType()
		 
		 //String content = (String) connection.getContent();
		 
		 InputStream istream = connection.getInputStream();
		 SAXBuilder builder = new SAXBuilder(false);
		 builder.setEntityResolver(new W3CResolver());
		 
		 Document doc;
		 try {
			 doc = builder.build(istream);	 
		 } catch (JDOMException e) {
			 throw new IOException(e);
		 }
				
		 String relevantContent = getRelevantContent(doc);
				
		 return relevantContent;
	}*/
	
	/**
	 * Gets plain text from relevant content page
	 * @param urlString
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	/*public String getRelevantPlainPageContent(String urlString) throws IOException, SAXException, ParserConfigurationException {
		String htmlContent = getRelevantHTMLPageContent(urlString);
		return  xmlUtils.getPlainTextFromHTML(htmlContent);
	}*/
	
	/**
	 * Returns the plain text page content for further processing
	 * 
	 * @param urlString
	 *            URL to get plain content for
	 * @return Page content as plain text
	 * @throws IOException
	 * @throws IOException
	 */
	/*public String getPlainPageContent(String urlString) throws IOException {
		String htmlContent = getHTMLPageContent(urlString);
		String plainContent;
		try {
			plainContent = xmlUtils.getPlainTextFromHTML(htmlContent);
		} catch (SAXException e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		return plainContent;
	}*/

	/**
	 * Writes the content to the wiki page at the url. Make sure the content is
	 * marked up correctly for the specific wiki! No abstraction for that yet!
	 * 
	 * @param urlString
	 *            URL of the page where to write the content
	 * @param content
	 *            Content in wiki markup specific for the underlying wiki
	 * @throws IOException
	 */
	public abstract void writePage(String urlString, String content)
			throws IOException;

	/**
	 * Given a URL where the host identifies a wiki, returns a list of all pages
	 * as URL Strings
	 * 
	 * TODO this only works if the wiki system is installed at the default
	 * location. configuration is needed!
	 * 
	 * @return A list of URL Strings resembling all pages
	 * @throws IOException
	 */
	//@Deprecated
	//public abstract String[] getAllPages() throws IOException;
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract List<Page> getPages() throws IOException;
	
	/**
	 * Given a URL where the host identifies a wiki, returns a list of all pages
	 * in the given space/web as URL Strings. If a wiki system does not support
	 * this notion, all pages in the wiki will be returned
	 * 
	 * @param space
	 *            Name of space to list pages for
	 * @return URLs to all pages in this space (as String), or all pages of the
	 *         Wiki if spaces/webs are not supported
	 * @throws IOException
	 */
	/*@Deprecated
	public abstract String[] getAllPagesForSpace(String space)
			throws IOException;*/
		
	/**
	 * 
	 * @param space
	 * @return
	 * @throws IOException
	 */
	public abstract List<Page> getPagesForSpace(String space) throws IOException;
	
	public abstract Page getPageForURL(URL pageURL) throws IOException;
	
	public boolean hasNextPages(){
		return hasNext;
	}
	
	//@Deprecated
	//public abstract List<String> getNextPages() throws IOException;
	
	public abstract List<Page> getNextPagesAsObjects() throws IOException;

	/**
	 * Called by the getRelevantHTMLContent() method, this passes the JDom
	 * document to the subclass to find the element that contains the relevant
	 * content
	 * 
	 * @param doc
	 *            JDom document containing the complete HTML page
	 * @return String with the content of the relevant HTML element
	 * @throws IOException
	 */
	protected abstract String getRelevantContent(Document doc)
			throws IOException;

	public class W3CResolver implements EntityResolver {

		public InputSource resolveEntity(String publicId, String systemId) {
			if (systemId
					.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd")) {
				return new InputSource(this.getClass().getResourceAsStream(
						"/META-INF/resources/xhtml1-transitional.dtd"));
			}
			if (publicId.equals("-//W3C//ENTITIES Latin 1 for XHTML//EN")) {
				return new InputSource(this.getClass().getResourceAsStream(
						"/META-INF/resources/xhtml-lat1.ent"));
			}
			if (publicId.equals("-//W3C//ENTITIES Symbols for XHTML//EN")) {
				return new InputSource(this.getClass().getResourceAsStream(
						"/META-INF/resources/xhtml-symbol.ent"));
			}
			if (publicId.equals("-//W3C//ENTITIES Special for XHTML//EN")) {
				return new InputSource(this.getClass().getResourceAsStream(
						"/META-INF/resources/xhtml-special.ent"));
			}
			return null;
		}
	}
}
