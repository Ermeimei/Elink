package cn.nju.ws.util;

import java.util.List;

public class ListUtil {
	public static String listToString(List<? extends Object> lists) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<lists.size();i++) {
			sb.append(lists.get(i)+"\n");
		}
		return sb.toString();
	}

}
