package cn.nju.ws.virtuoso;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import cn.nju.ws.config.ConfigureProperty;
import cn.nju.ws.util.ChineseConvert;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryWiki {
	private static String url; 
	private static String usr;
	private static String psd;
	
	public static void init() throws IOException{
		ConfigureProperty.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
	}
	public static void queryWikiDataByName(List<String> name,Set<String> alias){
		for(String n:name) {
			queryWikiDataByName(n,alias);
		}
	}
	public static void queryWikiDataByName(String name,Set<String> alias){
		if(name.equals("中国")) {
			alias.add("中华人民共和国");
			return;
		}
		VirtGraph vg = new VirtGraph(ConfigureProperty.WikidataVirtGraph,url,usr,psd);
		String query = "select distinct ?entity where {{"+
				"?entity wkdt:altlabel_en \"" + name + "\"@en.}" +
				"union {" +
				"?entity wkdt:altlabel_zh \"" + name + "\"@zh." +
				"}} limit 100";
	//	System.out.println(query);
		Query sparql = QueryFactory.create(ConfigureProperty.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		//List<String> entities = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode spo = result.get("entity");
			getChineseAlias(vg,spo.toString(),alias);
		}
		vqe.close();
	//	System.out.println(alias);
	}
	public static void getEnglishAlias(VirtGraph vg,String uri,Set<String> alias){
		String query = "select ?name where {"+
				"<" + uri + "> wkdt:altlabel_en ?name" +
				"} limit 100";
		Query sparql = QueryFactory.create(ConfigureProperty.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode name = result.get("name");
			alias.add(name.toString());
		}
	}
	public static void getChineseAlias(VirtGraph vg,String uri,Set<String> alias){
		String query = "select ?name where {"+
				"<" + uri + "> wkdt:altlabel_zh ?name" +
				"} limit 100";
		Query sparql = QueryFactory.create(ConfigureProperty.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode name = result.get("name");
			String cname = name.toString();
			cname = cname.substring(0, cname.length()-3);
			cname = ChineseConvert.convert(cname);//繁简体转换
			alias.add(cname);
		}
	}

}
