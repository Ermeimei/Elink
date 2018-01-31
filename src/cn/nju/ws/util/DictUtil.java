package cn.nju.ws.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;

import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import cn.nju.ws.config.Configure;
import cn.nju.ws.data.MapDictionary;

public class DictUtil {
	public static void main(String[] args) throws IOException {
		Configure.init();
		List<Integer> index = null;
		String s = "俄罗斯克拉斯诺亚尔斯克安全局边防管理局";
		for(int i = 1; i < 4;i++) {
			System.out.println(i);
			index = DictUtil.getLastIndex(s,i);
			if(index == null || index.size() == 0)
				continue;
			else {
				s = s.substring(index.get(0),index.get(1));
				System.out.println(s);
			}
		}
	}
	public static List<Integer> getLastIndex(String name,int dictIndex) {
	    List<Integer> b = new ArrayList<Integer>();
	    List<Integer> e = new ArrayList<Integer>();
	    List<Integer> len = new ArrayList<Integer>();
	    MapDictionary dict;
	    switch(dictIndex) {
	    case 0:
	    	dict = Configure.WordCutDict;break;
	    case 1:
	    	dict = Configure.NrDict;break;
	    case 2:
	    	dict = Configure.DevDict;break;
	    case 3:
	    	dict = Configure.CountryDict;break;
	/*    case 4:
	    	dict = Configure.NtDict;break;
	    case 5:
	    	dict = Configure.NsDict;break;
	    case 6:
	    	dict = Configure.RoleDict;break;*/
	    default:
	    	dict = Configure.WordCutDict;
	    }
	    dict.act.parseText(name, new AhoCorasickDoubleArrayTrie.IHit<String>()
	    {
	        @Override
	        public void hit(int begin, int end, String value)
	        {
	        	b.add(begin);
	        	e.add(end);
	        	len.add(end-begin);
	        	System.out.printf("[%d:%d]=%s\n", begin, end, value);
	        }
	    });
	    List<Integer> rs = new ArrayList<Integer>();
	    if(len.size() != 0) {
	    	DenseIntMatrix1D t = new DenseIntMatrix1D(len.size());
	    	for(int i=0;i<len.size();i++) {
	    		t.setQuick(i, len.get(i));
	    	}
	    	int [] index = t.getMaxLocation();
	    	rs.add(b.get(index[1]));
	    	rs.add(e.get(index[1]));
	    }
		return rs;
	}
}
