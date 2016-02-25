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
	"name" : "Wikify!",
	"author" : "Artem Vovk",
	"version" : "0",
	"java_class" : "de.tudarmstadt.ukp.wikulu.plugin.wikify.WikifyPlugin",
	"run_method" : "wikifyThis",
	"menu" : "true",
	"priority" : "2",
	"editable" : "true",
	"id" : "wikify",
	"params": [
	           {
	               "name": "keyphraseMaxCount",
	               "showname": "Keyphrase Max Count",
	               "type": "int",
	               "description": "Maximal count of keyphrases to show",
	               "value": "12" 
	           }
	       ]
}