package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.nju.ws.util.ComparatorByLen;
import cn.nju.ws.util.PrintUtil;
import cn.nju.ws.util.WordCut;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class Link2 {
	/* result1是QueryBaidu.queryBaiduBaikeByName()方法包含bif：contains的结果。
	 * result2是QueryBaidu.queryBaiduBaikeByName()方法不包含bif：contains的结果。
	 * 所以区别在于queryBaiduBaikeByName()方法不同，result1的结果数量明显比result2多。
	 */
	//static String folder = "data/result1/";
	static String folder = "data/result2/";
	static String[] type = {"sponsor","bear","loc"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		Link2.init();
	//	System.out.println(eLink("http://28/event#sponsor25758","印度驻伊斯兰堡",1));
	//	Link("data/all_s_b_l/all_sponsor_名称非空_27480.txt",0);
		Link("data/all_s_b_l/all_loc_名称非空_6199.txt",2);
	//	Link("data/all_s_b_l/all_bear_名称非空_28027.txt",1);
	}
	public static void Link(String filePath,int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		String[] strs ;
		int i = 1700;
		//13874
		int k = 1;
	//	s=br.readLine();
	//	strs = s.trim().split("\t");
	//	System.out.println(strs[0] + "\t" + strs[1]);
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_" + i + ".txt"));
		for(s=br.readLine();s!=null;s=br.readLine()){
			if(k<1700){
				k++;
				continue;
			}
			strs = s.trim().split("\t");
			System.out.println(i);
			out.write(eLink(strs[0],strs[1],j).getBytes("UTF-8"));
			if(i % 1000 == 0) {
				out.close();
				out = new FileOutputStream(new File(folder + type[j] + "_link_" + i + ".txt"));
			}
			i++;
		}
		br.close();
		out.close();
	}
	public static String eLink(String e,String s,int j) throws IOException {
	/*	s = s.substring(0, s.length()-3);//去掉末尾的@zh和@en
	//	System.out.println(s);
		if(s.length() == 0) {
			System.out.println(type[j] + "Name为空");
			return "";
		}*/
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\t" + s + "\n");
		List<String> words = WordCut.wordCut(s);//名称分词
		List<String> aliasList = new ArrayList<String>(words);
		//System.out.println(aliasList.toString());
		QueryWiki.queryWikiDataByName(words,aliasList);//从wikidata中获取别名
		Set<String> alias = new TreeSet<String>(new ComparatorByLen()); //根据别名长短排序
		for(String word:aliasList) {
			alias.add(word);
		}
		sb.append(alias.toString()+"\n");
	//	System.out.println(alias.toString());
		Iterator<String> entry = alias.iterator();
		List<String> bdbkEntities = new ArrayList<String>();
		while(entry.hasNext()) {
			QueryBaidu.queryBaiduBaikeByName(entry.next(),bdbkEntities);
		}
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		if(bdbkEntities.size() > 0) {
			Set<String> bdbkLinks = new HashSet<String>();
			for(String bdbke:bdbkEntities) {
				bdbkLinks.add(bdbke.split("\t")[0]);
			}
			FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "/" + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
			for(String link:bdbkLinks) {
				StringBuffer sb1 = new StringBuffer();
				sb1.append(("<" + e + "> <" + sameas + "> <" + link + "> .\n"));
				out.write(sb1.toString().getBytes("UTF-8"));
			}
			out.close();
		}
		sb.append(PrintUtil.listToString(bdbkEntities,"\n"));
		sb.append("***********************\n");	
		return sb.toString();
	}
}
