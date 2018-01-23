package cn.nju.ws.virtuoso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;


import cn.nju.ws.config.ConfigureProperty;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryT28 {
	private static String url; 
	private static String usr;
	private static String psd;
	
	public static void init() throws IOException{
		ConfigureProperty.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
	}
	
	public static Map<String,String> queryT28Sponsor(){
		VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select distinct ?sponsor ?sponsor_name where {"+
				"?event event:sponsor ?sponsor." +
				"?sponsor event:actor1name ?sponsor_name."+
				"}"
				+ "limit 100"
				;
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		Map<String,String> sponsorTName = new HashMap<String,String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode spo = result.get("sponsor");
			RDFNode spo_name = result.get("sponsor_name");
			sponsorTName.put(spo.toString(),spo_name.toString());
	//		System.out.println(spo.toString() + "\t" + spo_name.toString());
		}
		vqe.close();
		return sponsorTName;	
	}
	
	public static Map<String,String> queryT28Bear(){
		VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select distinct ?bear ?bear_name where {"+
				"?event event:bear ?bear." +
				"?bear event:actor2name ?bear_name."+
				"}"
			//	+ "limit 100"
				;
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		Map<String,String> bearTName = new HashMap<String,String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode bear = result.get("bear");
			RDFNode bear_name = result.get("bear_name");
			bearTName.put(bear.toString(),bear_name.toString());
		}
		vqe.close();
		return bearTName;	
	}
	public static Map<String,String> queryT28Loc(){
		VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select distinct ?loc ?loc_name where {"+
				"?event event:loc ?loc." +
				"?loc event:actiongeo_fullname ?loc_name."+
				"}"
			//	+ "limit 100"
				;
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		Map<String,String> locTName = new HashMap<String,String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode loc = result.get("loc");
			RDFNode loc_name = result.get("loc_name");
			locTName.put(loc.toString(),loc_name.toString());
		}
		vqe.close();
		return locTName;	
	}
	
	public static Map<String,String> queryT28ByUri(String uri){
		VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select * where {"+
				"<" + uri + "> ?p ?o." +
				"}";
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		Map<String,String> predicateObject = new HashMap<String,String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode predicate = result.get("p");
			RDFNode object = result.get("o");
			predicateObject.put(predicate.toString(),object.toString());
		}
		vqe.close();
		return predicateObject;	
	}
	
	public static Map<String,String> queryT28EventByUri(String uri){
		VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select * where {"+
				"?event event:sponsor <" + uri + ">." +
				"?event event:event_sentence ?sentence."+
				"}";
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		Map<String,String> predicateObject = new HashMap<String,String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode predicate = result.get("event");
			RDFNode object = result.get("sentence");
			predicateObject.put(predicate.toString(),object.toString());
		}
		vqe.close();
		return predicateObject;	
	}
	
	
}
