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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import de.tudarmstadt.ukp.wikiapi.type.MediaWikiPage;
import de.tudarmstadt.ukp.wikiapi.type.Page;

/**
 * A Class for getting several document source as well as several document parts
 * of a MediaWiki page.
 * 
 * @author J. Hoffart
 * @author A. Vovk
 * @author Fabian L. Tamin
 * 
 */
public class MediaWiki extends Wiki {
	
	

	private Pattern titleBracePattern = Pattern.compile(" \\(.*\\)");
	
	/**
	 * @param apiURL
	 * @param viewURL
	 */
	public MediaWiki(URL apiURL, URL viewURL) {
		super(apiURL, viewURL);
		//TODO hack fuer auth.:
		//this.viewURL = viewURL;
		this.apiURL = apiURL;
		login = "WiKulu";
		password ="NLP4Wiki";
		
	}
	
	/**
	 * Base URL for the Wiki, appending a wiki page title to this URL must
	 * result in a valid URL to the page.
	 */
//	private URL viewURL;

	// page to start next interation
	private String apFrom = null;

	private static int articleLimitPerIteration = 500;

//	public URL getWikiURL() {
//		return viewURL;
//	}
//
//	public void setWikiURL(URL wikiURL) {
//		this.viewURL = wikiURL;
//	}

	@Override
	public String getTitleForURL(String url)
	{
		String[] parts = url.split("/");
		String titlePart = parts[parts.length-1];
		
		String title = titlePart.replace("_", " ");
		
		String variant = title;
				
		Matcher m = titleBracePattern.matcher(title);
	
		if (m.find()) {
			variant = title.substring(0, m.start());
		}
		
		return variant;
	}

/*	public String getRelevantPlainPageContentFromWiki(String url)
			throws SAXException, ParserConfigurationException, IOException {*/
		/*MarkupParser markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(new MediaWikiLanguage());

		StringBuffer out = new StringBuffer();
		markupParser.setBuilder(new PlainTextDocumentBuilder(out));

		String wikiContent = this.getWikiPageContent(url);
		markupParser.parse(wikiContent);

		return out.toString();*/
		/*MediaWikiPage page = new MediaWikiPage(this, this.getCurrentPageTitle(new URL(url)));
		return page.getPlainContentFromWikiSyntax();
	}*/

	/*public String getWikiPageContent(String urlString) throws IOException {
		/*URL url = new URL(urlString);

		String request = apiURL.toExternalForm()
				+ "?action=query&prop=revisions&rvlimit=1&rvprop=content&format=xml&titles="
				+ getCurrentPageTitle(url) + "&redirects"; // resolve
															// redirection
															// automatically

		URL requestUrl = new URL(request);

		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(requestUrl);
		} catch (JDOMException e) {
			throw new IOException(e);
		}

		Iterator itr = doc.getDescendants(new Filter() {
			@Override
			public boolean matches(Object o) {
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

		return wikiContent;*/
		/*MediaWikiPage page = new MediaWikiPage(this, this.getCurrentPageTitle(new URL(urlString)));
		return page.getWikiSyntaxContent();
	}*/

	public String getCurrentPageTitle(URL url) {
		String path = url.getPath();
		String[] components = path.split("/");
		return components[components.length - 1];
	}

	@Override
	public void writePage(String urlString, String content) throws IOException {
		if(login != null && password !=null) {
			writePage(urlString, content, login, password);
		}
		
		URL url = new URL(urlString);

		String editToken;
		try {
			editToken = getEditToken(urlString);
		} catch (JDOMException e) {
			throw new IOException(e);
		}

		// construct the data string to edit page
		String data = "action=edit" + "&token="
				+ URLEncoder.encode(editToken, "UTF-8") + "&title="
				+ getCurrentPageTitle(url) + "&text="
				+ URLEncoder.encode(content, "UTF-8");

		// POST the data to the appropriate URL
		URLConnection connection = apiURL.openConnection();
		connection.setDoOutput(true);

		OutputStreamWriter writer = new OutputStreamWriter(connection
				.getOutputStream());
		writer.write(data);
		writer.flush();

		// TODO check if successful here!
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		// String line = null;
		//		
		// System.out.println("response:");
		//		
		// while ((line = reader.readLine()) != null)
		// System.out.println(line);

		reader.close();
		writer.close();
	}
	
	
	
	private HashMap<String, String> sessionInfo = new HashMap<String, String>();

	private void authorizeFirstPhase()
		throws IOException
	{
		// send user information via post
		String auth = "action=login&lgname=" + login + "&lgpassword="
				+ password + "&format=json";
		URLConnection connectionAuth = apiURL.openConnection();

		sendConnectionRequest(connectionAuth, auth);

		// read header and save sessionId
		String sessionId = null;
		for (int i = 0;; i++) {

			String name = connectionAuth.getHeaderFieldKey(i);
			String value = connectionAuth.getHeaderField(i);

			if (name == null && value == null) {
				break;
			}
			if (name != null) {
				if (name.equals("Set-Cookie"))
					sessionId = value;
				System.out.println(name + "!=!" + value);
			}
		}

		// read response
		String response = getConnectionResponse(connectionAuth);
		String token = null;

		try {
			JSONObject a = new JSONObject(response);
			JSONObject loginInfo = (JSONObject) a.get("login");
			token = loginInfo.getString("token");
			System.out.println("Token is:" + token);
		}
		catch (JSONException e1) {
			e1.printStackTrace();
		}

		sessionInfo.put("sessionId", sessionId);
		sessionInfo.put("token", token);

	}

	public void authorizeSecondPhase()
		throws IOException
	{
		String confirm = "action=login&lgname=" + login + "&lgpassword="
				+ password + "&lgtoken=" + sessionInfo.get("token")
				+ "&format=json";

		URLConnection connectionAuth = apiURL.openConnection();
		connectionAuth.addRequestProperty("Cookie",
				sessionInfo.get("sessionId"));

		sendConnectionRequest(connectionAuth, confirm);

		String response = getConnectionResponse(connectionAuth);

		String userName = null;
		String userId = null;
		try {
			JSONObject a = new JSONObject(response);
			JSONObject loginInfo = (JSONObject) a.get("login");
			String lgtoken = loginInfo.getString("lgtoken");
			sessionInfo.put("lgtoken", lgtoken);

			System.out.println("Token is:" + lgtoken);
			userName = loginInfo.getString("lgusername");

			userId = loginInfo.getString("lguserid");
			sessionInfo.put("userName", userName);
			sessionInfo.put("userId", userId);
		}
		catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

	private String getConnectionResponse(URLConnection connection)
		throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String line = null;
		System.out.println("response:");
		StringBuffer output = new StringBuffer();
		// save response in stringbuffer
		while ((line = reader.readLine()) != null)
			output.append(line);
		reader.close();
		System.out.println(output);
		return output.toString();
	}

	private void sendConnectionRequest(URLConnection connection, String request)
		throws IOException
	{
		connection.setDoOutput(true);

		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(request);
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	private String getEditToken2(String title)
		throws IOException
	{
		String path = "action=query&format=json&prop=info|revisions&intoken=edit&titles="
				+ title;

		URLConnection connection = apiURL.openConnection();
		String cookie = sessionInfo.get("sessionId") + "; wikiUserName="
				+ sessionInfo.get("userName") + "; wikiUserID="
				+ sessionInfo.get("userId") + "; wikiToken="
				+ sessionInfo.get("token");
		System.out.println("Cookie: " + cookie);
		connection.addRequestProperty("Cookie", cookie);

		sendConnectionRequest(connection, path);

		String response = getConnectionResponse(connection);

		String outtoken = null;

		try {
			JSONObject a = new JSONObject(response.toString());
			JSONObject query = (JSONObject) a.get("query");
			JSONObject pages = (JSONObject) query.get("pages");
			Iterator keys =pages.keys();
			String next = (String)keys.next();
			JSONObject editToken = (JSONObject)pages.get(next);
			
			outtoken = editToken.getString("edittoken");
			System.out.println("Edittoken is:" + outtoken);
		}
		catch (JSONException e1) {
			e1.printStackTrace();
		}

		return outtoken;
	}

	public void writePage(String urlString, String content, String login,
			String password)
		throws IOException
	{

		URL url = new URL(urlString);

		this.login = login;

		this.password = password;

		authorizeFirstPhase();

		authorizeSecondPhase();

		String pageTitle = getCurrentPageTitle(url);
		String outtoken = getEditToken2(pageTitle);

		String data = "action=edit&token="
				+ URLEncoder.encode(outtoken, "UTF-8")
				+ "&title="+pageTitle+"&text="+URLEncoder.encode(content, "UTF-8")+"&format=json";

		// POST the data to the appropriate URL
		URLConnection connection = apiURL.openConnection();
		String cookie = sessionInfo.get("sessionId") + "; wikiUserName=" + sessionInfo.get("userName")
				+ "; wikiUserID=" + sessionInfo.get("userId") + "; wikiToken=" + sessionInfo.get("token");
		System.out.println("Cookie: " + cookie);
		connection.addRequestProperty("Cookie", cookie);

		sendConnectionRequest(connection, data);

		// Check if successful here!
		System.out.println(getConnectionResponse(connection));

	}
	
	
	

	private String getEditToken(String urlString) throws JDOMException,
			IOException {
		URL url = new URL(urlString);

		String path = apiURL.toExternalForm()
				+ "?action=query&format=xml&prop=info|revisions&intoken=edit&titles="
				+ getCurrentPageTitle(url);

		URL requestUrl = new URL(path);

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(requestUrl);

		XPath xpath = XPath.newInstance("//page/@edittoken");
		Attribute editToken = (Attribute) xpath.selectSingleNode(doc
				.getRootElement());
		String token = editToken.getValue();

		return token;
	}

	/**
	 * Gets relevant Text content from wikipage
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected String getRelevantContent(Document doc) throws IOException {
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
				} else if (e.getAttribute("class") != null
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
	// public String[] getAllPages(String urlString) throws IOException {
	// URL url = new URL(urlString);
	//		
	// String path = API_BASE_PATH
	// +
	// "?action=query&format=xml&list=allpages&aplimit=5000&apfilterredir=nonredirects";
	// boolean isEnd = false;
	// List<String> pageURLs = new LinkedList<String>();
	//		
	//		
	// URL requestUrl = new URL(url.getProtocol(), url.getHost(), path);
	//
	// SAXBuilder builder = new SAXBuilder();
	// Document doc;
	// try {
	// doc = builder.build(requestUrl);
	// } catch (JDOMException e) {
	// throw new IOException(e);
	// }
	//		
	//
	// Iterator itr = doc.getDescendants(new Filter() {
	// @Override
	// public boolean matches(Object o) {
	// if (o instanceof Element) {
	// Element e = (Element) o;
	//
	// return e.getName().equals("p");
	// }
	// return false;
	// }
	// });
	//		
	//		
	//		
	// while (itr.hasNext()) {
	// Element e = (Element) itr.next();
	// String pageTitle = e.getAttributeValue("title");
	// String pageURL = getURLForTitle(url, pageTitle);
	// pageURLs.add(pageURL);
	// }
	//		
	// return pageURLs.toArray(new String[pageURLs.size()]);
	//		
	// }
	/*@Deprecated
	public String[] getAllPages() throws IOException {
		List<Page> allPages = this.getPages();
		ArrayList<String> urls = new ArrayList<String>();
		for(Page p : allPages) {
			urls.add(p.getArticleUrl().toExternalForm());
		}
		return urls.toArray(new String[urls.size()]);
	}*/

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<Page> getPages() throws IOException {
		List<Page> all = new ArrayList<Page>();
		while (this.hasNextPages()) {
			List<Page> nextPages = this.getNextPagesAsObjects();
			for(Page p : nextPages) {
				String url = p.getArticleUrl().toString().replace(this.viewUrl.toExternalForm(), "");
				// remove "File:"- and similar pages
				if(url.contains(":")) {
					if(Character.toString(url.charAt(url.indexOf(":")+1)).equals("_")) {
						all.add(p);
					}
				} else {
					all.add(p);
				}
			}
		}
		return all;
	}
	
	/*private List<String> getNextPages() throws IOException {
		List<Page> pages = this.getNextPagesAsObjects();
		List<String> pageUrls = new LinkedList<String>();
		for (int x = 0; x < pages.size(); x++) {
			Page p = pages.get(x);
			pageUrls.add(p.getArticleUrl().toExternalForm());
		}
		return pageUrls;
	}*/

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<Page> getNextPagesAsObjects() throws IOException {
		String request;
		if (apFrom == null) {
			request = apiURL.toExternalForm()
					+ "?action=query&format=xml&list=allpages&aplimit="
					+ MediaWiki.articleLimitPerIteration
					+ "&apfilterredir=nonredirects";
		} else {
			request = apiURL.toExternalForm()
					+ "?action=query&format=xml&list=allpages&aplimit="
					+ MediaWiki.articleLimitPerIteration
					+ "&apfilterredir=nonredirects&apfrom=" + apFrom;
		}
		
		List<String> pageURLs = new LinkedList<String>();
		List<String> ids = new LinkedList<String>();

		URL requestUrl = new URL(request);
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(requestUrl);
		} catch (JDOMException e) {
			throw new IOException(e);
		}

		Iterator itrNextPages = doc.getDescendants(new Filter() {
			/**
            *
            */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o) {
				if (o instanceof Element) {
					Element e = (Element) o;

					return e.getName().equals("allpages");
				}
				return false;
			}
		});
		int count = 0;
		while (itrNextPages.hasNext()) {
			Element e = (Element) itrNextPages.next();
			String pageTitle = e.getAttributeValue("apfrom");
			if (pageTitle != null){
				apFrom = pageTitle.replaceAll(" ", "_");
				
			}
			
			count++;
		}

		if (count != 2)
			hasNext = false;

		Iterator itr = doc.getDescendants(new Filter() {
			/**
            *
            */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o) {
				if (o instanceof Element) {
					Element e = (Element) o;

					return e.getName().equals("p");
				}
				return false;
			}
		});

		while (itr.hasNext()) {
			//TODO add attribute values directly to a new Page object
			Element e = (Element) itr.next();
			String pageTitle = e.getAttributeValue("title");
			
			String pageURL = getURLForTitle(pageTitle);
			pageURLs.add(pageURL);
			String id = e.getAttributeValue("pageid");
			ids.add(id);
		}

		List<Page> pages = new LinkedList<Page>();
		for (int x = 0; x < pageURLs.size(); x++) {
			Page p = new MediaWikiPage(this, pageURLs.get(x).replaceAll(this.getViewUrl().toExternalForm(), ""));
			p.setID(ids.get(x));
			//p.setArticleUrl(pageURLs.get(x));
			pages.add(p);
		}
		return pages;
	}

	// public boolean hasNextPages(){
	// return hasNext;
	// }

	//@SuppressWarnings("unchecked")
	/*
	 * public List<String> getNextPages() throws IOException { String request;
	 * if(apFrom == null){ request = apiURL.toExternalForm() +
	 * "?action=query&format=xml&list=allpages&aplimit="
	 * +MediaWiki.articleLimitPerIteration+"&apfilterredir=nonredirects"; }else{
	 * request = apiURL.toExternalForm() +
	 * "?action=query&format=xml&list=allpages&aplimit="
	 * +MediaWiki.articleLimitPerIteration
	 * +"&apfilterredir=nonredirects&apfrom="+apFrom; }
	 * 
	 * List<String> pageURLs = new LinkedList<String>();
	 * 
	 * URL requestUrl = new URL(request);
	 * 
	 * SAXBuilder builder = new SAXBuilder(); Document doc; try { doc =
	 * builder.build(requestUrl); } catch (JDOMException e) { throw new
	 * IOException(e); }
	 * 
	 * Iterator itrNextPages = doc.getDescendants(new Filter() { /**
	 */
	/*
	 * private static final long serialVersionUID = 1L;
	 * 
	 * @Override public boolean matches(Object o) { if (o instanceof Element) {
	 * Element e = (Element) o;
	 * 
	 * return e.getName().equals("allpages"); } return false; } }); int count =
	 * 0; while (itrNextPages.hasNext()) { Element e = (Element)
	 * itrNextPages.next(); String pageTitle = e.getAttributeValue("apfrom");
	 * if(pageTitle !=null) apFrom = pageTitle;
	 * 
	 * count++; }
	 * 
	 * if(count !=2) hasNext =false;
	 * 
	 * 
	 * Iterator itr = doc.getDescendants(new Filter() { /**
	 */
	/*
	 * private static final long serialVersionUID = 1L;
	 * 
	 * @Override public boolean matches(Object o) { if (o instanceof Element) {
	 * Element e = (Element) o;
	 * 
	 * return e.getName().equals("p"); } return false; } });
	 * 
	 * while (itr.hasNext()) { Element e = (Element) itr.next(); String
	 * pageTitle = e.getAttributeValue("title"); String pageURL =
	 * getURLForTitle(pageTitle); pageURLs.add(pageURL); }
	 * 
	 * return pageURLs;
	 * 
	 * }
	 */
	private String getURLForTitle(String pageTitle)
			throws MalformedURLException {
		String pageURL = this.getViewUrl() + pageTitle.replaceAll(" ", "_");
		return pageURL;
	}

	/**
	 * MediaWiki does not support spaces/webs
	 */
	/*@Override
	@Deprecated
	public String[] getAllPagesForSpace(String space) throws IOException {
		//return getAllPages();
	}*/

	/**
	 * Gets MediaWiki categories.
	 * 
	 * @param urlString
	 *            URL
	 * @return set of categories (limit=50)
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getCategories(String urlString) throws IOException {
		return getCategories(urlString, 50);
	}

	/**
	 * Gets MediaWiki Categories.
	 * 
	 * @param urlString
	 *            URL
	 * @param limit
	 *            maximum categories returned (max = 500)
	 * @return set of categories
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getCategories(String urlString, int limit)
			throws IOException {
		if (limit > 500)
			limit = 500;

		String sub_query = "?action=query&prop=categories&cllimit=" + limit
				+ "&format=xml&titles=";
		String entry_id = "cl";

		return getEntries(urlString, sub_query, entry_id);
	}

	/**
	 * Gets backlinks (only main namespace).
	 * 
	 * @param urlString
	 *            URL
	 * @return set of backlinks (limit 50)
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getBacklinks(String urlString) throws IOException {
		return getBacklinks(urlString, 50);
	}

	/**
	 * Gets backlinks (only main namespace).
	 * 
	 * @param urlString
	 *            URL
	 * @param limit
	 *            maximum backlinks returned (max = 500)
	 * @return a set of backlinks
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getBacklinks(String urlString, int limit)
			throws IOException {
		if (limit > 500)
			limit = 500;

		String sub_query = "?action=query&list=backlinks&bllimit=" + limit
				+ "&blnamespace=0&format=xml&bltitle=";
		String entry_id = "bl";

		return getEntries(urlString, sub_query, entry_id);
	}

	/**
	 * Gets article links (only main namespace).
	 * 
	 * @param urlString
	 *            URL
	 * @return a set of article links
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getArticleLinks(String urlString) throws IOException {
		return getArticleLinks(urlString, 50);
	}

	/**
	 * Gets article links (only main namespace).
	 * 
	 * @param urlString
	 *            URL
	 * @param limit
	 *            maximal links returned (max = 500)
	 * @return a set of article links
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	public Set<String> getArticleLinks(String urlString, int limit)
			throws IOException {
		if (limit > 500)
			limit = 500;

		String sub_query = "?action=query&prop=links&pllimit=" + limit
				+ "&plnamespace=0&format=xml&titles=";
		String entry_id = "pl";

		return getEntries(urlString, sub_query, entry_id);
	}

	/**
	 * Get entries using from WikiAPI. Redirection is active.
	 * 
	 * @param urlString
	 *            URL
	 * @param sub_query
	 *            query parameters
	 * @param entry_id
	 *            name of entry ID, e.g. <code>bl</code> for backlink
	 * @return a set of entries
	 * @throws MalformedURLException
	 *             it is thrown if URL is malformed
	 * @throws IOException
	 *             it is thrown if building XML DOM is failed
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getEntries(String urlString, final String sub_query,
			final String entry_id) throws MalformedURLException, IOException {
		URL url = new URL(urlString);

		String path = apiURL.toExternalForm() + sub_query
				+ getCurrentPageTitle(url) + "&redirects";

		URL requestUrl = new URL(path);

		SAXBuilder builder = new SAXBuilder();
		builder.setEntityResolver(new W3CResolver());

		Document doc;
		try {
			doc = builder.build(requestUrl);
		} catch (JDOMException e) {
			throw new IOException(e);
		}

		Iterator itr = doc.getDescendants(new Filter() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o) {
				if (o instanceof Element) {
					Element e = (Element) o;

					return e.getName().equals(entry_id);
				}

				return false;
			}
		});

		TreeSet<String> set = new TreeSet<String>();

		while (itr.hasNext()) {
			Element e = (Element) itr.next();
			set.add(e.getAttributeValue("title"));
		}

		return set;
	}

	@Override
	public List<Page> getPagesForSpace(String space) throws IOException {
		return this.getNextPagesAsObjects();
	}

	@Override
	public Page getPageForURL(URL pageURL) throws IOException {
		// TODO IMPLEMENT
		// Use combination from viewURL and Title??
//		System.out.println("RUF_M: pageURL:"+pageURL.toExternalForm());
//		String replace = this.viewURL.getPath();
//		
////		if(pageURL.toExternalForm().contains("http://localhost/index.php/")){
////			String tmp = pageURL.toExternalForm().substring(pageURL.toExternalForm().lastIndexOf("/"));
////			return new MediaWikiPage(this, tmp);
////		}
////			
//		
//		
//		String page = null;
//		if(replace.equals("/"))
//			page = pageURL.getPath();
//		else
//			page = pageURL.getPath().replace(this.viewURL.getPath(), "");
		//String urlString = pageURL.toExternalForm();
		//MediaWikiPage page = new MediaWikiPage(this, urlString.replace(this.wikiURL.toExternalForm(), ""));
		//return new MediaWikiPage(this, pageURL.toExternalForm().replace(this.viewURL.toExternalForm(), ""));
		
		
		String[] parts = pageURL.toExternalForm().split("/");
		this.viewUrl = new URL("http" + this.viewUrl.toExternalForm().split("http")[1]);

		return new MediaWikiPage(this, parts[parts.length-1]);
	}
	
	@Override
	public String createLink(String word, String linkCandidateName){
		String title = this.getTitleForURL(linkCandidateName);
		return "[["+title+"|"+word+"]]";
	}

	@Override
	public String getRelativeURL(String fullURLString) {
		return fullURLString.replaceFirst("(http|https)(://)[A-Za-z0-9_.:-]*", "");
	}
	
//	private Page
}
