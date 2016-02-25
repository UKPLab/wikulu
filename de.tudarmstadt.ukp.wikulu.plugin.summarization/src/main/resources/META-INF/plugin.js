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
{
"name" : "Summarize",
"author" : "Daniel B�r",
"version" : "0.7",
"java_class" : "de.tudarmstadt.ukp.wikulu.plugin.summarization.SummarizationPlugin",
"run_method" : "summarize",
"menu" : "true",
"priority" : "2",
"editable" : "true",
"id" : "summarization",
"params" :
	[
        {
            "name": "numberOfSentences",
            "showname": "Number of sentences",
            "type": "int",
            "description": "Number of sentences to display",
            "value": "3" 
        }
     ]
}