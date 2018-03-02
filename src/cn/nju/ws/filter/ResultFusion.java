package cn.nju.ws.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResultFusion {
	static String folder = "data/result5_1/";
	static String result = "result/result/";
	//static String result = "result/result6/";
	static String[] type = {"sponsor","bear","loc"};
	public static void main(String[] args) throws IOException {
		int i = 0;
	//	fusion(folder+type[i] + "_link_3.txt",i);
	//	combine(result+type[i],i);
		combine(result+"loc_supp_2/",2);
	}
	public static void combine(String filePath,int j) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(result + type[j]+"_supp_2_link.ttl"));
		File file = new File(filePath);  
		if (!file.isDirectory()) {  
			System.out.println("输入的参数应该为[文件夹名]");  
			System.out.println("filepath: " + file.getAbsolutePath());  
		} else if (file.isDirectory()) {  
			String[] filelist = file.list();  
			for (int i = 0; i < filelist.length; i++) {
		//		System.out.println(filelist[i]);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+"/"+ filelist[i]),"UTF-8"));
				String s;
				for(s=br.readLine();s!=null;s=br.readLine()){
					out.write((s+"\n").getBytes("UTF-8"));
				}
				br.close();
			}  
		}
		out.close();
	}
	//直接根据调试结果输出关联结果文件
	public static void fusion(String filePath,int j) throws IOException {
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
				String e = strs.get(0).split("\t")[0];
			//	System.out.println(e);
				FileOutputStream out = new FileOutputStream(new File(result + type[j]+"/" + e.substring(e.lastIndexOf("#")+1) + ".ttl"));
				StringBuffer sb1 = new StringBuffer();
				for(int k = 1;k<strs.size();k++) {
					String t = strs.get(k).split("\t")[0];
					sb1.append(("<" + e + "> <" + sameas + "> <" + t + "> .\n"));
				}
				out.write(sb1.toString().getBytes("UTF-8"));
				out.close();
				strs = new ArrayList<String>();
			}
		}
		br.close();
	}
}
