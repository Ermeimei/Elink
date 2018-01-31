package cn.nju.ws.nlp;

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

public class Ner {
	public static void main(String[] args) throws IOException {
		String[] types = {"country","dev","nr","ns","nt","role"};
		for(int i =0;i<types.length;i++) {
			filter("ner/b_i_" + types[i] + "_2.txt","ner/b_i_" + types[i] + "_3.txt");
		}
	}
	public static void filter(String infile,String outfile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infile),"UTF-8"));
		String s;
		FileOutputStream out = new FileOutputStream(new File(outfile));
		for(s=br.readLine();s!=null;s=br.readLine()){
			s = s.trim();
			if(s.length() > 1) {
				out.write(s.getBytes("UTF-8"));
				out.write("\n".getBytes("UTF-8"));
			}else {
				System.out.println(s);
			}
		}
		br.close();
		out.close();
	}
	public static void concatenate(String infile,String outfile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(infile),"UTF-8"));
		String s;
		List<String> ls = new ArrayList<String>();
		int i = 0;
		StringBuffer sb;
		for(s=br.readLine();s!=null;){
			String[] splits = s.split("/");
			if(s.indexOf("/b_") != -1) {
				ls.add(splits[0]);
				i++;
				s = br.readLine();
				continue;
			}
			sb = new StringBuffer();
			sb.append(ls.get(i-1));
			if(s.indexOf("/i_") != -1) {
				sb.append(splits[0]);
				s = br.readLine();
				while(s != null && s.indexOf("/i_") != -1) {
					splits = s.split("/");
					sb.append(splits[0]);
					s = br.readLine();
				}
				ls.set(i-1,sb.toString());
			}
		}
		Set<String> lset = new HashSet<String>(ls);
		FileOutputStream out = new FileOutputStream(new File(outfile));
		for(String l:lset) {
			out.write(l.toString().getBytes("UTF-8"));
			out.write("\n".getBytes("UTF-8"));
		}
		br.close();
		out.close();
	}
	public static void getAllType() throws IOException {
		String path = "ner/";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path+"ner_result.txt"),"UTF-8"));
		FileOutputStream b_i_country = new FileOutputStream(new File(path+"b_i_country.txt"));
		FileOutputStream b_i_dev = new FileOutputStream(new File(path+"b_i_dev.txt"));
		FileOutputStream b_i_nr = new FileOutputStream(new File(path+"b_i_nr.txt"));
		FileOutputStream b_i_ns = new FileOutputStream(new File(path+"b_i_ns.txt"));
		FileOutputStream b_i_nt = new FileOutputStream(new File(path+"b_i_nt.txt"));
		FileOutputStream b_i_role = new FileOutputStream(new File(path+"b_i_role.txt"));
		FileOutputStream other = new FileOutputStream(new File(path+"other.txt"));
		String s;
		String[] strs ;
		for(s=br.readLine();s!=null;s=br.readLine()){
			strs = s.trim().split(" ");
			int i = 0;
			for(i=0;i<strs.length-1;i++) {
				String[] types = strs[i].split("/");
				String t = strs[i] + "\n";
				switch(types[types.length-1]) {
				case "b_country":case "i_country":
					b_i_country.write(t.getBytes("UTF-8")); break;
				case "b_dev":case "i_dev":
					b_i_dev.write(t.getBytes("UTF-8")); break;
				case "b_nr":case "i_nr":
					b_i_nr.write(t.getBytes("UTF-8")); break;
				case "b_ns":case "i_ns":
					b_i_ns.write(t.getBytes("UTF-8")); break;
				case "b_nt":case "i_nt":
					b_i_nt.write(t.getBytes("UTF-8")); break;
				case "b_role":case "i_role":
					b_i_role.write(t.getBytes("UTF-8")); break;
				case "other":
					other.write(t.getBytes("UTF-8")); break;
				}
			}
		}
		br.close();
		b_i_country.close();
		b_i_dev.close();
		b_i_nr.close();
		b_i_ns.close();
		b_i_nt.close();
		b_i_role.close();
		other.close();
	}
	public static void getAllNerType() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ner_result.txt"),"UTF-8"));
		String s;
		String[] strs ;
		Set<String> ner_type = new HashSet<String>();
		for(s=br.readLine();s!=null;s=br.readLine()){
			strs = s.trim().split(" ");
			int i = 0;
			for(i=0;i<strs.length-1;i++) {
				String[] types = strs[i].split("/");
			//	ner_type.add("/"+types[types.length-2] + "/" + types[types.length-1]);
				ner_type.add(types[types.length-1]);
			}
		}
		br.close();
		System.out.println(ner_type.size());
	//	FileOutputStream out = new FileOutputStream(new File("ner_type.txt"));
		FileOutputStream out = new FileOutputStream(new File("ner_type_last.txt"));
		StringBuffer sb;
		for(String t:ner_type) {
			sb = new StringBuffer();
			sb.append(t+"\n");
			out.write(sb.toString().getBytes("UTF-8"));
		}
		out.close();
	}
}
