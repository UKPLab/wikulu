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
package de.tudarmstadt.ukp.similarity.algorithms.sound;

import java.util.Collection;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.sound.util.SoundUtils;

/**
 * Base class wrapper for sound based comparators implemented in commons-codec.
 * 
 * The comparators encode the strings in a phonetic form depending on the rules built into the different algorithms.
 * The encoded forms are then compared.
 * 
 * @author zesch
 *
 */
public abstract class SoundComparatorBaseFixed
    extends TextSimilarityMeasureBase
{

    protected StringEncoder encoder;
    
    
    @Override
    public double getSimilarity(String string1, String string2)
        throws SimilarityException
    {
        
        String encodedString1 = null;
        String encodedString2 = null; 
        try {
            encodedString1 = encoder.encode(string1);
            encodedString2 = encoder.encode(string2); 
        }
        catch (EncoderException e) {
            throw new SimilarityException();
        } 
        
        int value = SoundUtils.differenceEncoded(
            encodedString1,
            encodedString2
        );
        
        int minLength = Math.min(encodedString1.length(), encodedString2.length());
        
        return (double) value / minLength;
    }
    
	@Override
	public double getSimilarity(Collection<String> strings1,
			Collection<String> strings2)
		throws SimilarityException
	{
		String text1 = StringUtils.join(strings1, " ");
		String text2 = StringUtils.join(strings2, " ");
		
		return getSimilarity(text1, text2);
	}
}
