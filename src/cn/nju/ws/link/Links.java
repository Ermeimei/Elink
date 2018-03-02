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

import cn.nju.ws.data.Entity;
import cn.nju.ws.sim.Distance;
import cn.nju.ws.util.FileUtil;
import cn.nju.ws.util.ArraySort;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryGeonames;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class Links {
	//根据名称的相似度排序，取相似度最高的前5个。通过相似度计算很不准。。。
	static String folder = "data/result/";
	static String[] type = {"sponsor","bear","loc","test"};
	public static void init() throws IOException {
		QueryWiki.init();
		QueryBaidu.init();
		QueryT28.init();
		QueryGeonames.init();
	}
	public static void main(String[] args) throws IOException {
		Links.init();
		sLink("data/result1/bear/bear112359.ttl",3);
	//	Link("data/result1/sponsor/",0);
	//	Link("data/result1/bear/",1);
	//	Link("data/result1/loc/",2);
	}
	public static void Link(String filePath,int j) throws IOException {
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
		String sb = "";
		if(tESet.size() > 5) {
			Entity sE = QueryT28.queryT28ByUri(strs[0]);
			List<Entity> tEList = new ArrayList<Entity>();
			for(String t: tESet){
				tEList.add(QueryBaidu.queryBaiduBaikeByUri((t)));
			}
			sb = OneLink(sE,tEList,j);
		}
		return sb;
	}
	public static String OneLink(Entity sE,List<Entity> tEList,int j) throws IOException {
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
		StringBuffer sb = new StringBuffer();
		sb.append(e+"\t" + s+"\n");
		double[] tESim = new double[tEList.size()];
		int k = 0;
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "/"  + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
		for(Entity te:tEList) {
			double sim = -1000;
			List<String> title = te.getPredicate("title");
			for(String als:title) {
				System.out.print(s + "\t" + als + "\t");
			//	sim = new Jaro().score(s, als);
				sim = Distance.Levensteindistance(s, als);
			}
			System.out.println(sim);
			tESim[k++] = sim;
		}
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		int[] rs = new int[tEList.size()];
		ArraySort.sort(tESim, rs, tEList.size());
		int size = (tEList.size()>5)?5:tEList.size();
		StringBuffer sb1 = new StringBuffer();
		for(int i=0;i<size;i++) {
			Entity t = tEList.get(rs[i]);
			sb1.append(("<" + e + "> <" + sameas + "> <" + t.getSubject() + "> .\n"));
			sb.append(t.getSubject() + "\t" + t.getPredicate("title").toString()+ tESim[rs[i]]+ "\n");
		}
		out.write(sb.toString().getBytes("UTF-8"));
		out.close();
		return sb1.toString();
	}
}
