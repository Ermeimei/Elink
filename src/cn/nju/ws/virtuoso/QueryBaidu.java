package cn.nju.ws.virtuoso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import cn.nju.ws.config.ConfigureProperty;
import cn.nju.ws.data.Entity;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryBaidu {
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
		QueryBaidu.init();
		String en = "http://baike.baidu.com/A2262927";
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		QueryBaidu.queryBaiduBaikeByUri(en,predicateObject);
		Entity e = new Entity(en,predicateObject);
		System.out.println(e);
		System.out.println("*****************");
	}
	public static void queryBaiduBaikeByUri(String uri,Map<String,List<String>> predicateObject) {
		VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = ConfigureProperty.BdBkPrefix + "select * where {" + 
		 "<" + uri + "> ?p ?o.}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("p").toString();
			String object = result.get("o").toString();
			if(predicate.indexOf("infoboxItem") == -1 && predicate.indexOf("page_url") == -1) {
				if(predicate.lastIndexOf("#") != -1 ) {
					predicate = predicate.substring(predicate.lastIndexOf("#")+1);
				}
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
		}
		query = ConfigureProperty.BdBkPrefix + "select ?info_prop,?content where {" + 
		"<" + uri + "> gsbaidu:infoboxItem ?o." + 
		 "?o ns1:description ?info_prop." +
		 "?o gsbaidu:infoboxContent ?info_content." +
		 "?info_content ns1:description ?content." +
		 "}limit 100";
		vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("info_prop").toString();
			String object = result.get("content").toString();
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
	}
	public static void queryBaiduBaikeByName(String name,List<String> bdbkEntities){
		VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = ConfigureProperty.BdBkPrefix + "select *  where {" + 
				 "?s gsbaidu:title \""+ name + "\"@zh." +
				 "?s gsbaidu:subtitle ?p." +
				 "}limit 100";
		//Query sparql = QueryFactory.create(ConfigureProperty.BdBkPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		boolean flag = false;
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode entity = result.get("s");
			RDFNode subtitle = result.get("p");
			bdbkEntities.add(entity.toString() + "\t" + name + "\t" + subtitle.toString());
			flag = true;
		//	System.out.println(entity.toString() + "\t" + name + "\t" + subtitle.toString());	
		}
		//当名称无法完全匹配时，才用bif：contains。
		if(!flag) {
			query = ConfigureProperty.BdBkPrefix + "select * where {" + 
					 "?s gsbaidu:title ?name." +
					 "?s gsbaidu:subtitle ?p." +
					 "?name bif:contains \"'" + name + "'\"." +
					 "}limit 100";
			vqe = VirtuosoQueryExecutionFactory.create(query, vg);
			results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode entity = result.get("s");
				RDFNode title = result.get("name");
				RDFNode subtitle = result.get("p");
				bdbkEntities.add(entity.toString() + "\t" + title.toString()+ "\t" + subtitle.toString());
			//	flag = true;
				//	System.out.println(entity.toString() + "\t" + title.toString()+ "\t" + subtitle.toString());	
			}
		}
		if(!flag) {
			query = ConfigureProperty.BdBkPrefix + "select * where {" + 
					 "?s gsbaidu:alt_label ?name." +
					 "?name bif:contains \"'" + name + "'\"." +
					 "}limit 100";
			vqe = VirtuosoQueryExecutionFactory.create(query, vg);
			results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode entity = result.get("s");
				RDFNode alias = result.get("name");
				bdbkEntities.add(entity.toString() + "\t" + alias.toString());
			}
		}
	}
	//用于生成百度百科词条别名，后期直接查询知识库。
	public static void queryBaiduBaikeAliasByUri() throws IOException {
		int size = ConfigureProperty.BaiduLabelKeys.length;
		for(int i=0;i<size;i++) {
			queryBaiduBaikeAliasByUri(ConfigureProperty.BaiduLabelKeys[i]);
		}
	}
	public static void queryBaiduBaikeAliasByUri(String key) throws IOException {
		VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String altLabel = "<http://ws.nju.edu.cn/geoscholar/baidu#alt_label>";
		FileOutputStream out = new FileOutputStream(new File("alias_" + key + ".ttl"));
		String query = ConfigureProperty.BdBkPrefix + "select ?s ?content  where {" + 
				//	 "<" + uri + "> gsbaidu:infoboxItem ?o." +
				"?s gsbaidu:infoboxItem ?o." +
				"?o ns1:description \"" + key + "\"." + 
				"?o gsbaidu:infoboxContent ?info_content." +
				"?info_content ns1:description ?content." +
				"}";
			//System.out.println(ConfigureProperty.BaiduLabelKeys[i]);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			StringBuffer sb = new StringBuffer();
			sb.append("<" + result.get("s") +"> " + altLabel + " \""+result.get("content") + "\"@zh .\n");
			out.write(sb.toString().getBytes("UTF-8"));
		}
		out.close();
	}
}
