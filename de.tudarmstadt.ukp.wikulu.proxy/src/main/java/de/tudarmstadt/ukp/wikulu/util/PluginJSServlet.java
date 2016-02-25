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
package de.tudarmstadt.ukp.wikulu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class PluginJSServlet
	extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PathMatchingResourcePatternResolver pm;

	public void service(HttpServletRequest req, HttpServletResponse res)
	{
		String url = req.getRequestURL().toString();
		String[] parts = url.split("/");

		// the last part of the URL is the filename
		String fileName = parts[parts.length - 1];

		if (pm == null) {
			pm = new PathMatchingResourcePatternResolver();
		}
		// the search pattern for the file
		String javascriptFilePattern = "classpath*:/META-INF/**/" + fileName;
		 

		try {
			Resource[] resources = pm.getResources(javascriptFilePattern);
			
				//for (int x = 0; x < resources.length; x++) {
			for(Resource r : resources) {
					boolean fileExists = r.exists();
					if (fileExists) {
						InputStream jsFile = r.getInputStream();
						
						InputStreamReader in = new InputStreamReader(jsFile);
						BufferedReader buf = new BufferedReader(in);
						String line = null;
						StringBuffer content = new StringBuffer();
						while ((line = buf.readLine()) != null) {
							content.append(line);
							content.append("\n");
						}
						res.setContentType("application/javascript");
						res.getWriter().write(content.toString());
					}
				}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
