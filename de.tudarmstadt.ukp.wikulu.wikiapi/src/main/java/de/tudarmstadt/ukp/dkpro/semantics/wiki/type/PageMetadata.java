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

/* First created by JCasGen Thu Jul 16 16:18:47 CEST 2009 */
package de.tudarmstadt.ukp.dkpro.semantics.wiki.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * It represents a page metadata. Updated by JCasGen Wed Feb 17 14:48:09 CET
 * 2010 XML source:
 * /Users/hoffart/workspace/de.tudarmstadt.ukp.wikulu/de.tudarmstadt
 * .ukp.wikulu.wikiapi
 * /src/main/resources/desc/type/WikiSnifferTypeSystemDescriptor.xml
 * 
 * @generated
 */
public class PageMetadata
	extends DocumentMetaData
{
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry
			.register(PageMetadata.class);
	/**
	 * @generated
	 * @ordered
	 */
	public final static int type = typeIndexID;

	/** @generated */
	public int getTypeIndexID()
	{
		return typeIndexID;
	}

	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected PageMetadata()
	{
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public PageMetadata(int addr, TOP_Type type)
	{
		super(addr, type);
		readObject();
	}

	/** @generated */
	public PageMetadata(JCas jcas)
	{
		super(jcas);
		readObject();
	}

	/** @generated */
	public PageMetadata(JCas jcas, int begin, int end)
	{
		super(jcas);
		setBegin(begin);
		setEnd(end);
		readObject();
	}

	/**
	 * <!-- begin-user-doc --> Write your own initialization here <!--
	 * end-user-doc -->
	 * 
	 * @generated modifiable
	 */
	private void readObject()
	{
	}

	// *--------------*
	// * Feature: wikiSyntax

	/**
	 * getter for wikiSyntax - gets
	 * 
	 * @generated
	 */
	public String getWikiSyntax()
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiSyntax == null)
			jcasType.jcas
					.throwFeatMissing("wikiSyntax",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return jcasType.ll_cas.ll_getStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiSyntax);
	}

	/**
	 * setter for wikiSyntax - sets
	 * 
	 * @generated
	 */
	public void setWikiSyntax(String v)
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiSyntax == null)
			jcasType.jcas
					.throwFeatMissing("wikiSyntax",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		jcasType.ll_cas.ll_setStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiSyntax, v);
	}

	// *--------------*
	// * Feature: wikiApiUrl

	/**
	 * getter for wikiApiUrl - gets Wiki API URL
	 * 
	 * @generated
	 */
	public String getWikiApiUrl()
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiApiUrl == null)
			jcasType.jcas
					.throwFeatMissing("wikiApiUrl",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return jcasType.ll_cas.ll_getStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiApiUrl);
	}

	/**
	 * setter for wikiApiUrl - sets Wiki API URL
	 * 
	 * @generated
	 */
	public void setWikiApiUrl(String v)
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiApiUrl == null)
			jcasType.jcas
					.throwFeatMissing("wikiApiUrl",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		jcasType.ll_cas.ll_setStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiApiUrl, v);
	}

	// *--------------*
	// * Feature: wikiViewUrl

	/**
	 * getter for wikiViewUrl - gets Wiki view url (prepend this to the page id
	 * to get the complete URL)
	 * 
	 * @generated
	 */
	public String getWikiViewUrl()
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiViewUrl == null)
			jcasType.jcas
					.throwFeatMissing("wikiViewUrl",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return jcasType.ll_cas.ll_getStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiViewUrl);
	}

	/**
	 * setter for wikiViewUrl - sets Wiki view url (prepend this to the page id
	 * to get the complete URL)
	 * 
	 * @generated
	 */
	public void setWikiViewUrl(String v)
	{
		if (PageMetadata_Type.featOkTst
				&& ((PageMetadata_Type) jcasType).casFeat_wikiViewUrl == null)
			jcasType.jcas
					.throwFeatMissing("wikiViewUrl",
							"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		jcasType.ll_cas.ll_setStringValue(addr,
				((PageMetadata_Type) jcasType).casFeatCode_wikiViewUrl, v);
	}
}
