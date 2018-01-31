package cn.nju.ws.virtuoso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import cn.nju.ws.config.Configure;
import cn.nju.ws.data.Entity;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryGeonames {
	private static String url; 
	private static String usr;
	private static String psd;
	private static VirtGraph vg;
	private static VirtGraph vg1;
	
	public static void init() throws IOException{
		Configure.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
		vg = new VirtGraph(Configure.GeonamesVirtGraph,url,usr,psd);
		vg1 = new VirtGraph("http://cglink.com",url,usr,psd);
	}
	
	public static void main(String[] args) throws IOException {
		QueryGeonames.init();
	//	queryGeonamesHavingZhAlternateName();
	/*	Set<String> resultSet=queryGeonamesByZhAlternateName("德国");
		if(resultSet != null) {
			for(String s:resultSet)
				System.out.println(s);
		}
	/*	List<String> bdbkEntities = new ArrayList<String>();
		query("http://sws.geonames.org/4006164/", bdbkEntities);
		System.out.println(bdbkEntities);
		List<String> geonamesEntities = new ArrayList<String>();
		QueryGeonames.queryGeonamesByName("德国", geonamesEntities);
		System.out.println(geonamesEntities);*/
		String en = "http://sws.geonames.org/2921044/";
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		QueryGeonames.queryGeonamesByUri(en,predicateObject);
		Entity e = new Entity(en,predicateObject);
		System.out.println(e);
		System.out.println("*****************");
	}
	public static Set<String> queryGeonamesByZhAlternateName(String name){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.GeonamesVirtGraph,url,usr,psd);
		String query = Configure.GeonamesPrefix + "select distinct ?e where {"+
				"{?e gn:name ?name." + 
                "?name bif:contains \"'" + name + "'\".}" +
				"union"+
                "{?e gn:alternateName ?name."+
                "?name bif:contains \"'" + name + "'\".}" +
                "filter (str(?name) = \"" + name + "\")"+
				"} limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		Set<String> resultSet = null;
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String entity = result.get("e").toString();
			System.out.println(entity);
			List<String> bdbkEntities = new ArrayList<String>();
			queryBdbkLinkGeonames(entity, bdbkEntities);
			if(bdbkEntities.size() > 0) {
				resultSet = new HashSet<String>(bdbkEntities);
			}
		}
		vqe.close();
		return resultSet;
	}
	public static void queryBdbkLinkGeonames(String geoUri,List<String> bdbkEntities){
		String prefix = "PREFIX ns1: <http://www.w3.org/2002/07/owl#>";
		String query =  prefix + "select distinct ?e where {"+
				"?e ns1:sameAs <" + geoUri + ">." + 
				"} limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg1);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String entity = result.get("e").toString();
			bdbkEntities.add(entity);
		}
		vqe.close();
	}
	
	public static boolean queryBdbkLinkGeonames(String geoUri){
		String prefix = "PREFIX ns1: <http://www.w3.org/2002/07/owl#>";
		String query =  prefix + "select distinct ?e where {"+
				"?e ns1:sameAs <" + geoUri + ">." + 
				"} limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg1);
		ResultSet results = vqe.execSelect();
		boolean flag = false;
		if (results.hasNext()) {
			flag = true;
		}
		vqe.close();
		return flag;
	}
	public static void queryGeonamesByUri(String uri,Map<String,List<String>> predicateObject){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.GeonamesVirtGraph,url,usr,psd);
		String query = Configure.GeonamesPrefix + "select ?p ?o where {"+
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
	
	public static void queryGeonamesByName(String name,List<String> geonamesEntities){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.GeonamesVirtGraph,url,usr,psd);
		String query = Configure.GeonamesPrefix + "select distinct ?e where {"+
				"{?e gn:name ?name." + 
                "?name bif:contains \"'" + name + "'\".}" +
				"union"+
                "{?e gn:alternateName ?name."+
                "?name bif:contains \"'" + name + "'\".}" +
                "filter (str(?name) = \"" + name + "\")"+
				"} limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String entity = result.get("e").toString();
			geonamesEntities.add(entity);
		}
		vqe.close();
	}
	public static void queryGeonamesHavingZhAlternateName() throws IOException{
		//VirtGraph vg = new VirtGraph(ConfigureProperty.GeonamesVirtGraph,url,usr,psd);
		String query = Configure.GeonamesPrefix + "select ?e ?name where {"+
                "?e gn:alternateName ?name"+
                " filter (lang(?name) = 'zh')" +
				"}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		FileOutputStream out = new FileOutputStream(new File("geoname_zh.txt"));
		FileOutputStream out1 = new FileOutputStream(new File("geoname_link.ttl"));
		FileOutputStream out2 = new FileOutputStream(new File("geoname_zh_nolink.txt"));
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		StringBuffer sb;
		StringBuffer sb1;
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String entity = result.get("e").toString();
			String name = result.get("name").toString();
			sb = new StringBuffer();	
			sb.append(entity + "\t" + name + "\n");
			out.write(sb.toString().getBytes("UTF-8"));
			List<String> bdbkEntities = new ArrayList<String>();
			queryBdbkLinkGeonames(entity, bdbkEntities);
			if(bdbkEntities.size() == 0) {
				sb1 = new StringBuffer();
				sb1.append((entity + "\t" + name + "\n"));
				out2.write(sb1.toString().getBytes("UTF-8"));
			}
			else {
				sb1 = new StringBuffer();
				for(String s:bdbkEntities) {
					sb1.append(("<" + entity + "> <" + sameas + "> <" + s + "> .\n"));
				}
				out1.write(sb1.toString().getBytes("UTF-8"));
			}
		}
		out.close();
		out1.close();
		out2.close();
		vqe.close();
	}
}
