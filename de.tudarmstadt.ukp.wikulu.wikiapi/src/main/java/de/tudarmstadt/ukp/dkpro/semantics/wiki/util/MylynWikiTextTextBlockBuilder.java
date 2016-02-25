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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedTextBlock;

/**
 * <p>This class is used to extract text block from Wiki text. It uses Mylyn facility, so it supports both MediaWiki and TWiki syntax.</p>
 * <p>Comments cannot be parsed correctly by Mylyn parser and therefore they are included in the text blocked</p>
 * @author Fabian L. Tamin
 */
public class MylynWikiTextTextBlockBuilder extends NoOpDocumentBuilder {
	
	// Code for handle comments are currently set as comments
	// because comments cannot parsed correctly by Mylyn parser.
	
	/**
	 * List of extracted blocks
	 */
	private List<ExtractedTextBlock> blocks = new ArrayList<ExtractedTextBlock>();

	/**
	 * Open comment constant
	 */
	private final String OPEN_COMMENT = "<!--";
	/**
	 * Close comment constant
	 */
	private final String CLOSE_COMMENT = "-->";
	
	/**
	 * Current extracted block.
	 */
	private ExtractedTextBlock block;
	/**
	 * Block counter.<br />
	 * 0 := no block, 1 := one block is detected, n := n-1 nested blocks detected. 
	 */
	private int blockCounter = 0;
	/**
	 * Current status for comment.<br />
	 * <code>true</code> := in comment block. <code>false</code> := in normal block.
	 */
//	private boolean isComment = false;

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		if (this.blockCounter == 0) {
			this.block = new ExtractedTextBlock("", type.name());
			this.block.setBegin(getLocator().getDocumentOffset());	
		}
		
		this.blockCounter++;
	}
	
	@Override
	public void endBlock() {
		if (this.blockCounter == 1) {
			this.block.setEnd(getLocator().getDocumentOffset());
			filterAndAddBlock();
		} else {
//			System.out.println(block.getType()+" end");
			appendPlainTextToBlock("\n");
		}
		
		this.blockCounter--;
	}
	
	@Override
	public void beginHeading(int level, Attributes attributes) {
		this.block = new ExtractedTextBlock("", "HEADING_"+level);
	}

	@Override
	public void endHeading() {
		this.block.setBegin(getLocator().getLineDocumentOffset());
		this.block.setEnd(this.block.getBegin()+getLocator().getLineLength());
		filterAndAddBlock();
	}

	@Override
	public void charactersUnescaped(String literal) {
//		System.out.println(":::charUnsc:"+literal);
		if (literal.equals(OPEN_COMMENT)) {
//			this.isComment = true;
		} else if (literal.equals(CLOSE_COMMENT)) {
//			this.isComment = false;
		}
	}
	
	@Override
	public void characters(String text) {
		// TODO bugs in mylyn: some close comments are not detected 
		if (text.equals(OPEN_COMMENT)) {
//			this.isComment = true;
		} else if (text.equals(CLOSE_COMMENT)) {
//			this.isComment = false;
//		} else if (this.block != null && !this.isComment) {
		} else if (this.block != null) {
			appendPlainTextToBlock(text);
		}
	}

	@Override
	public void lineBreak() {
		if (this.block != null) {
			appendPlainTextToBlock("\n");
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		if (this.block != null) {
			appendPlainTextToBlock(text);
		}
	}
	
	@Override
	public void image(Attributes attributes, String url) {
		if (this.block != null) {
			appendPlainTextToBlock(attributes.getTitle());
		}
	}
	
	/**
	 * Appends text to current block.
	 * @param text text to append
	 */
	private void appendPlainTextToBlock(String text) {
		this.block.setText(this.block.getText()+text);
	}
	
	/**
	 * Filtering text from undesired characters.
	 */
	private void filterAndAddBlock() {
		this.block.setText(this.block.getText());
		this.blocks.add(this.block);
	}
	/**
	 * Gets list of the text blocks.
	 * @return list of the text blocks.
	 */
	public List<ExtractedTextBlock> getBlocks() {
		return blocks;
	}
}
