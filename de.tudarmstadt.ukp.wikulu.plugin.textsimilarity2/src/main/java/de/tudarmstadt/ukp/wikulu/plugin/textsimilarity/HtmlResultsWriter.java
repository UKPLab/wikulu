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
package de.tudarmstadt.ukp.wikulu.plugin.textsimilarity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.ExperimentalTextSimilarityScore;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.TextSimilarityScore;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;

/**
 * Writer which outputs text similarity scores along with their goldstandard
 * scores (optional).
 */
public class HtmlResultsWriter
	extends JCasConsumer_ImplBase
{
    public static final String LF = System.getProperty("line.separator");
    
	public static final String PARAM_OUTPUT_FILE = "OutputFile";
	@ConfigurationParameter(name=PARAM_OUTPUT_FILE, mandatory=true)
	private File outputFile;
	
	private DecimalFormat format;
	
	private BufferedWriter writer;

	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException
	{
		super.initialize(context);
		
		try {
			// Make sure all intermediate dirs are there
			outputFile.getParentFile().mkdirs();
			
			format = new DecimalFormat("#.###");
			
			writer = new BufferedWriter(new FileWriter(outputFile));
			writer.append("<table class='table table-hover' id='results'>");
			
			writer.append("<thead>");
			writer.append("<tr>"); 
			writer.append("<th nowrap>id 1</th>"); 
			writer.append("<th nowrap>id 2</th>");
			writer.append("<th width='50%'>text 1</th>");
			writer.append("<th width='50%'>text 2</th>");
			writer.append("<th>score</th>");
			writer.append("</tr>");
			writer.append("</thead>");
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	public void process(JCas jcas)
		throws AnalysisEngineProcessException
	{
		JCas view1;
		JCas view2;
		try {
			view1 = jcas.getView(CombinationReader.VIEW_1);
			view2 = jcas.getView(CombinationReader.VIEW_2);
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		DocumentMetaData md1 = JCasUtil.selectSingle(view1, DocumentMetaData.class);
		DocumentMetaData md2 = JCasUtil.selectSingle(view2, DocumentMetaData.class);
		
		TextSimilarityScore score = JCasUtil.selectSingle(jcas, ExperimentalTextSimilarityScore.class);
		
		try {
			writer.write("<tr>" + LF);
			writer.write("<td>" + md1.getDocumentId() + "</td>" + LF);
			writer.write("<td>" + md2.getDocumentId() + "</td>" + LF);
			writer.write("<td>" + view1.getDocumentText() + "</td>" + LF);
			writer.write("<td>" + view2.getDocumentText() + "</td>" + LF);
			writer.write("<td nowrap>" + format.format(score.getScore()).toString().replaceAll(",",".") + "</td>" + LF);
			writer.write("</tr>" + LF);
		}
		catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	@Override
	public void collectionProcessComplete()
		throws AnalysisEngineProcessException
	{
		try {
			writer.append("</table>");
			writer.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		super.collectionProcessComplete();
	}
}