package cn.nju.ws.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import cn.nju.ws.data.MapDictionary;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class WordCut {
	private static int getLastIndex(String name) {
		MapDictionary.init();
	    List<Integer> index = new ArrayList<Integer>();
		MapDictionary.act.parseText(name, new AhoCorasickDoubleArrayTrie.IHit<String>()
	    {
	        @Override
	        public void hit(int begin, int end, String value)
	        {
	        	index.add(end);
	        //	System.out.printf("[%d:%d]=%s\n", begin, end, value);
	        }
	    });
		return (index.size()==0)? 0:Collections.max(index);
	}
	public static List<String> wordCut(String name) {
	//	System.out.println(name);
        List<String> words = new ArrayList<String>();
        name = name.replaceAll("[\\\\'\"]", "");
     //   String s = name.replaceAll("[\\pP‘’“”'\"]", "");
        words.add(name);
     //   System.out.println(words);
		//把name中引号中的字符串抽出来作为一个实体，如从“幻影2000”战斗机抽出幻影2000
		String pattern = "[\"'‘“](.+)[\"'’”]";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(name); 
		if(m.find()) {
			words.add(m.group(1));
		//	System.out.println(m.group(1));
		}
	//	System.out.println(words);
		//把name中包含横杠的字符串抽出来作为一个实体，如F-16战斗机抽出F-16
		pattern = "[a-zA-Z]+[-－][a-zA-Z0-9]+";
		p = Pattern.compile(pattern);
		m = p.matcher(name); 
		if(m.find()) {
			words.add(m.group(0));
		//	System.out.println(m.group(0));
		}
	//	System.out.println(words);
        //人名的分词处理,正确切分人名
		name = name.replaceAll("[\\pP‘’“”'\"]", "");//去除标点符号
        String s = name.substring(getLastIndex(name));
        if(s.length()>1)
        	words.add(s);
   //     System.out.println(words);
        JiebaSegmenter segmenter = new JiebaSegmenter();
     //   name = name.replaceAll("[‘’“”'\"]", "");//去除标点符号
        name = name.replaceAll("[\\pP+~$`^=\\|<>～｀＄＾＋＝｜＜＞￥×\"]" , "");
        List<SegToken> tokenList = segmenter.process(name, SegMode.SEARCH);
        int size = tokenList.size();
        if(size == 1) {
        	words.add(tokenList.get(0).word);
        	return words;
        }
        for(int i = 0; i < size;) {
        	String word = tokenList.get(i).word;
        	int words_size = words.size();
        	if(word.length()==1) {
        		int j = i+1;
        		for(j=i+1; j<size;j++){
        			if(tokenList.get(j).word.length() == 1)
        				break;
        		}
        		if(j == size) {
        			if(i==0) {
        				word = word + tokenList.get(i+1).word;
        				words.add(word);
        				i++;
        			}
        			else if(i==size-1) {
        				word = words.get(words_size-1) + word;
        				words.set(words_size-1, word);
        			}
        			else {
        				word = words.get(words_size-1) + word;
        				words.set(words_size-1, word);
        			}
        			i++;
        		}
        		else {
        			StringBuffer sb = new StringBuffer();
        			for(int k=i;k<=j;k++) {
        				sb.append(tokenList.get(k).word);
        			}
        			words.add(sb.toString());
        			i = j+1;
        		}
        	}
        	else {
        		words.add(word);
        		i++;
        	}
        }
      //  System.out.println(words.toString());
		return words;
	}
}
