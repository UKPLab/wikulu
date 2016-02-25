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

/* First created by JCasGen Wed Feb 04 11:23:31 CET 2009 */
package de.tudarmstadt.ukp.dkpro.core.type;

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

/** Annotates a span of plain text in a structured document (XHTML/XML). Stores the location where it can be found in the original structured document.
 * Updated by JCasGen Wed May 27 17:48:09 CEST 2009
 * @generated */
public class PlainText_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PlainText_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PlainText_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PlainText(addr, PlainText_Type.this);
  		     PlainText_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PlainText(addr, PlainText_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = PlainText.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.type.PlainText");



  /** @generated */
  final Feature casFeat_OriginalOffset;
  /** @generated */
  final int     casFeatCode_OriginalOffset;
  /** @generated */ 
  public int getOriginalOffset(int addr) {
        if (featOkTst && casFeat_OriginalOffset == null)
      jcas.throwFeatMissing("OriginalOffset", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    return ll_cas.ll_getIntValue(addr, casFeatCode_OriginalOffset);
  }
  /** @generated */    
  public void setOriginalOffset(int addr, int v) {
        if (featOkTst && casFeat_OriginalOffset == null)
      jcas.throwFeatMissing("OriginalOffset", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    ll_cas.ll_setIntValue(addr, casFeatCode_OriginalOffset, v);}
    
  
 
  /** @generated */
  final Feature casFeat_OriginalXPath;
  /** @generated */
  final int     casFeatCode_OriginalXPath;
  /** @generated */ 
  public String getOriginalXPath(int addr) {
        if (featOkTst && casFeat_OriginalXPath == null)
      jcas.throwFeatMissing("OriginalXPath", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    return ll_cas.ll_getStringValue(addr, casFeatCode_OriginalXPath);
  }
  /** @generated */    
  public void setOriginalXPath(int addr, String v) {
        if (featOkTst && casFeat_OriginalXPath == null)
      jcas.throwFeatMissing("OriginalXPath", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    ll_cas.ll_setStringValue(addr, casFeatCode_OriginalXPath, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public PlainText_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_OriginalOffset = jcas.getRequiredFeatureDE(casType, "OriginalOffset", "uima.cas.Integer", featOkTst);
    casFeatCode_OriginalOffset  = (null == casFeat_OriginalOffset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_OriginalOffset).getCode();

 
    casFeat_OriginalXPath = jcas.getRequiredFeatureDE(casType, "OriginalXPath", "uima.cas.String", featOkTst);
    casFeatCode_OriginalXPath  = (null == casFeat_OriginalXPath) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_OriginalXPath).getCode();

  }
}



    