package cn.nju.ws.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocResultFilter {
	//针对loc类型的实体，每一个包含多个地名，尽量关联小范围地点，并尽可能保证正确性。
	static String folder = "data/result2/";
	static String result = "result/result/loc_supp_2/";
	static String type = "loc";
	public static void main(String[] args) throws IOException {
	/*	FileOutputStream out = new FileOutputStream(new File(folder + type + "_link_new_4.txt"));
		filterOld("data/result2/loc_link_1.txt",out);
		for(int i = 1; i < 7;i++)
			filterOld("data/result2/loc_link_"+ (i*1000) + ".txt",out);
		out.close();*/
		filterNew("data/result2/loc_link_1.txt");
		for(int i = 1; i < 7;i++)
			filterNew("data/result2/loc_link_"+ (i*1000) + ".txt");
	//	System.out.println(preprocess("首都（汉语词语）@zh"));
	}
	/*public static String preprocess(String label) {
		return label.replaceAll("（(.*)）|@zh", "");
	}*/
	public static void filterOld(String filePath,FileOutputStream out) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		List<String> strs = new ArrayList<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(!s.equals("***********************")) {
				strs.add(s);
			}
			else {
				//多了一行所有名称
				if(strs.size()<3) {
					strs = new ArrayList<String>();
					continue;
				}
				Map<String,List<String>> linkMap = new HashMap<String,List<String>>();
				for(int k = 2;k<strs.size();k++) {
					String[] split = strs.get(k).split("\t");
					String label = split[1].replaceAll("（(.*)）|@zh", "");
					if(linkMap.containsKey(label)) {
						List<String> uris = linkMap.get(label);
						if(!uris.contains(split[0]))
							uris.add(split[0]);
					}else {
						List<String> uris = new ArrayList<String>();
						uris.add(split[0]);
						linkMap.put(label, uris);
					}
				}
				StringBuffer sb1 = new StringBuffer();
				sb1.append(strs.get(0)+"\n");
				String locLabel = strs.get(0).split("\t")[1];
				for(String key:linkMap.keySet()) {
					List<String> uris = linkMap.get(key);
					if(locLabel.contains(key) && uris.size() <= 4) {
						for(String uri:uris)
							sb1.append(uri+"\t"+key+"\n");
					}
				}
				sb1.append("***********************\n");
				out.write(sb1.toString().getBytes("UTF-8"));
				strs = new ArrayList<String>();
			}
		}
		br.close();
	}
	public static void filterNew(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		String sameas = "http://www.w3.org/2002/07/owl#sameAs";
		List<String> strs = new ArrayList<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(!s.equals("***********************")) {
				strs.add(s);
			}
			else {
				//多了一行所有名称
				if(strs.size()<3) {
					strs = new ArrayList<String>();
					continue;
				}
				String locLabel = strs.get(0).split("\t")[1];
				Map<String,List<String>> linkMap = new HashMap<String,List<String>>();
				for(int k = 2;k<strs.size();k++) {
					String[] split = strs.get(k).split("\t");
					String label = split[1].replaceAll("（(.*)）|@zh", "");
					if(linkMap.containsKey(label)) {
						List<String> uris = linkMap.get(label);
						if(!uris.contains(split[0]))
							uris.add(split[0]);
					}else {
						List<String> uris = new ArrayList<String>();
						uris.add(split[0]);
						linkMap.put(label, uris);
					}
				}
				String e = strs.get(0).split("\t")[0];
				File linkFile = new File(result+e.substring(e.lastIndexOf("#")+1) + ".ttl");
				List<String> links = new ArrayList<String>();
				if(linkFile.exists()) {
					BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(linkFile),"UTF-8"));
					for(String s1=br1.readLine();s1!=null;s1=br1.readLine()){
						s1 = s1.trim();
					//	System.out.print(s1);
						links.add(s1.split(" ")[2]);
					}
					br1.close();
					for(String key:linkMap.keySet()) {
						List<String> uris = linkMap.get(key);
						if(locLabel.contains(key) && uris.size() <= 2) {
							boolean flag = true;
							for(String uri:uris) {
								if(links.contains(uri)) {
									flag = false;
									break;
								}
							}
							if(flag) {
								for(String uri:uris) {
									links.add(uri);
								}
							}
						}
					}
				}
				else {
					for(String key:linkMap.keySet()) {
						List<String> uris = linkMap.get(key);
						if(locLabel.contains(key) && uris.size() <= 2) {
							for(String uri:uris)
								links.add(uri);
						}
					}
				}
				StringBuffer sb1 = new StringBuffer();
				for(String l:links) {
					sb1.append(("<" + e + "> <" + sameas + "> <" + l + "> .\n"));
				}
				if(links.size() > 0) {
					FileOutputStream out = new FileOutputStream(linkFile);
					out.write(sb1.toString().getBytes("UTF-8"));
					out.close();
				}
				strs = new ArrayList<String>();
			}
		}
		br.close();
	}
}
