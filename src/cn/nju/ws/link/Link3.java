package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.nju.ws.data.Entity;
import cn.nju.ws.util.FileUtil;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class Link3 {
	//把候选实体数量小于等于5的实体直接输出。
	static String folder = "data/result3/";
	static String[] type = {"sponsor","bear","loc","test"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		Link3.init();
		//sLink("data/result1/bear/bear10570.ttl",3);
		Link("data/result1/sponsor/",0);
	//	Link("data/result1/bear/",1);
	//	Link("data/result1/loc/",2);
	}
	public static void Link(String filePath,int j) throws FileNotFoundException, IOException {
		List<String> fileList = FileUtil.readDirs(filePath);
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link.ttl"));
		int i = 1;
		for(String file:fileList) {
			System.out.println(i);
			out.write(sLink(file,j).getBytes("UTF-8"));
			i++;
		}
		out.close();
	}
	@SuppressWarnings("resource")
	public static String sLink(String file,int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String s=br.readLine();
		if(s == null)
			return "";
		StringBuffer sb = new StringBuffer();
		s = s.replaceAll("[<>]", "");
		String[] strs = s.trim().split(" ");
		Set<String> tESet = new HashSet<String>();
		tESet.add(strs[2]);
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.replaceAll("[<>]", "");
			strs = s.trim().split(" ");
			tESet.add((strs[2]));
		}
		br.close();
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		if(tESet.size() <= 5) {//如果候选实体少于等于5个，直接输出，top5
			Entity sE = QueryT28.queryT28ByUri(strs[0]);
			List<Entity> tEList = new ArrayList<Entity>();
			for(String t: tESet){
				sb.append(("<" + sE.getSubject() + "> <" + sameas + "> <" + t + "> .\n"));
				tEList.add(QueryBaidu.queryBaiduBaikeByUri((t)));
			}
			OneLink(sE,tEList,j);
		}
		return sb.toString();
	}
	public static void OneLink(Entity sE,List<Entity> tEList,int j) throws IOException {
	//	System.out.print(sE.toString());
		String predicate;
		switch(j) {
		case 0: predicate = "actor1name";break;
		case 1: predicate = "actor2name";break;
		case 2: predicate = "actiongeo_fullname";break;
		default: predicate = "actor2name";
		}
		List<String> name = sE.getPredicate(predicate);
		String s = name.get(0);
		s = s.substring(0, s.length()-3);
		String e = sE.getSubject();
		StringBuffer sb1 = new StringBuffer();
		sb1.append(e+"\t" + s+"\n");
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "/"  + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
		for(Entity te:tEList) {
			sb1.append(te.getSubject()+"\t");
			List<String> title = te.getPredicate("title");
			for(String als:title) {
				sb1.append(als);
				}
			sb1.append("\n");
		}
		out.write(sb1.toString().getBytes("UTF-8"));
		out.close();
	}
}
