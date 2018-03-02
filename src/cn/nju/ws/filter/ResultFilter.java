package cn.nju.ws.filter;

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

import cn.nju.ws.config.Configure;

public class ResultFilter {
	/* 优化实验结果,把一些影视作品，文学作品过滤掉
	 */
	static String folder = "data/result5_1/";
	static String[] type = {"sponsor","bear","loc"};
	static Set<String> filterDict;
	public static void main(String[] args) throws IOException {
		int i = 0;
		init();
		filter2(i);
	}
	public static void init() throws IOException {
		Configure.init();
		loadFilterDict(Configure.FilterDict);
	}
	/* 优化实验结果,把一些影视作品，文学作品过滤掉
	 */
	public static void filter1(int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(folder + type[j] + "_link_1.txt"),"UTF-8"));
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_2.txt"));
		String s;
		List<String> strs = new ArrayList<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(!s.equals("***********************")) {
				strs.add(s);
			}
			else {
				StringBuffer sb1 = new StringBuffer();
				sb1.append(strs.get(0)+"\n");
				for(int k = 1;k<strs.size();k++) {
					if(judge(strs.get(k)))
						sb1.append(strs.get(k)+"\n");
				}
				sb1.append("***********************\n");	
				out.write(sb1.toString().getBytes("UTF-8"));
				strs = new ArrayList<String>();
			}
		}
		br.close();
		out.close();
	}
	/* 优化实验结果,过滤掉噪声很大的实体
	 */
	public static void filter2(int j) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(folder + type[j] + "_link_2.txt"),"UTF-8"));
		FileOutputStream out = new FileOutputStream(new File(folder + type[j] + "_link_3.txt"));
		String s;
		List<String> strs = new ArrayList<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(!s.equals("***********************")) {
				strs.add(s);
			}
			else {
				//过滤掉噪声很大的实体，提高精度
				if(strs.size()>10) {
					strs = new ArrayList<String>();
					continue;
				}
				StringBuffer sb1 = new StringBuffer();
				for(String l:strs) {
					sb1.append(l+"\n");
				}
				sb1.append("***********************\n");	
				out.write(sb1.toString().getBytes("UTF-8"));
				strs = new ArrayList<String>();
			}
		}
		br.close();
		out.close();
	}
	public static boolean judge(String s) throws IOException {
		//形如《...》的作品
		if(s.indexOf("《") != -1)
			return false;
		//影视文艺类型过滤
		for(String fs:filterDict) {
			if(s.indexOf(fs) != -1)
				return false;
		}
		return true;
	}
	public static void loadFilterDict(String filePath) throws IOException {
		filterDict = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
		String s;
		for(s = br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			filterDict.add(s);
		}
		br.close();
	}
}
