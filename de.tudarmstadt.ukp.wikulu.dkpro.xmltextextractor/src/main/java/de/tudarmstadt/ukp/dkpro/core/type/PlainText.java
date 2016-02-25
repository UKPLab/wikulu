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
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Annotates a span of plain text in a structured document (XHTML/XML). Stores the location where it can be found in the original structured document.
 * Updated by JCasGen Wed May 27 17:48:09 CEST 2009
 * XML source: /home/hoffart/workspace/dkpro_core/desc/type/PlainText.xml
 * @generated */
public class PlainText extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(PlainText.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected PlainText() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PlainText(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PlainText(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PlainText(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
  //*--------------*
  //* Feature: OriginalOffset

  /** getter for OriginalOffset - gets Offset of the plain text section in the original document (including mark-up)
   * @generated */
  public int getOriginalOffset() {
    if (PlainText_Type.featOkTst && ((PlainText_Type)jcasType).casFeat_OriginalOffset == null)
      jcasType.jcas.throwFeatMissing("OriginalOffset", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    return jcasType.ll_cas.ll_getIntValue(addr, ((PlainText_Type)jcasType).casFeatCode_OriginalOffset);}
    
  /** setter for OriginalOffset - sets Offset of the plain text section in the original document (including mark-up) 
   * @generated */
  public void setOriginalOffset(int v) {
    if (PlainText_Type.featOkTst && ((PlainText_Type)jcasType).casFeat_OriginalOffset == null)
      jcasType.jcas.throwFeatMissing("OriginalOffset", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    jcasType.ll_cas.ll_setIntValue(addr, ((PlainText_Type)jcasType).casFeatCode_OriginalOffset, v);}    
   
    
  //*--------------*
  //* Feature: OriginalXPath

  /** getter for OriginalXPath - gets XPath in the original XML document
   * @generated */
  public String getOriginalXPath() {
    if (PlainText_Type.featOkTst && ((PlainText_Type)jcasType).casFeat_OriginalXPath == null)
      jcasType.jcas.throwFeatMissing("OriginalXPath", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PlainText_Type)jcasType).casFeatCode_OriginalXPath);}
    
  /** setter for OriginalXPath - sets XPath in the original XML document 
   * @generated */
  public void setOriginalXPath(String v) {
    if (PlainText_Type.featOkTst && ((PlainText_Type)jcasType).casFeat_OriginalXPath == null)
      jcasType.jcas.throwFeatMissing("OriginalXPath", "de.tudarmstadt.ukp.dkpro.core.type.PlainText");
    jcasType.ll_cas.ll_setStringValue(addr, ((PlainText_Type)jcasType).casFeatCode_OriginalXPath, v);}    
  }

    