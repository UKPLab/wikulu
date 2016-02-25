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
"name" : "Add Content...",
"author" : "",
"version" : "",
"java_class" : "AddContentPlugin",
"run_method" : "showExtendedSearchArea",
"menu" : "true",
"id" : "add_content",
"params" : [
            {
            	"name": "lucene.index.path",
            	"showname": "Path to lucene index",
            	"type": "string",
            	"description": "Path to lucene index",
            	"value": "/Users/carolin/Wikulu/Resources/twiki_mrburns_demo"
            },
            {
            	"name": "search.count",
            	"showname": "Wikulu Search Count",
            	"type": "int",
            	"description": "Wikulu Search Count",
            	"value": "10"
            	
            	
            }
           ]
          
	
}