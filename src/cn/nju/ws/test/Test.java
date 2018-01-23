package cn.nju.ws.test;

import java.util.ArrayList;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

import junit.framework.TestCase;
import opencc.OpenCC;

public class Test extends TestCase{
    public static void testChineseConvert() {
        // use default conversion "s2t", convert from Simplified Chinese to Traditional Chinese
        OpenCC openCC = new OpenCC("tw2s");
        
        // can also set conversion when constructing
        // OpenCC openCC = new OpenCC("s2tw"); // convert from Simplified Chinese to Traditional Chinese (Taiwan Standard)
        
        // also can set a new conversion when needed
        // opencCC.setConversion("s2hk");
        
        String toConvert = "當轉換有兩個以上的字詞可能時";
        String converted = openCC.convert(toConvert);
        System.out.println(converted);
    }
    public static void testWordCut() {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String[] sentences =
            new String[] {"土耳其总统埃尔多安","马里政府"};
        for (String sentence : sentences) {
        	sentence = sentence.replaceAll("[\\pP‘’“”]", "");
            List<SegToken> tokenList = segmenter.process(sentence, SegMode.SEARCH);
            System.out.println(tokenList.toString());
            List<String> words = new ArrayList<String>();
            int size = tokenList.size();
            if(size == 0)
            	continue;
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
            System.out.println(words.toString());
        }
    }

}
