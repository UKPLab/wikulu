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
	"name" : "POS Tagger",
	"author" : "C. Deeg",
	"version" : "0.1",
	"java_class" : "de.tudarmstadt.ukp.wikulu.plugin.highlightannotations.ExtractAnnotations",
	"run_method" : "activateAnnotatorMenu",
	"priority" : "3",
	"editable" : "true",
	"menu" : "true",
	"id" : "highlight_annotations",
	"params" : [
		{
			"name": "typePrefix",
			"showname": "Prefix",
			"type": "string",
			"description": "Prefix for type class",
			"value": "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos."
		},
		{
			"name": "CONJ",
			"showname" : "CONJ",
			"type": "string",
			"description": "Highlighting color for CONJ",
			"value": "#00ff60"
		},
		{
			"name": "V",
			"showname" : "V",
			"type": "string",
			"description": "Highlighting color for V",
			"value": "#00c0ff"
		},
		{
			"name": "PP",
			"showname" : "PP",
			"type": "string",
			"description": "Highlighting color for PP",
			"value": "#a0ff00"
		},
		{
			"name": "ART",
			"showname" : "ART",
			"type": "string",
			"description": "Highlighting color for ART",
			"value": "#c0ffff"
		},
		{
			"name": "PR",
			"showname" : "PR",
			"type": "string",
			"description": "Highlighting color for PR",
			"value": "#ffc020"
		},
		{
			"name": "PUNC",
			"showname" : "PUNC",
			"type": "string",
			"description": "Highlighting color for PUNC",
			"value": "#ff2060"
		},
		{
			"name": "ADJ",
			"showname" : "ADJ",
			"type": "string",
			"description": "Highlighting color for ADJ",
			"value": "#20a0ff"
		},
		{
			"name": "O",
			"showname" : "O",
			"type": "string",
			"description": "Highlighting color for O",
			"value": "#60c080"
		},
		{
			"name": "ADV",
			"showname" : "ADV",
			"type": "string",
			"description": "Highlighting color for ADV",
			"value": "#6080c0"
		},
		{
			"name": "CARD",
			"showname" : "CARD",
			"type": "string",
			"description": "Highlighting color for CARD",
			"value": "#a0a020"
		},
		{
			"name": "NP",
			"showname" : "NP",
			"type": "string",
			"description": "Highlighting color for NP",
			"value": "#c080a0"
		},
		{
			"name": "NN",
			"showname" : "NN",
			"type": "string",
			"description": "Highlighting color for NN",
			"value": "#c0c060"
		}
	]
}