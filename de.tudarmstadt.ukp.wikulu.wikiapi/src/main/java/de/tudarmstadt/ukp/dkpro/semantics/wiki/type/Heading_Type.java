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

/* First created by JCasGen Sat Jul 11 22:08:31 CEST 2009 */
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

/** It represents heading.
 * Updated by JCasGen Wed Feb 17 14:48:09 CET 2010
 * @generated */
public class Heading_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Heading_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Heading_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Heading(addr, Heading_Type.this);
  			   Heading_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Heading(addr, Heading_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Heading.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading");



  /** @generated */
  final Feature casFeat_level;
  /** @generated */
  final int     casFeatCode_level;
  /** @generated */ 
  public int getLevel(int addr) {
        if (featOkTst && casFeat_level == null)
      jcas.throwFeatMissing("level", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading");
    return ll_cas.ll_getIntValue(addr, casFeatCode_level);
  }
  /** @generated */    
  public void setLevel(int addr, int v) {
        if (featOkTst && casFeat_level == null)
      jcas.throwFeatMissing("level", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading");
    ll_cas.ll_setIntValue(addr, casFeatCode_level, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Heading_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_level = jcas.getRequiredFeatureDE(casType, "level", "uima.cas.Integer", featOkTst);
    casFeatCode_level  = (null == casFeat_level) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_level).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

  }
}



    