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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is used to discern URLs which should be injected with JavaScripts from ones that should not.
 * 
 * @author hoffart
 *
 */
public class InjectionFilter implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try{
			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpRequest = (HttpServletRequest)request;
				
				if (!httpRequest.getRequestURI().endsWith(".jpg") &&
					!httpRequest.getRequestURI().endsWith(".ico") &&
					!httpRequest.getRequestURI().endsWith(".gif") &&
					!httpRequest.getRequestURI().endsWith(".png") &&
					!httpRequest.getRequestURI().endsWith(".css")) {
						chain.doFilter(request, new InjectionServletResponse((HttpServletResponse)response, (HttpServletRequest)request));
				} else {
					chain.doFilter(request, response);
				}
			} else {
				chain.doFilter(request, response);
			}
		} catch(FileNotFoundException e){
			System.err.println("File not found "+e);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}

}
