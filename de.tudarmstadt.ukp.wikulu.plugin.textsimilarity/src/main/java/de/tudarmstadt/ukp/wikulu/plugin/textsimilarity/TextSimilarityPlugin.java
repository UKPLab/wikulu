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
package de.tudarmstadt.ukp.wikulu.plugin.textsimilarity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerTT4JBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

public class TextSimilarityPlugin extends Plugin {
	private Wiki wiki;
	/** allows easy sorting (see below) **/
	private List<Double> sortList;
	/** SQL cache database **/
	String dataBasePath = null;
	/** SQL Statement object **/
	Statement stm = null;
	/** SQL Connection object **/
	Connection conn = null;

	public TextSimilarityPlugin(Wiki wiki) {
		super(wiki);
		this.wiki = wiki;
	}

	@Override
	public String run(String text) throws AnalysisEngineProcessException,
			ResourceInitializationException, JSONException,
			ResourceConfigurationException, IOException, CASException,
			JDOMException {

		JSONObject args = new JSONObject(text);
		JSONObject results = new JSONObject();
		JSONArray list = new JSONArray();
		this.sortList = new ArrayList<Double>();

		String[] methodSplit = args.getString("method").split(" ");
		String compareMethod = args.getString("method").split(" ")[0];
		String methodParams = "";
		if (methodSplit.length > 1)
			methodParams = args.getString("method").split(" ")[1].replace("(",
					"").replace(")", "");

		// This creates the database(s) in proxy's target folder.
		// JDBC won't open a database in2 this project's target folder.
		String pathPart = this.wiki.getViewUrl().getPath().replace("/", "-");
		// remove unnecessary "-" at the end of the path
		if (pathPart.endsWith("-"))
			pathPart = pathPart.substring(0, pathPart.length() - 1);
		dataBasePath = "target/" + "cache.plugin.textsimilarity."
				+ this.wiki.getViewUrl().getHost() + "" + pathPart + ".sqlite";

		// delete database
		if (compareMethod.equals("DELETE")) {
			File f = new File(dataBasePath);
			if (f.exists()) {
				if (f.delete()) {
					results.put("successMessage", "Database deleted!");
					return results.toString();
				} else {
					results.put("successMessage", "Could not delete database!");
					return results.toString();
				}
			}
		}

		JSONArray methods = new JSONArray();

		ArrayList<String> availableCompareMethods = new ArrayList<String>();
		HashMap<String, ArrayList<String>> avComMeth = new HashMap<String, ArrayList<String>>();

		JSONObject algoTable = new JSONObject(getParameter("alglist"));
		JSONArray algorithms = algoTable.getJSONArray("values");
		for (int x = 0; x < algorithms.length(); x++) {
			JSONArray ja = algorithms.getJSONArray(x);
			if (ja.getString(0).equals("true")) {
				if (compareMethod.equals("NONE")) {
					compareMethod = ja.getString(1);
					JSONArray relMethod = ja.getJSONArray(3);
					for (int i = 0; i < relMethod.length(); i++) {
						String val = relMethod.getString(i);
						if (val.startsWith("!")) {
							methodParams += val.replace("!", "");
						}
					}
					methodParams += ":" + ja.getString(4);
				}

				availableCompareMethods.add(ja.getString(1) + " ("
						+ ja.getString(4) + ")");

				JSONArray relMethod = ja.getJSONArray(3);
				for (int i = 0; i < relMethod.length(); i++) {
					String val = relMethod.getString(i);
					if (val.startsWith("!")) {
						String settingString = val.replace("!", "") + ":"
								+ ja.getString(4);
						if (ja.getString(2).endsWith(compareMethod)
								&& ja.getString(4).equals(methodParams)) {
							methodParams = settingString;
						}
						if (!avComMeth.containsKey(ja.getString(2))) {
							ArrayList<String> possParams = new ArrayList<String>();
							possParams.add(settingString);
							avComMeth.put(ja.getString(2), possParams);
						} else {
							avComMeth.get(ja.getString(2)).add(settingString);
						}
						break;
					}
				}
			}
			if (compareMethod.equals(ja.getString(1)))
				compareMethod = ja.getString(2);
		}

		for (int x = 0; x < availableCompareMethods.size(); x++) {
			methods.put(availableCompareMethods.get(x));
		}
		
		results.put("compare_methods", methods);

		File f = new File(dataBasePath);
		Iterator<Page> iter;
		if (compareMethod.equals("UPDATE")) {
			try {

				iter = this.wiki.getPages().listIterator();
				if (f.exists()) {
					String updateSuccess = this.updateDatabase(avComMeth, iter);
					return updateSuccess;
				} else {
					this.setupDatabase(avComMeth, iter);
					return "Database created and updated!";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SimilarityException e) {
				e.printStackTrace();
			}
		}

		// if cache does not exist, create database and
		// store everything
		if (!f.exists()) {
			try {

				iter = this.wiki.getPages().listIterator();
				this.setupDatabase(avComMeth, iter);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SimilarityException e) {
				e.printStackTrace();
			}
		}

		String articleName = args.getString("url");

		try {
			this.connectToDatabase();

			ResultSet textId = stm
					.executeQuery("select id from pages where name='"
							+ articleName + "'");
			int tId = textId.getInt("id");

			ResultSet result = stm
					.executeQuery("select r.compResult,p.name, m.params from results r, pages p, methods m where "
							+ "p.id=r.idTextOne and r.idTextTwo = "
							+ tId
							+ " and m.id=r.idMethod "
							+ "and m.params=\""
							+ methodParams
							+ "\" "
							+ "and m.name=\""
							+ compareMethod + "\"");

			while (result.next()) {
				JSONObject tmp = new JSONObject();
				tmp.put("params", result.getString("params"));
				tmp.put("url", result.getString("name"));
				Float fl = result.getFloat("compResult");
				// adjust results
				if(fl > 100) {
					while(fl > 100) {
						fl = fl/100;
					}
				} else if(fl < 1) {
					fl = fl*100;
				}

				tmp.put("similarity", fl);
				Double d = fl.doubleValue();
				sortList.add(d);
				list.put(tmp);
			}
			stm.close();
			conn.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		Collections.sort(sortList);
		int index = 0;
		double d;

		// TODO replace this with a more elegant solution
		// ArrayList to store ranks that are already used (since
		// Collections.sort avoids reordering equal elements
		ArrayList<Integer> usedRanks = new ArrayList<Integer>();
		for (int x = 0; x < sortList.size(); x++) {
			d = list.getJSONObject(x).getDouble("similarity");
			index = sortList.indexOf(d);
			while (usedRanks.contains(index)) {
				index++;
			}
			usedRanks.add(index);
			// this avoids sorting in JavaScript
			// rank: rank if sorted by similarity score
			// alph_rank: rank if sorted alphabetically
			list.getJSONObject(x).put("rank", index);
			list.getJSONObject(x).put("alph_rank", x);
		}

		results.put("content", list);
		results.put("url", this.wiki.viewUrl.toExternalForm());
		// System.out.println(results.toString(2));
		return results.toString();
	}

	/**
	 * Delete data from the DB.
	 * 
	 * @param delMeths
	 * @throws SQLException
	 */
	private void deleteUnnecessaryData(ArrayList<Integer> delMeths)
			throws SQLException {
		for (Integer i : delMeths) {
			this.stm.executeUpdate("delete from methods where id=" + i);
			this.stm.executeUpdate("delete from results where idMethod=" + i);
		}
	}

	/**
	 * Establishes connection to the database at filePath.
	 * 
	 * @throws SQLException
	 *             thrown if Statement object failed
	 * @throws ClassNotFoundException
	 *             thrown if the JDBC driver couldn't be found
	 */
	private void connectToDatabase() throws SQLException,
			ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + dataBasePath);
		this.stm = conn.createStatement();
	}

	private String updateDatabase(HashMap<String, ArrayList<String>> avComMeth,
			Iterator<Page> pages) throws SQLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, IOException, // RelatednessException,
			SimilarityException {
		this.connectToDatabase();
		System.out.println("UPDATING");
		ResultSet exisMethods = stm.executeQuery("select * from methods");
		ArrayList<Integer> delMethods = new ArrayList<Integer>();
		HashMap<String, ArrayList<String>> allMethods = new HashMap<String, ArrayList<String>>();
		// copy of avComMeth, in case new Pages are found
		allMethods.putAll(avComMeth);

		while (exisMethods.next()) {
			String meth = exisMethods.getString("name");
			System.out.println("M in DB: " + meth);
			String params = exisMethods.getString("params").replace(" ", "");
//			System.out.println("method: " + meth + " params: " + params);

			if (avComMeth.containsKey(meth)) {

				ArrayList<String> methInfo = avComMeth.get(meth);

				// if database contains parameters that aren't used
				// anymore, delete the method+param combo
//				for (String s : methInfo) {
//					System.out.println("METHINFO: " + s);
//				}
				if (!methInfo.contains(params)) {
					delMethods.add(exisMethods.getInt("id"));
//					System.out.println("DELETION: " + meth + ", " + params
//							+ ", " + exisMethods.getInt("id"));
					methInfo.remove(params);
					if (avComMeth.get(meth).size() == 0)
						avComMeth.remove(meth);
				} else {
					// nothing special to do, method+param combo is
					// still in use (delete from list, since it's not new
//					System.out.println("EXISTING: " + meth + ", " + params
//							+ ", " + exisMethods.getInt("id"));
					methInfo.remove(params);
				}

			} else {
				// delete data
				delMethods.add(exisMethods.getInt("id"));
//				System.out.println("DEL-ID: " + exisMethods.getInt("id"));
			}
		}

//		for (Integer i : delMethods) {
//			System.out.println("DEL ID: " + i);
//		}
		this.deleteUnnecessaryData(delMethods);

//		System.out.println("NEW METHODS: " + avComMeth.size());
		for (String key : avComMeth.keySet()) {
			for (String s : avComMeth.get(key)) {
				System.out.println("NEW! key: " + key + " params: " + s);
			}
		}

//		ResultSet exisPages = stm.executeQuery("select * from pages");

		ArrayList<Page> pagesList = new ArrayList<Page>();
		while (pages.hasNext()) {
			Page p = pages.next();
			if (!p.getArticleUrl().toExternalForm().contains("Travelogue")) {
				pagesList.add(p);
			}
		}

//		ArrayList<Page> newPages = new ArrayList<Page>();
//		newPages.addAll(pagesList);
//		while (exisPages.next()) {
//			String pageUrl = exisPages.getString("name");
//			for (Page p : pagesList) {
//				if (p.getArticleUrl()
//						.toExternalForm()
//						.replaceAll(this.wiki.getViewUrl().toExternalForm(), "")
//						.equals(pageUrl)) {
//					newPages.remove(p);
//				}
//			}
//		}

//		if (avComMeth.size() == 0 && newPages.size() == 0) {
//			return "No need to update database!";
//		}
		
		if(avComMeth.size() == 0) {
			return "No need to update database!";
		}

//		System.out.println("number of new pages: " + newPages.size());

		try {
			if (avComMeth.size() > 0)
				this.insertDataIntoDatabase(avComMeth, pagesList.iterator(), 1);
//			if (newPages.size() > 0)
//				this.newPagesFound(newPages, allMethods);
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}

		return "Database updated!";
	}

	/**
	 * Add data for new pages
	 * 
	 * @param pages
	 *            the new pages
	 * @param allMethods
	 *            all methods that are in the DB
	 * @throws SQLException
	 * @throws MalformedURLException
	 */
	private void newPagesFound(ArrayList<Page> pages,
			HashMap<String, ArrayList<String>> allMethods)
			throws MalformedURLException, SQLException {
		int count = 0;
		ResultSet exisPages = stm.executeQuery("select * from pages");
		while (exisPages.next())
			count++;

		int initCount = count;
		
		// add the new pages to the database
		Iterator<Page> iterat = pages.iterator();
		while (iterat.hasNext()) {
			Page p = iterat.next();
			stm.executeUpdate("insert into pages values ("
					+ count
					+ ", \""
					+ p.getArticleUrl()
							.toExternalForm()
							.replaceAll(
									this.wiki.getViewUrl().toExternalForm(), "")
					+ "\");");
			count++;
		}
	}

	@SuppressWarnings("rawtypes")
	private void insertDataIntoDatabase(
			HashMap<String, ArrayList<String>> avComMeth, Iterator<Page> pages,
			int updating) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException,
			ResourceInitializationException, AnalysisEngineProcessException,
			SimilarityException {
		Set<String> keys = avComMeth.keySet();
		Iterator<String> iterat = keys.iterator();
		int count = 0;
		ResultSet exisMethods = stm.executeQuery("select * from methods");
		while (exisMethods.next())
			count++;
		int initCount = count;
		while (iterat.hasNext()) {
			String key = iterat.next();

			for (String s : avComMeth.get(key)) {
				stm.executeUpdate("insert into methods values (" + count
						+ ", \"" + key + "\", \"" + s + "\");");
				count++;
			}
		}

		ArrayList<Page> pagesList = new ArrayList<Page>();
		while (pages.hasNext()) {
			Page p = pages.next();
			if (!p.getArticleUrl().toExternalForm().contains("Travelogue")) {
				pagesList.add(p);
			}
		}

		if (updating == 0) {
			for (int x = 0; x < pagesList.size(); x++) {
				stm.executeUpdate("insert into pages values ("
						+ x
						+ ", \""
						+ pagesList
								.get(x)
								.getArticleUrl()
								.toExternalForm()
								.replaceAll(
										this.wiki.getViewUrl().toExternalForm(),
										"") + "\");");
			}
		}
		
		int current;
		double similarity;
		String pageContent;

		// iterate over the methods
		Iterator<String> newIterat = keys.iterator();
		count = initCount;
		HashMap<TextSimilarityMeasure, String> allMeasures = new HashMap<TextSimilarityMeasure, String>();

		while (newIterat.hasNext()) {
			String className = newIterat.next();
			for (String consParams : avComMeth.get(className)) {
//				System.out.println("CONS: " + consParams);
				String[] constructorParameters = null;
				String[] splitArr = consParams.split(":");
				if (splitArr.length > 1) {
					constructorParameters = consParams.split(":")[1].split(",");
				} else {
					constructorParameters = new String[0];
				}
//				System.out.println(constructorParameters);
				Class clazz = Class.forName(className);

				Constructor[] constructors = clazz.getConstructors();
//				System.out.println("CONS AMOUNT: " + constructors.length);
				Object[] params2 = new Object[constructorParameters.length];
				int index = 0;
				for (Constructor c : constructors) {
					Class[] parameterClasses = c.getParameterTypes();
//					for(int u=0; u<parameterClasses.length;u++) {
//						System.out.println("PARAM CLASS: " + parameterClasses[u].getSimpleName());
//					}
					if (parameterClasses.length == constructorParameters.length) {
//						System.out.println("length matches! ("
//								+ parameterClasses.length + ")");
						// correct constructor found?
						for (int x = 0; x < constructorParameters.length; x++) {
							String param = constructorParameters[x];
							System.out.println("PAR: " + param);
//							System.out.println(parameterClasses[x]);
//							System.out.println("param no " + x + ": " + param);
							if (!param.matches("[a-zA-Z]")) {
//								System.out.println("no string!");
								if (param.contains("\\.")) {
//									System.out.println("double!");
									// double
									if (parameterClasses[x].getSimpleName()
											.equals("double")) {
										params2[index] = new Double(param);
										index++;
									}
								} else {
									// integer
									if (parameterClasses[x].getSimpleName()
											.equals("int")) {
//										System.out.println("new int!");
										params2[index] = new Integer(param);
										index++;
									}
								}
							} else {
								// assume it's a String
								params2[index] = param;
								index++;
							}
						}
						try {
							TextSimilarityMeasure comp = (TextSimilarityMeasure) c
									.newInstance(params2);
							allMeasures.put(comp, consParams.split(":")[0]);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

//		System.out.println("EVERYTHING: " + allMeasures.keySet().size());

		for (TextSimilarityMeasure comp : allMeasures.keySet()) {
			for (int y = 0; y < pagesList.size(); y++) {
				System.out.println("doing page " + (y + 1) + " of "
						+ pagesList.size() + " with method "
						+ comp.getClass().getSimpleName()
						+ " and relatedness method " + allMeasures.get(comp));
				pageContent = pagesList.get(y).getPlainContentFromWikiSyntax();
				for (current = 0; current < pagesList.size(); current++) {
					String tmpcontent = pagesList.get(current)
							.getPlainContentFromWikiSyntax();
					if (current == y) {
						continue;
					}
					try {
						// get the correct getSimilarity method
						// from the HashMap
						String relMethod = allMeasures.get(comp);
						if (relMethod.equals("Token")) {
							AnalysisEngine tokenizer = AnalysisEngineFactory
									.createPrimitive(BreakIteratorSegmenter.class);

							JCas jcas = tokenizer.newJCas();
							jcas.setDocumentText(tmpcontent);
							jcas.setDocumentLanguage("en");

							tokenizer.process(jcas);
							Collection<Token> lol = JCasUtil.select(jcas,
									Token.class);
							ArrayList<String> coll = new ArrayList<String>();
							for (Token t : lol) {
								coll.add(t.getCoveredText());
							}

							JCas jcas2 = tokenizer.newJCas();
							jcas2.setDocumentText(pageContent);
							jcas2.setDocumentLanguage("en");
							tokenizer.process(jcas2);

							Collection<Token> lol2 = JCasUtil.select(jcas2,
									Token.class);
							ArrayList<String> coll2 = new ArrayList<String>();
							for (Token t : lol2) {
								coll2.add(t.getCoveredText());
							}

							similarity = comp.getSimilarity(coll, coll2);

						} else if (relMethod.equals("Lemma")) {
							AnalysisEngine tokenizer = AnalysisEngineFactory
									.createPrimitive(BreakIteratorSegmenter.class);
							AnalysisEngine treetagger = AnalysisEngineFactory
									.createPrimitive(
											TreeTaggerPosLemmaTT4J.class,
											TreeTaggerTT4JBase.PARAM_LANGUAGE,
											"en");

							JCas jcas = treetagger.newJCas();
							jcas.setDocumentText(tmpcontent);
							jcas.setDocumentLanguage("en");
							tokenizer.process(jcas);
							treetagger.process(jcas);

							Collection<Lemma> lol = JCasUtil.select(jcas,
									Lemma.class);
							ArrayList<String> coll = new ArrayList<String>();
							for (Lemma l : lol) {
								coll.add(l.getCoveredText());
							}

							JCas jcas2 = treetagger.newJCas();
							jcas2.setDocumentText(pageContent);
							jcas2.setDocumentLanguage("en");
							tokenizer.process(jcas2);
							treetagger.process(jcas2);

							Collection<Lemma> lol2 = JCasUtil.select(jcas2,
									Lemma.class);
							ArrayList<String> coll2 = new ArrayList<String>();
							for (Lemma l : lol2) {
								coll2.add(l.getCoveredText());
							}
							similarity = comp.getSimilarity(coll, coll2);
						} else if (relMethod.equals("JCas")) {

							JCasTextSimilarityMeasure comp2 = (JCasTextSimilarityMeasure) comp
									.getClass().newInstance();
							AnalysisEngine tokenizer = AnalysisEngineFactory
									.createPrimitive(BreakIteratorSegmenter.class);
							AnalysisEngine treetagger = AnalysisEngineFactory
									.createPrimitive(
											TreeTaggerPosLemmaTT4J.class,
											TreeTaggerTT4JBase.PARAM_LANGUAGE,
											"en");

							JCas jcas = treetagger.newJCas();
							jcas.setDocumentText(tmpcontent);
							jcas.setDocumentLanguage("en");
							tokenizer.process(jcas);
							treetagger.process(jcas);

							JCas jcas2 = treetagger.newJCas();
							jcas2.setDocumentText(pageContent);
							jcas2.setDocumentLanguage("en");
							tokenizer.process(jcas2);
							treetagger.process(jcas2);

							similarity = comp2.getSimilarity(jcas, jcas2);
						} else {
							// process text as Strings
							similarity = comp.getSimilarity(pageContent,
									tmpcontent);
						}
						// System.out.println("insert into results values ("
						// + count + ", " + y + ", " + current + ", "
						// + new Float(similarity) + ");");
						stm.executeUpdate("insert into results values ("
								+ count + ", " + y + ", " + current + ", "
								+ new Float(similarity) + ");");

					} catch (StackOverflowError e) {
						System.err.println("couldn't calcute value for method "
								+ comp.getClass().getSimpleName());
						stm.executeUpdate("insert into results values ("
								+ count + ", " + y + ", " + current + ", 0.0);");
					} catch (OutOfMemoryError e1) {
						System.err.println("couldn't calcute value for method "
								+ comp.getClass().getSimpleName());
						stm.executeUpdate("insert into results values ("
								+ count + ", " + y + ", " + current + ", 0.0);");
					}
				}
			}

			count++;
		}

		stm.close();
		conn.close();
	}

	/**
	 * Creates an SQL database and stores all results of any comparing
	 * algorithms.
	 * 
	 * @param avComMeth
	 *            a list of available compare methods
	 * @param pages
	 *            an iterator for all wiki pages
	 * @throws ClassNotFoundException
	 *             thrown if the JDBC driver can't be found
	 * @throws SQLException
	 *             thrown if some SQL stuff goes wrong
	 * @throws IOException
	 * @throws AnalysisEngineProcessException
	 * @throws ResourceInitializationException
	 * @throws SimilarityException
	 */
	private void setupDatabase(HashMap<String, ArrayList<String>> avComMeth,
			Iterator<Page> pages)
			throws ClassNotFoundException,
			SQLException,// , RelatednessException,
			IOException, InstantiationException, IllegalAccessException,
			AnalysisEngineProcessException, ResourceInitializationException,
			SimilarityException {
		this.connectToDatabase();

		// queries
		String queryCreateMethodTable = "create table methods (id integer, name string, params string)";
		String queryCreateTextTable = "create table pages (id integer, name string)";
		String queryCreateMainTable = "create table results (idMethod integer, idTextOne integer, idTextTwo integer, compResult float)";

		// create tables
		stm.executeUpdate(queryCreateMethodTable);
		stm.executeUpdate(queryCreateTextTable);
		stm.executeUpdate(queryCreateMainTable);

		this.insertDataIntoDatabase(avComMeth, pages, 0);
	}

}
