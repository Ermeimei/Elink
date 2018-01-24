package cn.nju.ws.virtuoso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import cn.nju.ws.config.ConfigureProperty;
import cn.nju.ws.data.Entity;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryGeonames {
	private static String url; 
	private static String usr;
	private static String psd;
	
	public static void init() throws IOException{
		ConfigureProperty.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
	}
	
	public static void main(String[] args) throws IOException {
		QueryGeonames.init();
		String en = "http://sws.geonames.org/29278/";
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		QueryGeonames.queryGeonamesByUri(en,predicateObject);
		Entity e = new Entity(en,predicateObject);
		System.out.println(e);
		System.out.println("*****************");
	}
	
	public static void queryGeonamesByUri(String uri,Map<String,List<String>> predicateObject){
		VirtGraph vg = new VirtGraph(ConfigureProperty.GeonamesVirtGraph,url,usr,psd);
		String query = ConfigureProperty.GeonamesPrefix + "select ?p ?o where {"+
		//"<http://sws.geonames.org/29278/> ?p ?o" +
				"<" + uri + "> ?p ?o." +
				"} limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("p").toString();
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

}
