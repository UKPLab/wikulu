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
package de.tudarmstadt.ukp.wikiproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.servlet.ProxyServlet;
import org.mortbay.util.IO;

/**
 *
 * @author hoffart
 *
 */
public class InjectionService
	extends ProxyServlet
{

	protected ServletContext context;

	public static ResourceBundle properties;
	// Set with headers to avoid
	protected HashSet<String> _DontProxyHeaders = new HashSet<String>();

	{
		// Fill set with values
		_DontProxyHeaders.add("proxy-connection");
		_DontProxyHeaders.add("connection");
		_DontProxyHeaders.add("keep-alive");
		_DontProxyHeaders.add("transfer-encoding");
		_DontProxyHeaders.add("te");
		_DontProxyHeaders.add("trailer");
		_DontProxyHeaders.add("proxy-authorization");
		_DontProxyHeaders.add("proxy-authenticate");
		_DontProxyHeaders.add("upgrade");
		_DontProxyHeaders.add("accept-encoding");
		_DontProxyHeaders.add("content-length");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
		throws ServletException
	{
		this.context = config.getServletContext();
		// init resourceBundle
		properties = PropertyResourceBundle.getBundle("wikulu");
	}

	/**
	 * This code is mainly taken from the ProxyServlet example of the Jetty
	 * project, augmented by the AUGUR project of TK TU Darmstadt
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res)
		throws ServletException, IOException
	{
		// http request
		HttpServletRequest request = (HttpServletRequest) req;
		// http responce
		HttpServletResponse response = (HttpServletResponse) res;
		
		// Check request method
		if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
			handleConnect(request, response);
		}
		else {
			// create URI
			String uri = request.getRequestURI();

			if (request.getQueryString() != null) {
				uri += "?" + request.getQueryString();
			}

			// Create URL from URI and request
			// direct proxy has another URL as just normal proxy
			URL url = createURL(uri, request);
			// Open Connection
			URLConnection connection = url.openConnection();
			connection.setAllowUserInteraction(false);

			// Set method
			HttpURLConnection http = null;

			if (connection instanceof HttpURLConnection) {
				http = (HttpURLConnection) connection;
				http.setRequestMethod(request.getMethod());
				http.setInstanceFollowRedirects(false);
			}

			// copy headers
			boolean hasContent = copyHeadersToConnection(request, url,
					connection);

			// a little bit of cache control
			String cache_control = request.getHeader("Cache-Control");
			if (cache_control != null
					&& (cache_control.indexOf("no-cache") >= 0 || cache_control
							.indexOf("no-store") >= 0)) {
				connection.setUseCaches(false);
			}

			copyStreamFromRequestToConnection(connection, request, hasContent);

			// input stream from response
			InputStream proxy_in = null;

			// handler status codes etc.
			int code = 500;
			if (http != null) {
				try {
					proxy_in = connection.getInputStream();
				}
				catch (IOException e) {
					proxy_in = http.getErrorStream();
					context.log("File not found: " + e.getLocalizedMessage());
				}

				code = http.getResponseCode();
				response.setStatus(code);
			}

			setResponseHeaders(connection, response);

			// Handle
			if (proxy_in != null) {
				IO.copy(proxy_in, response.getOutputStream());
			}

		}
	}

	/**
	 * Creates URL from URI appropriate to settings(for direct proxy or for
	 * proxy)
	 *
	 * @param uri
	 *            uri to create an URL
	 * @param request
	 *            request
	 * @return new URL
	 * @throws MalformedURLException
	 */
	private URL createURL(String uri, HttpServletRequest request)
		throws MalformedURLException
	{
		URL url = null;
		String targetServer = null;

		if (properties.getString("wikulu.direct_connection").equals("yes")) {
			// use direct_connection
			URL targetSite = new URL(properties.getString("wikulu.targetPlatformBaseURL") + "" +
					properties.getString("wikulu.targetPlatformWikiURL"));

			targetServer = targetSite.getHost();
			String targetURI = null;
			if (uri.equals("/")) {
				targetURI = targetSite.getPath();
			}
			else {
				targetURI = uri;
			}

			// use ssl
			if (properties.getString("wikulu.useSSL").equals("yes")) {
				url = new URL("https", targetServer, 443, targetURI);
			}
			else {
				url = new URL("http", targetServer, 80, targetURI);
			}
		}
		else {
			// use proxy
			url = new URL(request.getScheme(), request.getServerName(),
					request.getServerPort(), uri);
		}

		return url;
	}

	/**
	 * Copy headers ro request properties
	 *
	 * @param request
	 *            request that contains headers
	 * @param url
	 *            url of the page
	 * @param connection
	 *            connection to headers
	 * @return has content
	 */
	private boolean copyHeadersToConnection(HttpServletRequest request,
			URL url, URLConnection connection)
	{
		// check connection header
		String connectionHdr = request.getHeader("Connection");
		if (connectionHdr != null) {
			connectionHdr = connectionHdr.toLowerCase();
			if (connectionHdr.equals("keep-alive")
					|| connectionHdr.equals("close")) {
				connectionHdr = null;
			}
		}

		// copy headers
		boolean xForwardedFor = false;
		boolean hasContent = false;
		Enumeration enm = request.getHeaderNames();

		while (enm.hasMoreElements()) {
			String hdr = (String) enm.nextElement();
			String lhdr = hdr.toLowerCase();

			// avoid proxy headers contained in HashSet
			if (_DontProxyHeaders.contains(lhdr)) {
				continue;
			}

			if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0) {
				continue;
			}

			if ("content-type".equals(lhdr)) {
				hasContent = true;
			}

			Enumeration vals = request.getHeaders(hdr);
			while (vals.hasMoreElements()) {
				String val = (String) vals.nextElement();
				if (val != null) {
					// TODO: DIPF hack ...
					if (hdr.toLowerCase().equals("host")
							&& properties.getString("wikulu.direct_connection")
									.equals("yes")
							&& url.getHost().equals("wiki.bildungsserver.de")) {
						val = url.getHost();
					}
					connection.addRequestProperty(hdr, val);
					xForwardedFor |= "X-Forwarded-For".equalsIgnoreCase(hdr);

				}
			}
		}

		// Proxy headers
		connection.setRequestProperty("Via", "1.1 (jetty)");
		if (!xForwardedFor) {
			connection.addRequestProperty("X-Forwarded-For",
					request.getRemoteAddr());
		}

		return hasContent;
	}

	/**
	 * Set response headers
	 *
	 * @param connection
	 *            connection to get headers
	 * @param response
	 *            response to set headers
	 * @throws MalformedURLException
	 */
	private void setResponseHeaders(URLConnection connection,
			HttpServletResponse response)
		throws MalformedURLException
	{
		// clear response defaults.
		response.setHeader("Date", null);
		response.setHeader("Server", null);

		// set response headers
		int h = 0;
		String hdr = connection.getHeaderFieldKey(h);
		String val = connection.getHeaderField(h);
		while (hdr != null || val != null) {
			String lhdr = hdr != null ? hdr.toLowerCase() : null;
			if (hdr != null && val != null && !_DontProxyHeaders.contains(lhdr)) {
				// change location header
				if (hdr.equals("Location")
						&& properties.getString("wikulu.direct_connection")
								.equals("yes")) {
					URL valURL = new URL(val);
					URL hostURL = new URL(
							properties.getString("wikulu.proxyLocationHost"));
					URL newURL = new URL(hostURL.getProtocol(),
							hostURL.getHost(), hostURL.getPort(),
							valURL.getPath());

					response.addHeader(hdr, newURL.toExternalForm());
				}
				else {
					response.addHeader(hdr, val);
				}

				if ("Content-Type".equals(hdr)) {
					response.setContentType(val);
				}
			}

			h++;
			hdr = connection.getHeaderFieldKey(h);
			val = connection.getHeaderField(h);
		}
		response.addHeader("Via", "1.1 (jetty)");

		// response.setContentType(res.getContentType());
		// response.setCharacterEncoding(res.getCharacterEncoding());

	}

	/**
	 * Copy InputStream from request to connection
	 *
	 * @param connection
	 *            in this connection stream will be copied
	 * @param request
	 *            request with inputstream to copy
	 * @param hasContent
	 *            hasContent
	 */
	private void copyStreamFromRequestToConnection(URLConnection connection,
			HttpServletRequest request, boolean hasContent)
	{
		// customize Connection
		try {
			connection.setDoInput(true);
			// do input thang!
			InputStream in = request.getInputStream();
			if (hasContent) {
				connection.setDoOutput(true);
				// copy inputStream from request to connection
				IO.copy(in, connection.getOutputStream());
			}
			// Connect
			connection.connect();
		}
		catch (Exception e) {
			context.log("proxy", e);
		}
	}
}
