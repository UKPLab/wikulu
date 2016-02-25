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
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Modifies the servlet response to use the InjectionWrappedOutputStream.
 * 
 * @author hoffart
 *
 */
public class InjectionServletResponse extends HttpServletResponseWrapper {
	private HttpServletRequest request;
	private ResourceBundle bundle;

	public InjectionServletResponse(HttpServletResponse arg0,
			HttpServletRequest request) throws MalformedURLException {
		super(arg0);
		// this "blocks" requests from other websites than the wiki adress from wikulu.properties
		this.bundle = PropertyResourceBundle.getBundle("wikulu");
		URL baseUrl = new URL(request.getRequestURL().toString());
		URL wikiUrl = new URL(this.bundle.getString("wikulu.targetPlatformBaseURL") + "" + this.bundle.getString("wikulu.targetPlatformWikiURL"));
		URL serverUrl = new URL(this.bundle.getString("wikulu.proxyLocationHost"));
		if(wikiUrl.getHost().equals(baseUrl.getHost()) || baseUrl.getHost().equals(serverUrl.getHost())) {
			this.request = request;
		} else {
			// TODO: return an error 500
			this.request = null;
		}
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return new InjectionWrappedOutputStream(super.getOutputStream(), this,
				request);
	}

	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(super.getOutputStream(), true);
	}

	public String toString() {
		return request.toString();
	}
}
