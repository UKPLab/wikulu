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

/* First created by JCasGen Mon Sep 14 17:28:42 CEST 2009 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** It represents a block of HTML code.
 * Updated by JCasGen Tue Dec 15 07:47:06 CET 2009
 * @generated */
public class HTMLTextBlock_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HTMLTextBlock_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HTMLTextBlock_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HTMLTextBlock(addr, HTMLTextBlock_Type.this);
  			   HTMLTextBlock_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HTMLTextBlock(addr, HTMLTextBlock_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = HTMLTextBlock.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_blockType;
  /** @generated */
  final int     casFeatCode_blockType;
  /** @generated */ 
  public String getBlockType(int addr) {
        if (featOkTst && casFeat_blockType == null)
      jcas.throwFeatMissing("blockType", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    return ll_cas.ll_getStringValue(addr, casFeatCode_blockType);
  }
  /** @generated */    
  public void setBlockType(int addr, String v) {
        if (featOkTst && casFeat_blockType == null)
      jcas.throwFeatMissing("blockType", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    ll_cas.ll_setStringValue(addr, casFeatCode_blockType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HTMLTextBlock_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_blockType = jcas.getRequiredFeatureDE(casType, "blockType", "uima.cas.String", featOkTst);
    casFeatCode_blockType  = (null == casFeat_blockType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_blockType).getCode();

  }
}



    