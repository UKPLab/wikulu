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


/* First created by JCasGen Sat Jul 11 22:22:56 CEST 2009 */
package de.tudarmstadt.ukp.dkpro.semantics.wiki.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;


/** It represents a tag or a wiki category
 * Updated by JCasGen Wed Feb 17 14:48:09 CET 2010
 * XML source: /Users/hoffart/workspace/de.tudarmstadt.ukp.wikulu/de.tudarmstadt.ukp.wikulu.wikiapi/src/main/resources/desc/type/WikiSnifferTypeSystemDescriptor.xml
 * @generated */
public class Tag extends Link {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Tag.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Tag() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Tag(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Tag(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Tag(JCas jcas, int begin, int end) {
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
     
 
    
}

    