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
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** It represents a block of HTML code.
 * Updated by JCasGen Tue Dec 15 07:47:06 CET 2009
 * XML source: ./desc/type/HTMLTextBlockTypeSystemDescriptor.xml
 * @generated */
public class HTMLTextBlock extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(HTMLTextBlock.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected HTMLTextBlock() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HTMLTextBlock(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HTMLTextBlock(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HTMLTextBlock(JCas jcas, int begin, int end) {
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
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (HTMLTextBlock_Type.featOkTst && ((HTMLTextBlock_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    return jcasType.ll_cas.ll_getStringValue(addr, ((HTMLTextBlock_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (HTMLTextBlock_Type.featOkTst && ((HTMLTextBlock_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    jcasType.ll_cas.ll_setStringValue(addr, ((HTMLTextBlock_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: blockType

  /** getter for blockType - gets 
   * @generated */
  public String getBlockType() {
    if (HTMLTextBlock_Type.featOkTst && ((HTMLTextBlock_Type)jcasType).casFeat_blockType == null)
      jcasType.jcas.throwFeatMissing("blockType", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    return jcasType.ll_cas.ll_getStringValue(addr, ((HTMLTextBlock_Type)jcasType).casFeatCode_blockType);}
    
  /** setter for blockType - sets  
   * @generated */
  public void setBlockType(String v) {
    if (HTMLTextBlock_Type.featOkTst && ((HTMLTextBlock_Type)jcasType).casFeat_blockType == null)
      jcasType.jcas.throwFeatMissing("blockType", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock");
    jcasType.ll_cas.ll_setStringValue(addr, ((HTMLTextBlock_Type)jcasType).casFeatCode_blockType, v);}    
  }

    