package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Link2_1 {
	/* result2的改进版，只选取前5个答案。这个对人名处理不好，但是人名的可以单独处理嘛。
	 * 人名的用其他结果，其他结果没有结果再用这个结果。
	 */
	static String folder = "data/result2_1/";
	static String[] type = {"sponsor","bear","loc"};
	public static void main(String[] args) throws IOException {
		Link("data/result2/loc_link_1.txt",2);
		for(int i = 1; i < 7;i++)
			Link("data/result2/loc_link_"+ (i*1000) + ".txt",2);
	}
	public static void Link(String filePath,int j) throws IOException {
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
				//额外输出了一行所有名称
				if(strs.size()<3) {
					strs = new ArrayList<String>();
					continue;
				}
				String e = strs.get(0).split("\t")[0];
				FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "/" + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
				List<String> links = new ArrayList<String>();
				int count = 0;
				for(int k = 2;k<strs.size();k++) {
					String t = strs.get(k).split("\t")[0];
					if(!links.contains(t)) {
						links.add(t);
						count++;
					}
					if(count == 5)
						break;
				}
				StringBuffer sb1 = new StringBuffer();
				for(String l:links) {
					sb1.append(("<" + e + "> <" + sameas + "> <" + l + "> .\n"));
				}
				out.write(sb1.toString().getBytes("UTF-8"));
				out.close();
				strs = new ArrayList<String>();
			}
		}
		br.close();
	}
}
