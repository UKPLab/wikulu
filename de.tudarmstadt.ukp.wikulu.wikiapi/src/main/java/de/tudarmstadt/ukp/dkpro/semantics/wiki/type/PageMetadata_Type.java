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
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData_Type;

/**
 * It represents a page metadata. Updated by JCasGen Wed Feb 17 14:48:09 CET
 * 2010
 * 
 * @generated
 */
public class PageMetadata_Type
	extends DocumentMetaData_Type
{
	/** @generated */
	protected FSGenerator getFSGenerator()
	{
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator fsGenerator = new FSGenerator()
	{
		public FeatureStructure createFS(int addr, CASImpl cas)
		{
			if (PageMetadata_Type.this.useExistingInstance) {
				// Return eq fs instance if already created
				FeatureStructure fs = PageMetadata_Type.this.jcas
						.getJfsFromCaddr(addr);
				if (null == fs) {
					fs = new PageMetadata(addr, PageMetadata_Type.this);
					PageMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
					return fs;
				}
				return fs;
			}
			else
				return new PageMetadata(addr, PageMetadata_Type.this);
		}
	};
	/** @generated */
	public final static int typeIndexID = PageMetadata.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry
			.getFeatOkTst("de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");

	/** @generated */
	final Feature casFeat_wikiSyntax;
	/** @generated */
	final int casFeatCode_wikiSyntax;

	/** @generated */
	public String getWikiSyntax(int addr)
	{
		if (featOkTst && casFeat_wikiSyntax == null)
			jcas.throwFeatMissing("wikiSyntax",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return ll_cas.ll_getStringValue(addr, casFeatCode_wikiSyntax);
	}

	/** @generated */
	public void setWikiSyntax(int addr, String v)
	{
		if (featOkTst && casFeat_wikiSyntax == null)
			jcas.throwFeatMissing("wikiSyntax",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		ll_cas.ll_setStringValue(addr, casFeatCode_wikiSyntax, v);
	}

	/** @generated */
	final Feature casFeat_wikiApiUrl;
	/** @generated */
	final int casFeatCode_wikiApiUrl;

	/** @generated */
	public String getWikiApiUrl(int addr)
	{
		if (featOkTst && casFeat_wikiApiUrl == null)
			jcas.throwFeatMissing("wikiApiUrl",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return ll_cas.ll_getStringValue(addr, casFeatCode_wikiApiUrl);
	}

	/** @generated */
	public void setWikiApiUrl(int addr, String v)
	{
		if (featOkTst && casFeat_wikiApiUrl == null)
			jcas.throwFeatMissing("wikiApiUrl",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		ll_cas.ll_setStringValue(addr, casFeatCode_wikiApiUrl, v);
	}

	/** @generated */
	final Feature casFeat_wikiViewUrl;
	/** @generated */
	final int casFeatCode_wikiViewUrl;

	/** @generated */
	public String getWikiViewUrl(int addr)
	{
		if (featOkTst && casFeat_wikiViewUrl == null)
			jcas.throwFeatMissing("wikiViewUrl",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		return ll_cas.ll_getStringValue(addr, casFeatCode_wikiViewUrl);
	}

	/** @generated */
	public void setWikiViewUrl(int addr, String v)
	{
		if (featOkTst && casFeat_wikiViewUrl == null)
			jcas.throwFeatMissing("wikiViewUrl",
					"de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata");
		ll_cas.ll_setStringValue(addr, casFeatCode_wikiViewUrl, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public PageMetadata_Type(JCas jcas, Type casType)
	{
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType(
				(TypeImpl) this.casType, getFSGenerator());

		casFeat_wikiSyntax = jcas.getRequiredFeatureDE(casType, "wikiSyntax",
				"uima.cas.String", featOkTst);
		casFeatCode_wikiSyntax = (null == casFeat_wikiSyntax) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_wikiSyntax).getCode();

		casFeat_wikiApiUrl = jcas.getRequiredFeatureDE(casType, "wikiApiUrl",
				"uima.cas.String", featOkTst);
		casFeatCode_wikiApiUrl = (null == casFeat_wikiApiUrl) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_wikiApiUrl).getCode();

		casFeat_wikiViewUrl = jcas.getRequiredFeatureDE(casType, "wikiViewUrl",
				"uima.cas.String", featOkTst);
		casFeatCode_wikiViewUrl = (null == casFeat_wikiViewUrl) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_wikiViewUrl).getCode();

	}
}
