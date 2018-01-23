package cn.nju.ws.util;

import java.util.ArrayList;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class WordCut {
	public static List<String> wordCut(String name) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        name = name.replaceAll("[\\pP‘’“”]", "");//去除标点符号
        List<SegToken> tokenList = segmenter.process(name, SegMode.SEARCH);
        List<String> words = new ArrayList<String>();
        words.add(name);
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
    //    System.out.println(words.toString());
		return words;
	}
}
