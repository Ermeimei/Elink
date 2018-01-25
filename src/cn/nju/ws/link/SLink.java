package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.nju.ws.data.Entity;
import cn.nju.ws.util.ComparatorByLen;
import cn.nju.ws.util.FileUtil;
import cn.nju.ws.util.WordCut;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class SLink {
	static String[] folder = {"data/result2/sponsor/","data/result2/bear/","data/result2/loc/"};
	static String[] type = {"sponsor","bear","loc"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		ELink.init();
		Link("data/result1/sponsor/");
	}
	public static void Link(String filePath) throws FileNotFoundException, IOException {
		List<String> fileList = FileUtil.readDirs(filePath);
		for(String file:fileList) {
			sLink(file,0);
		}
	}
	public static void sLink(String file,int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String s=br.readLine();
		s = s.replaceAll("[<>]", "");
		String[] strs = s.trim().split(" ");
		Entity sE = QueryT28.queryT28ByUri(strs[0]);
		List<Entity> tEList = new ArrayList<Entity>();
		tEList.add(QueryT28.queryT28ByUri(strs[2]));
		for(;s!=null;s=br.readLine()){
			s = s.replaceAll("[<>]", "");
			strs = s.trim().split(" ");
			tEList.add(QueryT28.queryT28ByUri(strs[2]));
		}
		String e = sE.getSubject();
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		if(tEList.size() <= 3) {
			FileOutputStream out = new FileOutputStream(new File(folder[j] + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
			for(Entity te:tEList) {
				StringBuffer sb1 = new StringBuffer();
				sb1.append(("<" + e + "> <" + sameas + "> <" + te.getSubject() + "> .\n"));
				out.write(sb1.toString().getBytes("UTF-8"));
			}
			out.close();
		}
		br.close();
	}
	public static void OneLink(Entity sE,List<Entity> tEList,int j) throws IOException {
		List<String> name = sE.getPredicate("actor1name");
		String s = name.get(0);
		s = s.substring(0, s.length()-3);
		List<String> words = WordCut.wordCut(s);//名称分词
		List<String> sAlias = new ArrayList<String>(words);
		//System.out.println(sAlias.toString());
		QueryWiki.queryWikiDataByName(words,sAlias);//从wikidata中获取别名
		Set<String> alias = new HashSet<String>(sAlias); 
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		String e = sE.getSubject();
		FileOutputStream out = new FileOutputStream(new File(folder[j] + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
		for(Entity te:tEList) {
	//		List<String> tAlias = new ArrayList<String>();
			List<String> title = te.getPredicate("title");
		//	List<String> subtitle = te.getPredicate("subtitle");
		//	List<String> altLabel = te.getPredicate("alt_label");
			for(String als:title) {
				if(als.lastIndexOf("@zh") != -1)
					als = als.substring(0, als.length()-3);
				if(als.length()>0 && alias.contains(als)) {
					StringBuffer sb1 = new StringBuffer();
					sb1.append(("<" + e + "> <" + sameas + "> <" + te.getSubject() + "> .\n"));
					out.write(sb1.toString().getBytes("UTF-8"));
				//	tAlias.add(als);
				}
			}
		}
		out.close();
	}
}
