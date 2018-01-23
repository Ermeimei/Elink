package cn.nju.ws.virtuoso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import cn.nju.ws.config.ConfigureProperty;
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
		QueryBaidu.queryBaiduBaikeAliasByUri();
	}
	public static void queryBaiduBaikeByUri(String uri) {
		VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		String query = ConfigureProperty.BdBkPrefix + "select ?tag,?info_prop,?content where {{" + 
		 "<" + uri + "> gsbaidu:openTag ?tag." +
		 "} union {" +
		 "<" + uri + "> gsbaidu:infoboxItem ?o." + 
		 "?o ns1:description ?info_prop." +
		 "?o gsbaidu:infoboxContent ?info_content." +
		 "?info_content ns1:description ?content." +
		 "}}limit 100";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode tag = result.get("tag");
			RDFNode infoProp = result.get("info_prop");
			RDFNode content = result.get("content");
			System.out.println(tag + "\t" + infoProp + "\t" + content);	
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
			//	System.out.println(entity.toString() + "\t" + title.toString()+ "\t" + subtitle.toString());	
			}
		}
	}
	public static void queryBaiduBaikeAliasByUri() throws IOException {
		VirtGraph vg = new VirtGraph(ConfigureProperty.BdBkVirtGraph,url,usr,psd);
		int size = ConfigureProperty.BaiduLabelKeys.length;
		String altLabel = "<http://ws.nju.edu.cn/geoscholar/baidu#alt_label>";
		for(int i=0;i<size;i++) {
			FileOutputStream out = new FileOutputStream(new File("alias" + i + ".ttl"));
			String query = ConfigureProperty.BdBkPrefix + "select ?s ?content  where {" + 
				//	 "<" + uri + "> gsbaidu:infoboxItem ?o." +
					 "?s gsbaidu:infoboxItem ?o." +
					 "?o ns1:description \"" + ConfigureProperty.BaiduLabelKeys[i] + "\"." + 
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
}
