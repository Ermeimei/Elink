package cn.nju.ws.virtuoso;

import java.io.IOException;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import cn.nju.ws.config.Configure;
import cn.nju.ws.util.ChineseConvert;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class QueryWiki {
	private static String url; 
	private static String usr;
	private static String psd;
	private static VirtGraph vg;
	
	public static void init() throws IOException{
		Configure.init();
		url = VirtGraphLoader.getUrl();
		usr = VirtGraphLoader.getUser();
		psd = VirtGraphLoader.getPassword();
		vg = new VirtGraph(Configure.WikidataVirtGraph,url,usr,psd);
	}
	public static void queryWikiDataByName(List<String> name,List<String> alias){
		for(String n:name) {
			queryWikiDataByName(n,alias);
		}
	}
	public static void queryWikiDataByName(String name,List<String> alias){
		if(name.equals("中国")) {
			alias.add("中华人民共和国");
			return;
		}
		String query = "select distinct ?entity where {{"+
				"?entity wkdt:altlabel_en \"" + name + "\"@en.}" +
				"union {" +
				"?entity wkdt:altlabel_zh \"" + name + "\"@zh." +
				"}} limit 100";
	//	System.out.println(name);
		Query sparql = QueryFactory.create(Configure.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		//List<String> entities = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode spo = result.get("entity");
			getChineseAlias(spo.toString(),alias);
		}
		vqe.close();
	//	System.out.println(alias);
	}
	public static void getEnglishAlias(String uri,List<String> alias){
		String query = "select ?name where {"+
				"<" + uri + "> wkdt:altlabel_en ?name" +
				"} limit 100";
		Query sparql = QueryFactory.create(Configure.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String name = result.get("name").toString();
			name = name.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
			if(name.length() >= 2)
				alias.add(name.toString());
		}
		vqe.close();
	}
	public static void getChineseAlias(String uri,List<String> alias){
		String query = "select ?name where {"+
				"<" + uri + "> wkdt:altlabel_zh ?name" +
				"} limit 100";
		Query sparql = QueryFactory.create(Configure.WikidataPrefix+query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			String cname = result.get("name").toString();
			cname = cname.substring(0, cname.length()-3);
			cname = cname.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
		//	cname = cname.replaceAll("[\\pP‘’“”]", "");
			if(cname.length() >= 2) {
				cname = ChineseConvert.convert(cname);//繁简体转换
				alias.add(cname);
			}
		}
		vqe.close();
	}
}
