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

import org.apache.commons.codec.language.Soundex;

/**
 * For English words.
 * Under most conditions performance of {@link MetaphoneComparator} or {@link DoubleMetaphoneComparator} should be better.
 *  
 * @author zesch
 *
 */
public class SoundexComparatorFixed
    extends SoundComparatorBaseFixed
{

    public SoundexComparatorFixed()
    {
        encoder = new Soundex();
    }
}