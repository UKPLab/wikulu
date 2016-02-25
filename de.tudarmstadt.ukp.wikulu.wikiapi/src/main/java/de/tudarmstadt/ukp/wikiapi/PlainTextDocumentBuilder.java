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



import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class PlainTextDocumentBuilder extends DocumentBuilder{

	
	private StringBuffer out;
	
	public PlainTextDocumentBuilder(StringBuffer out){
		this.out = out;
	}
	
	@Override
	public void acronym(String arg0, String arg1) {
		//out.append("___acrinym___");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginBlock(BlockType arg0, Attributes arg1) {
		//out.append("_ver_");
	}

	@Override
	public void beginDocument() {
				
	}

	@Override
	public void beginHeading(int arg0, Attributes arg1) {
		out.append(" ");
	}

	@Override
	public void beginSpan(SpanType arg0, Attributes arg1) {
		
		
	}

	@Override
	public void characters(String arg0) {
		out.append(arg0);
		
	}

	@Override
	public void charactersUnescaped(String arg0) {
		//out.append(arg0);
		// TODO ???
		
	}

	@Override
	public void endBlock() {
		//out.append("_ver_end__");	
	}

	@Override
	public void endDocument() {
				
	}

	@Override
	public void endHeading() {
		out.append(" ");
		//ASK heading ends with dot or without dot
		
	}

	@Override
	public void endSpan() {
		
		
	}

	@Override
	public void entityReference(String arg0) {
		//out.append(arg0);
		
	}

	@Override
	public void image(Attributes arg0, String arg1) {
		
		
	}

	@Override
	public void imageLink(Attributes arg0, Attributes arg1, String arg2,
			String arg3) {
		
		
	}

	@Override
	public void lineBreak() {
		out.append("\n");
		
	}

	@Override
	public void link(Attributes arg0, String arg1, String arg2) {
		out.append(" "+arg2+" ");
		
	}

	
	
}
