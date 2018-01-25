package cn.nju.ws.util;

import java.util.Comparator;
public class ComparatorByLen implements Comparator<String> { 
	@Override
	public int compare(String s1, String s2) {
		// TODO Auto-generated method stub
		int temp = s2.length()-s1.length();
		return temp==0? s1.compareTo(s2):temp;
	}
 }	
