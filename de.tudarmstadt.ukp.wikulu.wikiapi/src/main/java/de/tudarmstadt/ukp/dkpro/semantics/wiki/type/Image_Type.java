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

/* First created by JCasGen Fri Oct 30 05:49:17 CET 2009 */
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

/** It represents an image.
 * Updated by JCasGen Wed Feb 17 14:48:09 CET 2010
 * @generated */
public class Image_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Image_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Image_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Image(addr, Image_Type.this);
  			   Image_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Image(addr, Image_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Image.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image");
 
  /** @generated */
  final Feature casFeat_description;
  /** @generated */
  final int     casFeatCode_description;
  /** @generated */ 
  public String getDescription(int addr) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image");
    return ll_cas.ll_getStringValue(addr, casFeatCode_description);
  }
  /** @generated */    
  public void setDescription(int addr, String v) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image");
    ll_cas.ll_setStringValue(addr, casFeatCode_description, v);}
    
  
 
  /** @generated */
  final Feature casFeat_href;
  /** @generated */
  final int     casFeatCode_href;
  /** @generated */ 
  public String getHref(int addr) {
        if (featOkTst && casFeat_href == null)
      jcas.throwFeatMissing("href", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image");
    return ll_cas.ll_getStringValue(addr, casFeatCode_href);
  }
  /** @generated */    
  public void setHref(int addr, String v) {
        if (featOkTst && casFeat_href == null)
      jcas.throwFeatMissing("href", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image");
    ll_cas.ll_setStringValue(addr, casFeatCode_href, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Image_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_description = jcas.getRequiredFeatureDE(casType, "description", "uima.cas.String", featOkTst);
    casFeatCode_description  = (null == casFeat_description) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_description).getCode();

 
    casFeat_href = jcas.getRequiredFeatureDE(casType, "href", "uima.cas.String", featOkTst);
    casFeatCode_href  = (null == casFeat_href) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_href).getCode();

  }
}



    