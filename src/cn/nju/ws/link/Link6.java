package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.nju.ws.nlp.NerFilter;
import cn.nju.ws.util.PrintUtil;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class Link6 {
	//根据命名实体识别的结果，去匹配。目前只用到了三种类型（nr、dev、country）
	static String folder = "data/result6/";
	static String[] type = {"sponsor","bear","loc"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		Link6.init();
	//	System.out.println(eLink("http://28/event#sponsor57417","叙利亚儿童",1));
	//	Link("data/all_s_b_l/all_sponsor_名称非空_27480.txt",0);
		Link("data/all_s_b_l/all_loc_名称非空_6199.txt",2);
	//	Link("data/all_s_b_l/all_bear_名称非空_28027.txt",1);
	}
	public static void Link(String filePath,int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		String[] strs ;
		int i = 1;
		int k = 1;
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_" + i + ".txt"));
		for(s=br.readLine();s!=null;s=br.readLine()){
			if(k<1){
				k++;
				continue;
			}
			strs = s.trim().split("\t");
			System.out.println(i);
			out.write(eLink(strs[0],strs[1],j).getBytes("UTF-8"));
			if(i % 5000 == 0) {
				out.close();
				out = new FileOutputStream(new File(folder + type[j] + "_link_" + i + ".txt"));
			}
			i++;
		}
		br.close();
		out.close();
	}
	public static String eLink(String e,String s,int j) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\t" + s + "\n");
		Set<String> ner = new HashSet<String>();
		QueryT28.queryT28NerResultByUri(e,type[j],ner);
		List<String> results = new ArrayList<String>();
		List<String> bdbkEntities = new ArrayList<String>();
		Set<String> bdbkLinks = new HashSet<String>();
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		NerFilter.ner(s,ner,results);
		for(String n:results) {
		//	System.out.println(n);
			if(s.contains(n)) {
				n = n.replaceAll("[\\\\'\"]", "");
				if(n.equals("中国")) {
					bdbkEntities.add("http://baike.baidu.com/A109632\t中华人民共和国");
					break;
				}
				if(n.equals("德国")) {
					bdbkEntities.add("http://baike.baidu.com/A108817\t德意志联邦共和国");
					break;
				}
				Set<String> resultSet = QueryGeonames.queryGeonamesByZhAlternateName(n);
				if(resultSet != null && resultSet.size() != 0) {
					for(String rset:resultSet)
						bdbkEntities.add(rset + "\t" + n);
					break;
				}
				QueryBaidu.queryBaiduBaikeByName(n,bdbkEntities);
				if(bdbkEntities.size() > 0) 
					break;
			}
		}
		if(bdbkEntities.size() > 0) {
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
		sb.append(PrintUtil.listToString(bdbkEntities));
		sb.append("***********************\n");	
		return sb.toString();
	}
}
