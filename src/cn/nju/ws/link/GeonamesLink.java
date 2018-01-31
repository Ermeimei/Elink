package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nju.ws.config.Configure;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;

public class GeonamesLink {
	static String folder = "geonames/result2/";
	static Set<String> locDict;
	public static void init() throws IOException {
		QueryBaidu.init();
		QueryGeonames.init();
		loadLocDict(Configure.LocDict);
	}
	public static void loadLocDict(String filePath) throws IOException {
		locDict = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		for(s = br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			locDict.add(s);
		}
		br.close();
	}
	public static void main(String[] args) throws IOException {
		/*FileOutputStream out = new FileOutputStream(new File(folder+"geoname_nolink.txt"));
		Map<String,String> geoLinks = link(folder+"geoname_link.ttl");
		linkStatistics(folder+"geoname_zh.txt",out,geoLinks);
		out.close();
		noLinkNames(folder+"geoname_nolink.txt");
		FileOutputStream out = new FileOutputStream(new File(folder+"geoname_tobelink_le1.txt"));
		geonamesTobeLinked(folder+"geoname_nolink.txt",out);
		out.close();*/
		GeonamesLink.init();
		gLink("geonames/geoname_tobelink_le1.txt");
	}
	public static void gLink(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		String[] strs ;
		int i = 1;
		//13874
		int k = 1;
		FileOutputStream out = new FileOutputStream(new File(folder+"geonames_link_" + i + ".txt"));
		FileOutputStream rout = new FileOutputStream(new File(folder+"geonames_link_" + i + ".ttl"));
		for(s=br.readLine();s!=null;s=br.readLine()){
			if(k<1){
				k++;
				continue;
			}
			strs = s.trim().split("\t");
			System.out.println(i);
			out.write(eLink(strs[0],strs[1],rout).getBytes("UTF-8"));
			if(i % 10000 == 0) {
				out.close();
				out = new FileOutputStream(new File(folder + "geonames_link_" + i + ".txt"));
			}
			i++;
		}
		br.close();
		out.close();
		rout.close();
	}
	public static String eLink(String e,String s,FileOutputStream out) throws IOException {
		s = s.substring(0, s.length()-3);//去掉末尾的@zh和@en
		StringBuffer sb = new StringBuffer();
		List<String> bdbkEntities = new ArrayList<String>();
		QueryBaidu.queryBaiduBaikeByNameGeo(s,bdbkEntities);
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		if(bdbkEntities.size() > 0 && bdbkEntities.size() < 6) {//当结果数量大于5时，很有可能就是人名，不可能是地点
			sb.append(e + "\t" + s + "\n");
			if(bdbkEntities.size() == 1) {//只有一个匹配结果，直接输出。
				out.write(("<" + e + "> <" + sameas + "> <" + bdbkEntities.get(0).split("\t")[0] + "> .\n").getBytes("UTF-8"));
				sb.append((bdbkEntities.get(0) + "\n"));
			}
			else {//多于一个的匹配结果，利用subtitle的信息进行匹配。如果subtitle的信息显示这个实体是一个地点，则输出，否则直接丢弃。
				Set<String> links = new HashSet<String>();
				Set<String> infos = new HashSet<String>();
				for(String bdbke:bdbkEntities) {
					String[] splits = bdbke.split("\t");
					for(String st:locDict) {
						if(splits[2].contains(st)) {
							links.add(splits[0]);
							infos.add(bdbke);
						}
					}
				}
				for(String link:links) {
					out.write(("<" + e + "> <" + sameas + "> <" + link + "> .\n").getBytes("UTF-8"));
				}
				for(String info:infos) {
					sb.append((info+"\n"));
				}
			}
			sb.append("***********************\n");	
		}
		return sb.toString();
	}
	public static void geonamesTobeLinked(String filePath,FileOutputStream out) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s = br.readLine().trim();
		String[] t = s.split("\t");
		String st = t[1];
		List<String> strs = new ArrayList<String>();
		for(;s!=null;s=br.readLine()){
			s = s.trim();
			t = s.split("\t");
			if(st.equals(t[1])) {
				strs.add(t[0]);
			}
			else {
				if(strs.size() <= 1) {
					for(String str:strs) {
						out.write((str+ "\t" + st + "\n").getBytes("UTF-8"));
					}
				}
				strs = new ArrayList<String>();
				strs.add(t[0]);
			}
			st = t[1];
		}
		br.close();
	}
	public static void noLinkNames(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		Map<String,String> geoLinks = new HashMap<String,String>();
		String s;
		for(s = br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			geoLinks.put(s.split("\t")[1], "");
		}
		br.close();
		FileOutputStream out = new FileOutputStream(new File("geoname_nolinkNames.txt"));
		Set<String> keys = geoLinks.keySet();
		for(String k:keys) {
			out.write((k + "\n").getBytes("UTF-8"));
		}
		out.close();
	}
	public static Map<String,String> link(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		Map<String,String> geoLinks = new HashMap<String,String>();
		String s;
		for(s = br.readLine();s!=null;s=br.readLine()){
			String t = s.split(" ")[0];
			t = t.substring(1, t.length()-1);
			geoLinks.put(t, "");
		}
		br.close();
		return geoLinks;
	}
	public static void linkStatistics(String filePath,FileOutputStream out,Map<String,String> geoLinks) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s = br.readLine().trim();
		String[] t = s.split("\t");
		String st = t[1];
		List<String> strs = new ArrayList<String>();
		for(;s!=null;s=br.readLine()){
			s = s.trim();
			t = s.split("\t");
			if(st.equals(t[1])) {
				strs.add(t[0]);
			}
			else {
				boolean flag = false;
				for(String str:strs) {
					if(geoLinks.containsKey(str)) {
				//	if(QueryGeonames.queryBdbkLinkGeonames(str)) {
						flag = true;
						break;
					}
				}
				if(!flag) {
					for(String str:strs) {
						out.write((str+ "\t" + st + "\n").getBytes("UTF-8"));
					}
				}
				strs = new ArrayList<String>();
				strs.add(t[0]);
			}
			st = t[1];
		}
		br.close();
	}
}
