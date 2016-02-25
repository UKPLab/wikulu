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
	"name":"Text Similarity",
	"author":"C.Deeg",
	"version":"0.1",
	"java_class":"de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin",
	"run_method":"showSimilarityValues",
	"menu":"true",
	"id":"textsimilarity",
	"editable":"true",
	"priority":4,
	"params":
		[
			{
				"name":"alglist",
				"showname":"Algorithms",
				"type":"list",
				"description":"A list of all algorithms in use",
				"value":{'definition' : {'Used' : 'bool', 'Name' : 'string', 'Path' : 'string', 'Relatedness' : 'dropdown', 'Parameters' : 'string'}, 'values' : [['false', 'CosineSimilarity', 'de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity', ['!String', 'Token', 'Lemma', 'JCas'], 'None'], ['false', 'TokenPairOrderingMeasure', 'de.tudarmstadt.ukp.similarity.algorithms.structure.TokenPairOrderingMeasure', ['!String', 'Token', 'Lemma', 'JCas'], 'None'], ['true', 'WordNGramContainmentMeasure', 'de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure', ['String', '!Token', 'Lemma', 'JCas'], '']]}
			},
			{
				"name":"delButton",
				"showname":"Delete Database",
				"type":"button",
				"description":"Delete the existing database",
				"value":"deleteTextSimDatabase('de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin')"
			},
			{
				"name":"updButton",
				"showname":"Update Database",
				"type":"button",
				"description":"Update the database",
				"value":"updateTextSimDatabase('de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin')"
			}
		]
}