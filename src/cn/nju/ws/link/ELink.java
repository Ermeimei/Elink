package cn.nju.ws.link;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nju.ws.util.ListUtil;
import cn.nju.ws.util.WordCut;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class ELink {
	static String[] folder = {"data/sponsor/","data/bear/","data/loc/"};
	static String[] type = {"Sponsor","Bear","Loc"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		ELink.init();
		int i = 1;
		Map<String,String> entities;
	//	System.out.println(eLink("http://28/event#sponsor12224","旁遮普省阿塔克地区@zh"));
		for(int j = 2;j<3;j++) {
			i = 1;
			if(j == 0) {
				 entities = QueryT28.queryT28Sponsor();
			}
			else if(j == 1) {
				 entities = QueryT28.queryT28Bear();
			}
			else {
				entities = QueryT28.queryT28Loc();
			}
			FileOutputStream out = new FileOutputStream(new File("data/" + type[j] + "_link_" + i + ".txt"));
		//	Map<String,String> entities = QueryT28.queryT28Bear();
		//	FileOutputStream out = new FileOutputStream(new File("data/bear_link.txt"));
		//    Map<String,String> entities = QueryT28.queryT28Loc();
		//	FileOutputStream out = new FileOutputStream(new File("data/loc_link.txt"));
			for(Map.Entry<String,String> en:entities.entrySet()) {
				out.write(eLink(en.getKey(),en.getValue(),j).getBytes("UTF-8"));
				if(i % 1000 == 0) {
					out.close();
					out = new FileOutputStream(new File("data/" + type[j] + "_link_" + i + ".txt"));
				}
				i++;
				//System.out.println("***********************");
			}
			out.close();
		}
	}
	@SuppressWarnings("resource")
	public static String eLink(String e,String s,int j) throws UnsupportedEncodingException, IOException {
		s = s.substring(0, s.length()-3);//去掉末尾的@zh和@en
	//	System.out.println(s);
		if(s.length() == 0) {
			System.out.println(type[j] + "Name为空");
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\t" + s + "\n");
		List<String> words = WordCut.wordCut(s);//名称分词
		Set<String> alias = new HashSet<String>();
		for(String word:words) {
			alias.add(word);
		}
		QueryWiki.queryWikiDataByName(words,alias);//从wikidata中获取别名
		sb.append(alias.toString()+"\n");
		Iterator<String> entry = alias.iterator();
		List<String> bdbkEntities = new ArrayList<String>();
		while(entry.hasNext()) {
			QueryBaidu.queryBaiduBaikeByName(entry.next(),bdbkEntities);
		}
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		FileOutputStream out = new FileOutputStream(new File(folder[j] + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
		for(String bdbke:bdbkEntities) {
			out.write(("<" + e + "> " + sameas + " <" + bdbke.split("\t")[0] + "> .\n").getBytes("UTF-8"));
		}
		out.close();
		sb.append(ListUtil.listToString(bdbkEntities));
		sb.append("***********************\n");	
		return sb.toString();
	}
}
