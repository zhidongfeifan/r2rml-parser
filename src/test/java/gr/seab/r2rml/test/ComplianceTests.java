/**
 * Licensed under the Creative Commons Attribution-NonCommercial 3.0 Unported 
 * License (the "License"). You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * 
 *  http://creativecommons.org/licenses/by-nc/3.0/
 *  
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package gr.seab.r2rml.test;

import gr.seab.r2rml.beans.Generator;
import gr.seab.r2rml.beans.Parser;
import gr.seab.r2rml.beans.Util;
import gr.seab.r2rml.entities.MappingDocument;
import gr.seab.r2rml.entities.sparql.LocalResultSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

@ContextConfiguration(locations = { "classpath:test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
public class ComplianceTests {

	private static final Logger log = LoggerFactory.getLogger(ComplianceTests.class);

	private Connection connection;

	@Test
	public void testAll() {
		log.info("test all");
		LinkedHashMap<String, String[]> tests = new LinkedHashMap<String, String[]>();
		tests.put("D000-1table1column0rows", new String[]{"r2rml.ttl"});
		tests.put("D001-1table1column1row", new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D002-1table2columns1row", new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl", "r2rmle.ttl", "r2rmlf.ttl", "r2rmlg.ttl", "r2rmlh.ttl", "r2rmli.ttl", "r2rmlj.ttl"});
		tests.put("D003-1table3columns1row",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl"});
		tests.put("D004-1table2columns1row", new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D005-1table3columns3rows2duplicates",  new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D006-1table1primarykey1column1row",  new String[]{"r2rmla.ttl"});
		tests.put("D007-1table1primarykey2columns1row",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl", "r2rmle.ttl", "r2rmlf.ttl", "r2rmlg.ttl", "r2rmlh.ttl"});
		tests.put("D008-1table1compositeprimarykey3columns1row", new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl"});
		tests.put("D009-2tables1primarykey1foreignkey",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl"});
		tests.put("D010-1table1primarykey3colums3rows",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl"});
		tests.put("D011-M2MRelations",  new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D012-2tables2duplicates0nulls", new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl", "r2rmle.ttl"});
		tests.put("D013-1table1primarykey3columns2rows1nullvalue",  new String[]{"r2rmla.ttl"});
		tests.put("D014-3tables1primarykey1foreignkey",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl"});
		tests.put("D015-1table3columns1composityeprimarykey3rows2languages", new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D016-1table1primarykey10columns3rowsSQLdatatypes",  new String[]{"r2rmla.ttl", "r2rmlb.ttl", "r2rmlc.ttl", "r2rmld.ttl", "r2rmle.ttl"});
		tests.put("D017-I18NnoSpecialChars",  new String[]{});
		tests.put("D018-1table1primarykey2columns3rows", new String[]{"r2rmla.ttl"});
		tests.put("D019-1table1primarykey3columns3rows",  new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D020-1table1column5rows",  new String[]{"r2rmla.ttl", "r2rmlb.ttl"});
		tests.put("D021-2tables2primarykeys1foreignkeyReferencesAllNulls", new String[]{});
		tests.put("D022-2tables1primarykey1foreignkeyReferencesNoPrimaryKey",  new String[]{});
		tests.put("D023-2tables2primarykeys2foreignkeysReferencesToNon-primarykeys", new String[]{});
		tests.put("D024-2tables2primarykeys1foreignkeyToARowWithSomeNulls",  new String[]{});
		tests.put("D025-3tables3primarykeys3foreignkeys", new String[]{});

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-context.xml");
		
		int counter = 0;
		for (String key : tests.keySet()) {
			if (counter == 19) { //(counter > 4 && counter < 6) {
				String folder = "src/test/resources/postgres/" + key + "/";
				initialiseSourceDatabase(folder + "create.sql");
				
				for (String mappingFile : tests.get(key)) {
					//Override property file
					Parser parser = (Parser) context.getBean("parser");
					Properties p = parser.getProperties();
						mappingFile = folder + mappingFile;
						if (new File(mappingFile).exists()) {
							p.setProperty("mapping.file", mappingFile);
						} else {
							log.error("File " + mappingFile + " does not exist.");
						}
						p.setProperty("jena.destinationFileName", mappingFile.substring(0, mappingFile.indexOf(".") + 1) + "nq");
					parser.setProperties(p);
					MappingDocument mappingDocument = parser.parse();
			
					Generator generator = (Generator) context.getBean("generator");
					generator.setProperties(parser.getProperties());
					generator.setResultModel(parser.getResultModel());
					log.info("--- generating " + p.getProperty("jena.destinationFileName") + " from " + mappingFile + " ---");
					generator.createTriples(mappingDocument);
				}
			}
			counter++;
		}
		context.close();
	}
		
	@Test
	public void testSingle() {
		log.info("test single. Careful, database 'test' will be erased and re-created!");
		String folder = "src/test/resources/postgres/D002-1table2columns1row/";
		initialiseSourceDatabase(folder + "create.sql");
		
		//Override property file
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-context.xml");
		Parser parser = (Parser) context.getBean("parser");
		Properties p = parser.getProperties();
			p.setProperty("mapping.file", folder + "r2rmla.ttl");
			p.setProperty("jena.destinationFileName", folder + "r2rmla.nq");
		parser.setProperties(p);
		MappingDocument mappingDocument = parser.parse();

		Generator generator = (Generator) context.getBean("generator");
		generator.setProperties(parser.getProperties());
		generator.setResultModel(parser.getResultModel());
		generator.createTriples(mappingDocument);
		
		context.close();
	}
	
	@Test
	public void testSparqlQuery() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-context.xml");
		Util util = (Util) context.getBean("util");
		
		Model model = ModelFactory.createDefaultModel();
		String modelFilename = "dump1-epersons.rdf";
		InputStream isMap = FileManager.get().open(modelFilename);
		try {
			model.read(isMap, null, "N3");
		} catch (Exception e) {
			log.error("Error reading model.");
			System.exit(0);
		}
		String query = "SELECT ?x ?z WHERE {?x dc:source ?z} ";
		LocalResultSet rs = util.sparql(model, query);
		log.info("found " + String.valueOf(rs.getRows().size()));
		
		context.close();
	}
	
	@Test
	public void createModelFromReified() {
		Model model = ModelFactory.createDefaultModel();
		String modelFilename = "example.rdf";
		InputStream isMap = FileManager.get().open(modelFilename);
		try {
			model.read(isMap, null, "N3");
		} catch (Exception e) {
			log.error("Error reading model.");
			System.exit(0);
		}
		
		Set<Statement> stmtToAdd = new HashSet<Statement>();
		Model newModel = ModelFactory.createDefaultModel();
		RSIterator rsIter = model.listReifiedStatements();
		while (rsIter.hasNext()) {
			ReifiedStatement rstmt = rsIter.next();
			stmtToAdd.add(rstmt.getStatement());
		}
		rsIter.close();
		newModel.add(stmtToAdd.toArray(new Statement[stmtToAdd.size()]));
		
		log.info("newModel has " + newModel.listStatements().toList().size() + " statements");
	}
	
	/**
	 * Drops and re-creates source database
	 */
	private void initialiseSourceDatabase(String createFile) {
		if (connection == null)
			openConnection();
		
		String createQuery = fileContents(createFile);

		queryNoResultSet("DROP SCHEMA public CASCADE");
		queryNoResultSet("CREATE SCHEMA public");
		
		queryNoResultSet(createQuery);
	}
	
	private int queryNoResultSet(String query) {
		int rowsAffected = 0;
		try {
			if (connection == null)
				openConnection();

			java.sql.Statement statement = connection.createStatement();
			log.info("sql query: " + query);
			rowsAffected = statement.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowsAffected;
	}
	
	private String fileContents(String filePath) {
		try {
			FileInputStream fi = new FileInputStream(filePath);

			StringBuffer contents = new StringBuffer("");
			int ch;
			while ((ch = fi.read()) != -1)
				contents.append((char) ch);
			fi.close();

			return contents.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void openConnection() {
		connection = null;

		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "postgres");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}