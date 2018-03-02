package cn.nju.ws.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrintUtil {
	
	public static String setToString(Set<String> lists) {
		StringBuffer sb = new StringBuffer();
		for(String s:lists) {
			sb.append(s +"\n");
		}
		return sb.toString();
	}
	public static String listToString(List<String> list,String delimeter) {
		StringBuffer sb = new StringBuffer();
		for(String s:list) {
			sb.append(s+delimeter);
		}
		return sb.toString();
	}
	public static String mapToString(Map<String,List<String>> predicateObject) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = predicateObject.keySet().iterator();
	 	while (iter.hasNext()) {
			String pred = iter.next();
			sb.append(pred + ":\t");
			List<String> objList = predicateObject.get(pred);
			for(int i = 0; i < objList.size(); i++) {
				String obj = objList.get(i);
				sb.append(obj+"\n");
				sb.append("-----------------------------------------------\n");
			}
		}
		return sb.toString();
	}
}
