package cn.nju.ws.virtuoso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import cn.nju.ws.config.Configure;
import cn.nju.ws.data.Entity;
import cn.nju.ws.util.PrintUtil;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryBaidu {
	private static String url; 
	private static String usr;
	private static String psd;
	private static VirtGraph vg;
	
	public static void init() throws IOException{
		Configure.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
		vg = new VirtGraph(Configure.BdBkVirtGraph,url,usr,psd);
	}
	public static void main(String[] args) throws IOException {
		QueryBaidu.init();
		String en = "http://baike.baidu.com/A2342137";
	//	Entity e = QueryBaidu.queryBaiduBaikeByUri(en);
	//	System.out.println(e);
	//	System.out.println("*****************");
		System.out.println(QueryBaidu.queryBaiduBaikeInformationByUri(en));
		/*List<String> bdbkEntities = new ArrayList<String>();
		queryBaiduBaikeByName("习近平", bdbkEntities);
		if(bdbkEntities.size() > 0) {
			for(String bdbke:bdbkEntities) {
				System.out.println(bdbke.split("\t")[0]);
			}
		}*/
	}
	public static Entity queryBaiduBaikeByUri(String uri) {
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		//VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = Configure.BdBkPrefix + "select * where {" + 
		 "<" + uri + "> ?p ?o.}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("p").toString();
			String object = result.get("o").toString().replaceAll("@zh", "");
			if(predicate.indexOf("infoboxItem") == -1) {
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
	//	predicateObject.putAll(queryBaiduBaikeInfoboxByUri(uri));
		return new Entity(uri,predicateObject);
	}
	public static void queryBaiduBaikeByNameGeo(String name,List<String> bdbkEntities){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = Configure.BdBkPrefix + "select *  where {" + 
				 "?s gsbaidu:title \""+ name + "\"@zh." +
				 "?s gsbaidu:subtitle ?p." +
				 "}limit 100";
		//Query sparql = QueryFactory.create(ConfigureProperty.BdBkPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode entity = result.get("s");
			RDFNode subtitle = result.get("p");
			bdbkEntities.add(entity.toString() + "\t" + name + "\t" + subtitle.toString());
		//	System.out.println(entity.toString() + "\t" + name + "\t" + subtitle.toString());	
		}
		vqe.close();
	}
	public static void queryBaiduBaikeByName(String name,List<String> bdbkEntities){
		//VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = Configure.BdBkPrefix + "select *  where {" + 
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
		//	flag = true;
		//	System.out.println(entity.toString() + "\t" + name + "\t" + subtitle.toString());	
		}
	//	System.out.println(name);
		if(!flag) {
			query = Configure.BdBkPrefix + "select * where {" + 
					 "?s gsbaidu:alt_label \""+ name + "\"@zh." +
					 "}limit 100";
			vqe = VirtuosoQueryExecutionFactory.create(query, vg);
			results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode entity = result.get("s");
				bdbkEntities.add(entity.toString() + "\t" + name);
			}
		}
		//当名称无法完全匹配时，才用bif:contains。有些时候会漏掉，所以还是需要后面这一步的。
	/*	if(!flag) {
			query = ConfigureProperty.BdBkPrefix + "select * where {" + 
					 "?s gsbaidu:title ?name." +
					 "?s gsbaidu:subtitle ?p." +
					 "?name bif:contains \"'" + name + "'\"." +
					 "}limit 10";
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
		//当bif:contains也找不到结果时，在从别名中找，别名是从词条的infobox中抽出来的。
		if(!flag) {
			query = ConfigureProperty.BdBkPrefix + "select * where {" + 
					 "?s gsbaidu:alt_label ?name." +
					 "?name bif:contains \"'" + name + "'\"." +
					 "}limit 10";
			vqe = VirtuosoQueryExecutionFactory.create(query, vg);
			results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode entity = result.get("s");
				RDFNode alias = result.get("name");
				bdbkEntities.add(entity.toString() + "\t" + alias.toString());
			}
		}*/
		vqe.close();
	}
	//用于生成百度百科词条别名，后期直接查询知识库。
	public static void queryBaiduBaikeAliasByUri() throws IOException {
		int size = Configure.BaiduLabelKeys.length;
		for(int i=0;i<size;i++) {
			queryBaiduBaikeAliasByUri(Configure.BaiduLabelKeys[i]);
		}
	}
	public static void queryBaiduBaikeAliasByUri(String key) throws IOException {
		//VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String altLabel = "<http://ws.nju.edu.cn/geoscholar/baidu#alt_label>";
		FileOutputStream out = new FileOutputStream(new File("alias_" + key + ".ttl"));
		String query = Configure.BdBkPrefix + "select ?s ?content  where {" + 
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
		vqe.close();
	}
	public static String queryBaiduBaikeInformationByUri(String uri) {
		Entity e = QueryBaidu.queryBaiduBaikeByUri(uri);
		StringBuffer sb = new StringBuffer();
		sb.append(e.getSubject()+"\n");
		sb.append("**********************************************************************\n");
		Map<String,List<String>> predicateObject = e.getPredicate_object();
		if(predicateObject.containsKey("title")) {
			sb.append("标题:\t");
			sb.append(PrintUtil.listToString(predicateObject.get("title")," "));
			sb.append("\n");
			sb.append("-----------------------------------------------\n");
		}
		if(predicateObject.containsKey("subtitle")) {
			sb.append("副标题:\t");
			sb.append(PrintUtil.listToString(predicateObject.get("subtitle")," "));
			sb.append("\n");
			sb.append("-----------------------------------------------\n");
		}
		if(predicateObject.containsKey("summary")) {
			sb.append("摘要:\t");
			sb.append(PrintUtil.listToString(predicateObject.get("summary")," "));
			sb.append("\n");
			sb.append("-----------------------------------------------\n");
		}else{
			sb.append(PrintUtil.mapToString(queryBaiduBaikeInfoboxByUri(uri)));
		}
		if(predicateObject.containsKey("page_url")) {
			sb.append("网址:\t");
			sb.append(PrintUtil.listToString(predicateObject.get("page_url")," "));
			sb.append("\n");
		}
		sb.append("**********************************************************************\n");
		return sb.toString();	
	}
	public static Map<String,List<String>> queryBaiduBaikeInfoboxByUri(String uri) {
		String query = Configure.BdBkPrefix + "select ?info_prop,?content where {" + 
				"<" + uri + "> gsbaidu:infoboxItem ?o." + 
				 "?o ns1:description ?info_prop." +
				 "?o gsbaidu:infoboxContent ?info_content." +
				 "?info_content ns1:description ?content." +
				 "}limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		Map<String,List<String>> predicateObject = new HashMap<String,List<String>>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String predicate = result.get("info_prop").toString();
			String object = result.get("content").toString().replaceAll("@zh", "");
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
		return predicateObject;
	}
}
