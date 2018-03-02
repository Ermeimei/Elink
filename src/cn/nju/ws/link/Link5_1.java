package cn.nju.ws.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Link5_1 {
	/* 把链接结果展示出来，便于查看链接结果的准确性。
	 */
	static String folder = "data/result5_1/";
	static String[] type = {"sponsor","bear","loc"};
	public static void main(String[] args) throws IOException {
		/*int j = 2;
		int k = 7;
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_2.txt"));
		display("data/result2/" + type[j] + "_link_1.txt",out);
		for(int i = 1; i < k;i++)
			display("data/result2/" + type[j] + "_link_" + (i*1000) + ".txt",out);
		out.close();*/
		int j = 2;
		int k = 7;
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_new.txt"));
		display("data/result5/" + type[j] + "_link_1.txt",out);
		for(int i = 1; i < k;i++)
			display("data/result5/" + type[j] + "_link_" + (i*1000) + ".txt",out);
		out.close();
	/*	int j = 0;
		int k = 18;
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_2.txt"));
		display("data/result2/" + type[j] + "_link_1.txt",out);
		for(int i = 1; i < k;i++)
			display("data/result2/" + type[j] + "_link_" + (i*1000) + ".txt",out);
		display("data/result2/" + type[j] + "_link_17745.txt",out);
		for(int i = k; i < 28;i++)
			display("data/result2/" + type[j] + "_link_" + (i*1000) + ".txt",out);
		out.close();*/
	}
	public static void display(String filePath,FileOutputStream out) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		List<String> strs = new ArrayList<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(!s.equals("***********************")) {
				strs.add(s);
			}
			else {
				//相比result2没有别名那一行
				if(strs.size()<2) {
					strs = new ArrayList<String>();
					continue;
				}
				out.write((strs.get(0)+"\n").getBytes("UTF-8"));
				List<String> links = new ArrayList<String>();
				for(int k = 1;k<strs.size();k++) {
					String t = strs.get(k).split("\t")[0];
					if(!links.contains(t)) {
						links.add(t);
						out.write((strs.get(k)+"\n").getBytes("UTF-8"));
					}
				}
				out.write((s+"\n").getBytes("UTF-8"));
				strs = new ArrayList<String>();
			}
		}
		br.close();
	}
}
