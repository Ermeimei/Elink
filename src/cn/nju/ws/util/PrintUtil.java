package cn.nju.ws.util;

import java.util.List;
import java.util.Set;

public class PrintUtil {
	public static String listToString(List<? extends Object> lists) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<lists.size();i++) {
			sb.append(lists.get(i)+"\n");
		}
		return sb.toString();
	}
	public static String setToString(Set<String> lists) {
		StringBuffer sb = new StringBuffer();
		for(String s:lists) {
			sb.append(s +"\n");
		}
		return sb.toString();
	}
}
