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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * This class is a helper class for reading and writing text file.
 * @author Fabian L. Tamin
 *
 */
public class TextFileIO {

	/**
	 * Reads inputs from text file
	 * @param filepath file path source
	 * @param encoding text encoding (e. g.: UTF8)
	 * @return text as <code>String</code>
	 */
	public static String readInput(String filepath, String encoding) {
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(filepath);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			Reader in = new BufferedReader(isr);
			int ch;
			while ((ch = in.read()) > -1) {
				buffer.append((char) ch);
			}
			in.close();
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes <code>String</code> text to a text file. 
	 * @param str text as <code>String</code>
	 * @param filepath file path destination
	 * @param encoding text encoding (e. g.: UTF8)
	 * @throws IOException 
	 */
	public static void writeOutput(String str, String filepath, String encoding) throws IOException {	
		FileOutputStream fos = new FileOutputStream(filepath);
		Writer out = new OutputStreamWriter(fos, encoding);
		out.write(str);
		out.close();
	}
}
