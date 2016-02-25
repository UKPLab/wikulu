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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

import de.tudarmstadt.ukp.wikiapi.type.Page;
import de.tudarmstadt.ukp.wikiapi.type.TWikiPage;

public class TWiki extends Wiki {

	/**
	 * @param apiURL
	 * @param viewURL
	 */
	public TWiki(URL apiURL, URL viewURL) {
		super(apiURL, viewURL);
	}

	public TWiki(URL apiURL, URL viewURL, String authData) {
		super(apiURL, viewURL, authData);
	}

	@Override
	public String getTitleForURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWebTopic(URL url) {
		// FIXME this does not work for hierarchical topics, only for
		// first-level ones
		String path = url.getPath();
		String[] components = path.split("/");
		String web = components[components.length - 2];
		String topic = components[components.length - 1];
		String webTopic = web + "." + topic;

		return webTopic;
	}

	@Override
	public void writePage(String urlString, String content) throws IOException {

		URL url = new URL(urlString);

		URL queryURL = new URL(apiURL.toExternalForm() + "writewikicontent");

		String topic = getWebTopic(url);

		String data;

		data = "topic=" + topic + "&text="
				+ URLEncoder.encode(content, "UTF-8");

		URLConnection connection = queryURL.openConnection();
		connection
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.1.5) Gecko/20091109 Ubuntu/9.10 (karmic) Firefox/3.5.5");
		connection.setDoOutput(true);
		if (this.getAuthData() != null)
			connection.addRequestProperty("Cookie", this.getAuthData());

		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(data);
		writer.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String line = null;

		System.out.println("response:");

		while ((line = reader.readLine()) != null)
			System.out.println(line);

		reader.close();
		writer.close();

	}

	@SuppressWarnings("unchecked")
	protected String getRelevantContent(Document doc) throws IOException {
		Iterator itr = doc.getDescendants(new Filter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean matches(Object o) {
				if (o instanceof Element) {
					Element e = (Element) o;

					if (e.getAttribute("class") != null
							&& e.getAttributeValue("class").equals(
									"patternContent")) {
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

	/*
	 * public List<String> getNextPages() throws IOException { return null; }
	 */

	@Override
	public List<Page> getPages() throws IOException {
		String query = apiURL.toExternalForm() + "alltopics";

		URL queryURL = new URL(query);
		URLConnection connection = queryURL.openConnection();
		connection
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.1.5) Gecko/20091109 Ubuntu/9.10 (karmic) Firefox/3.5.5");
		connection.setDoOutput(true);
		if (this.getAuthData() != null)
			connection.addRequestProperty("Cookie", this.getAuthData());

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		List<Page> pageURLs = new LinkedList<Page>();

		String line = null;

		while ((line = reader.readLine()) != null) {
			String pageURL = this.viewUrl + line;
			if(line.startsWith("Wikulu") && !line.startsWith("Wikulu/Web")) {
				pageURLs.add(new TWikiPage(this, pageURL));
			}
			// FIXME get relevant pages only (not TWiki pages and
			// not things like WebIndex or WebHome (these pages
			// confuse MarkupParser -> get new parser that can handle them) 
			// FIXME create "blacklist" for unwanted URLs (-> regex)?
		}

		return pageURLs;
	}

	@Override
	public List<Page> getPagesForSpace(String space) throws IOException {
		String query = apiURL.toExternalForm() + "alltopicsinweb";

		query += "?web=" + space;
		String cookie = this.getAuthData();
		// String cookie =
		// "JSESSIONID="+this.sessionID+";TWIKISID="+this.twikisID;
		URL queryURL = new URL(query);

		HttpURLConnection connection = (HttpURLConnection) queryURL
				.openConnection();
		connection
				.addRequestProperty(
						"user-agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.127 Safari/534.162011-03-09 06:14:55");
		connection.setDoOutput(true);
		connection.addRequestProperty("Cookie", cookie);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		List<Page> pageURLs = new LinkedList<Page>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			String pageURL = line;
			pageURLs.add(new TWikiPage(this, pageURL));
		}

		return pageURLs;
	}

	@Override
	public List<Page> getNextPagesAsObjects() throws IOException {
		return null;
	}

	@Override
	public Page getPageForURL(URL pageURL) throws IOException {
		String pagePath = pageURL.getPath();
		String viewPath = viewUrl.getPath();

		String relativeURL = pagePath.replace(viewPath, "");
		Page page = new TWikiPage(this, relativeURL);
		return page;
	}
	
	@Override
	public String createLink(String word, String linkCandidateName){
		return "[["+linkCandidateName+"]["+word+"]]";
	}

	@Override
	public String getRelativeURL(String fullURLString) {
		return fullURLString.replaceFirst("(http|https)(://)[A-Za-z0-9_.:-]*", "");
		
	}

}
