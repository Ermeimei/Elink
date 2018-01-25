package cn.nju.ws.virtuoso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;


import cn.nju.ws.config.ConfigureProperty;
import cn.nju.ws.data.Entity;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryT28 {
	private static String url; 
	private static String usr;
	private static String psd;
	private static VirtGraph vg;
	
	public static void init() throws IOException{
		ConfigureProperty.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
		vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
	}
	public static void main(String[] args) throws IOException {
		QueryT28.init();
		String en = "http://28/event#sponsor88165";
		Entity e = queryT28ByUri(en);
		System.out.println(e);
		System.out.println("*****************");
	}
	
	public static Map<String,String> queryT28Sponsor(){
	//	VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select distinct ?sponsor ?sponsor_name where {"+
				"?event event:sponsor ?sponsor." +
				"?sponsor event:actor1name ?sponsor_name."+
				"}"
			//	+ "limit 10"
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
		//VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
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
		//VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select distinct ?loc ?loc_name where {"+
				"?event event:loc ?loc." +
				"?loc event:actiongeo_fullname ?loc_name."+
				"}"
		//		+ "limit 100"
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
	
	public static Entity queryT28ByUri(String uri){
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		queryT28PropertyByUri(uri,predicateObject);
		queryT28EventByUri(uri,predicateObject);
		return new Entity(uri,predicateObject);
	}
	public static void queryT28PropertyByUri(String uri,Map<String,List<String>> predicateObject){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select * where {"+
				"<" + uri + "> ?p ?o." +
				"}";
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("p").toString();
			if(predicate.lastIndexOf("#") != -1 ) {
				predicate = predicate.substring(predicate.lastIndexOf("#")+1);
			}
			String object = result.get("o").toString();
			if(predicateObject.containsKey(predicate)) {
    			List<String> list = predicateObject.get(predicate);
				list.add(object);
    		}
    		else {
    			List<String> list = new ArrayList<String>();
				list.add(object);
				predicateObject.put(predicate, list);
    		}
		}
		vqe.close();
	}
	
	public static void queryT28EventByUri(String uri,Map<String,List<String>> predicateObject){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.T28VirtGraph,url,usr,psd);
		String query = "select * where {"+
				"?event event:sponsor <" + uri + ">." +
				"?event event:event_sentence ?sentence."+
				"}";
		Query sparql = QueryFactory.create(ConfigureProperty.T28Prefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
		//	RDFNode predicate = result.get("event");
			String predicate = "event";
			String object = result.get("sentence").toString();
			if(object.lastIndexOf("@zh") != -1)
				object = object.substring(0, object.lastIndexOf("@zh"));
			if(predicateObject.containsKey("event")) {
    			List<String> list = predicateObject.get(predicate);
				list.add(object);
    		}
    		else {
    			List<String> list = new ArrayList<String>();
				list.add(object);
				predicateObject.put(predicate, list);
    		}
		}
		vqe.close();
	}
}
