package cn.nju.ws.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

import cn.nju.ws.config.ConfigureProperty;
import cn.nju.ws.virtuoso.VirtGraphLoader;
import junit.framework.TestCase;
import opencc.OpenCC;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class Test extends TestCase{
	public static void test() throws IOException{
		ConfigureProperty.init();
		String url = VirtGraphLoader.getUrl(); 
		String usr = VirtGraphLoader.getUser();
		String psd = VirtGraphLoader.getPassword();
		VirtGraph vg = new VirtGraph ("http://sws.geonames.org",url, usr, psd);
		String query = "select * where { <http://sws.geonames.org/111421/> ?p ?o.}limit 10";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query,vg);
		ResultSet results = vqe.execSelect();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode p = result.get("p");
			RDFNode o = result.get("o");
			System.out.println("test========"+p.toString()+o.toString());
		}	
	}
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
            new String[] {"中国贸促会","菲律宾总统杜特尔特致","西藏林芝米林县,西藏自治区,中国"};
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
