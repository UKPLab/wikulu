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
"name" : "QADemo",
"author" : "Zhemin Zhu",
"version" : "0.1",
"java_class" : "de.tudarmstadt.ukp.wikulu.plugin.qademo.QADemoPlugin",
"run_method" : "qademo",
"menu" : "true",
"priority" : "2",
"editable" : "true",
"id" : "qademo",
"params" : [ {
         "name": "server",
         "showname": "qael db server",
         "type": "string",
         "description": "Database server name",
         "value": "bender.ukp.informatik.tu-darmstadt.de" 
     	},
     	{
     	 "name": "user",
         "showname": "qael db user",
         "type": "string",
         "description": "Database user name",
         "value": "student"
         }, 
        {
         "name": "password",
         "showname": "qael db pw",
         "type": "string",
         "description": "Database server pw",
         "value": "student"
         },
       {
             "name": "indexfiledb",
             "showname": "index file db",
             "type": "string",
             "description": "qa demo indexfile db",
             "value": "qa_demo"
        },
        {
            "name": "wortschatzdb",
            "showname": "wortschatz db",
            "type": "string",
            "description": "wortschatz db",
            "value": "en1M"
       },
       {
           "name": "wordnetfredb",
           "showname": "wordnet frequency db",
           "type": "string",
           "description": "wordnet frequency db",
           "value": "wordnetfre"
      }
	]
}