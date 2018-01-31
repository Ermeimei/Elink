package cn.nju.ws.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.nju.ws.util.ComparatorByLen;

public class NerFilter {
	public static void ner(String name,Set<String> nerResult,List<String> results) {
		List<String> b_i_country = new ArrayList<String>();
		List<String> b_i_dev = new ArrayList<String>();
		List<String> b_i_nr = new ArrayList<String>();
	//	List<String> b_i_ns = new ArrayList<String>();
	//	List<String> b_i_nt = new ArrayList<String>();
	//	List<String> b_i_role = new ArrayList<String>();
		String[] strs ;
		for(String s:nerResult){
			strs = s.trim().split(" ");
			int i = 0;
			for(i=0;i<strs.length-1;i++) {
				String[] types = strs[i].split("/");
				String t = strs[i];
				switch(types[types.length-1]) {
				case "b_country":case "i_country":
					b_i_country.add(t); break;
				case "b_dev":case "i_dev":
					b_i_dev.add(t); break;
				case "b_nr":case "i_nr":
					b_i_nr.add(t); break;
	//			case "b_ns":case "i_ns":
		//			b_i_ns.add(t); break;
		//		case "b_nt":case "i_nt":
		//			b_i_nt.add(t); break;
		//		case "b_role":case "i_role":
		//			b_i_role.add(t); break;
		//		case "other":
				default:
					break;
				}
			}
		}
	/*	System.out.println("b_i_nr:" + ListUtil.listToString(b_i_nr));
		System.out.println("b_i_dev:" +ListUtil.listToString(b_i_dev));
		System.out.println("b_i_nt:" +ListUtil.listToString(b_i_nt));
		System.out.println("b_i_country:" +ListUtil.listToString(b_i_country));
		System.out.println("b_i_ns:" +ListUtil.listToString(b_i_ns));
		System.out.println("b_i_role:" +ListUtil.listToString(b_i_role));*/
	//	System.out.println("******");
		concatenateAndFilter(b_i_nr,results);
	//	System.out.println("b_i_nr:" + ListUtil.listToString(results));
		concatenateAndFilter(b_i_dev,results);
	//	System.out.println("b_i_dev:" +ListUtil.listToString(results));
	//	concatenateAndFilter(b_i_nt,results);
	//	System.out.println("b_i_nt:" +ListUtil.listToString(results));
		concatenateAndFilter(b_i_country,results);
	//	System.out.println("b_i_country:" +ListUtil.listToString(results));
	//	concatenateAndFilter(b_i_ns,results);
	//	System.out.println("b_i_ns:" +ListUtil.listToString(results));
	//	concatenateAndFilter(b_i_role,results);
	//	System.out.println("b_i_role:" +ListUtil.listToString(results));
	}
	public static void concatenateAndFilter(List<String> list,List<String> results) {
		if(list == null || list.size() == 0)
			return;
		List<String> ls = new ArrayList<String>();
		int i = 0;
		StringBuffer sb;
		int j = 0;
		String s = list.get(j);
		int size = list.size();
		String[] splits;
	//	System.out.println(size);
		for(;j<size;){
	//		System.out.println(j);
			splits = s.split("/");
			if(s.indexOf("/b_") != -1) {
				ls.add(splits[0]);
		//		System.out.println("/b_"+splits[0]);
				i++;
				j++;
				s = list.get(Math.min(j,size-1));
				continue;
			}
			sb = new StringBuffer();
			sb.append(ls.get(i-1));
			if(s.indexOf("/i_") != -1) {
				sb.append(splits[0]);
	//			System.out.println("/i_"+splits[0]);
				j++;
				s = list.get(Math.min(j,size-1));
				while(j < size && s.indexOf("/i_") != -1) {
					splits = s.split("/");
					sb.append(splits[0]);
					j++;
					s = list.get(Math.min(j,size-1));
				}
				ls.set(i-1,sb.toString());
		//		System.out.println("/i_"+sb.toString());
			}
		}
	//	System.out.println(ListUtil.listToString(ls));
		Set<String> lset = new TreeSet<String>(new ComparatorByLen());
	//	Set<String> lset = new HashSet<String>(ls);
		for(String l:ls) {
			if(l.length()<2)
				continue;
			lset.add(l);
		}
		for(String l:lset) {
			results.add(l);
		}
	}
}
