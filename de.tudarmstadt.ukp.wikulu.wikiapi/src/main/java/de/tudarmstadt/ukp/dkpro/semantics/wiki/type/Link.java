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


/* First created by JCasGen Wed Jul 15 22:43:06 CEST 2009 */
package de.tudarmstadt.ukp.dkpro.semantics.wiki.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** It represents a link.
 * Updated by JCasGen Wed Feb 17 14:48:09 CET 2010
 * XML source: /Users/hoffart/workspace/de.tudarmstadt.ukp.wikulu/de.tudarmstadt.ukp.wikulu.wikiapi/src/main/resources/desc/type/WikiSnifferTypeSystemDescriptor.xml
 * @generated */
public class Link extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Link.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Link() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Link(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Link(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Link(JCas jcas, int begin, int end) {
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
  //* Feature: href

  /** getter for href - gets 
   * @generated */
  public String getHref() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_href == null)
      jcasType.jcas.throwFeatMissing("href", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_href);}
    
  /** setter for href - sets  
   * @generated */
  public void setHref(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_href == null)
      jcasType.jcas.throwFeatMissing("href", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_href, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_text, v);}    
  }

    