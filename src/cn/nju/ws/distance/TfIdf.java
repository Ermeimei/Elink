package cn.nju.ws.distance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class TfIdf {
	private static List<String> fileList = new ArrayList<String>(); 
	/**
	 * 所有文件tf结果.key:文件名,value:该文件tf
	 */
	private static Map<String, Map<String, Double>> allTfMap = new HashMap<String, Map<String, Double>>();  
	
	/**
	 * 所有文件分词结果.key:文件名,value:该文件分词统计
	 */
    private static Map<String, Map<String, Integer>> allSegsMap = new HashMap<String, Map<String, Integer>>(); 
    
    /**
	 * 所有文件分词的idf结果.key:文件名,value:词w在整个文档集合中的逆向文档频率idf (Inverse Document Frequency)，即文档总数n与词w所出现文件数docs(w, D)比值的对数
	 */
    private static Map<String, Double> idfMap = new HashMap<String, Double>();  
    
    /**
     * 统计包含单词的文档数  key:单词  value:包含该词的文档数
     */
    private static Map<String, Integer> containWordOfAllDocNumberMap=new HashMap<String, Integer>();
    
    /**
     * 统计单词的TF-IDF
     * key:文件名 value:该文件tf-idf
     */
    private static Map<String, Map<String, Double>> tfIdfMap = new HashMap<String, Map<String, Double>>();  
    
	
	/**
	* @Description: 递归获取文件 
	 */
    private static List<String> readDirs(String filepath) throws FileNotFoundException, IOException {  
        try {  
            File file = new File(filepath);  
            if (!file.isDirectory()) {  
                System.out.println("输入的参数应该为[文件夹名]");  
                System.out.println("filepath: " + file.getAbsolutePath());  
            } else if (file.isDirectory()) {  
                String[] filelist = file.list();  
                for (int i = 0; i < filelist.length; i++) {  
                    File readfile = new File(filepath + File.separator + filelist[i]);  
                    if (!readfile.isDirectory()) {  
                        fileList.add(readfile.getAbsolutePath());  
                    } else if (readfile.isDirectory()) {  
                        readDirs(filepath + File.separator + filelist[i]);  
                    }  
                }  
            }  
  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();
        }  
        return fileList;  
    }
    
    /**
    * @Description: 读取文件转化成string
     */
    private static String readFile(String file) throws FileNotFoundException, IOException {  
        StringBuffer sb = new StringBuffer();  
        InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");  
        BufferedReader br = new BufferedReader(is);  
        String line = br.readLine();  
        while (line != null) {  
            sb.append(line).append("\r\n");  
            line = br.readLine();  
        }  
        br.close();  
        return sb.toString();  
    }  

    /**
    * @Description: 用ik进行字符串分词,统计各个词出现的次数
     */
    private static Map<String, Integer> segString(String content){
        // 分词
        Map<String, Integer> words = new HashMap<String, Integer>();
        JiebaSegmenter segmenter = new JiebaSegmenter();
        content = content.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
        List<SegToken> tokenList = segmenter.process(content, SegMode.SEARCH);
        for(int i=0;i<tokenList.size();i++) {
        	if(words.containsKey(tokenList.get(i).word)) {
        		words.put(tokenList.get(i).word, words.get(tokenList.get(i).word)+1);
        	}
        	else {
        		words.put(tokenList.get(i).word, 1);
        	}
        }
        return words;
    }
    
    /**
    * @Description: 返回LinkedHashMap的分词
     */
    public static Map<String, Integer> segStr(String content){
        // 分词
        Map<String, Integer> words = new LinkedHashMap<String, Integer>();
        JiebaSegmenter segmenter = new JiebaSegmenter();
        content = content.replaceAll("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
        List<SegToken> tokenList = segmenter.process(content, SegMode.SEARCH);
        for(int i=0;i<tokenList.size();i++) {
        	if(words.containsKey(tokenList.get(i).word)) {
        		words.put(tokenList.get(i).word, words.get(tokenList.get(i).word)+1);
        	}
        	else {
        		words.put(tokenList.get(i).word, 1);
        	}
        }
        return words;
    }
    
    public static Map<String, Integer> getMostFrequentWords(int num,Map<String, Integer> words){
        
        Map<String, Integer> keywords = new LinkedHashMap<String, Integer>();
        int count=0;
        // 词频统计
        List<Map.Entry<String, Integer>> info = new ArrayList<Map.Entry<String, Integer>>(words.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        
        // 高频词输出
        for (int j = 0; j < info.size(); j++) {
            // 词-->频
            if(info.get(j).getKey().length()>1){
                if(num>count){
                    keywords.put(info.get(j).getKey(), info.get(j).getValue());
                    count++;
                }else{
                    break;
                }
            }
        }
        return keywords;
    }
    
    /**
    * @Description: 分词结果转化为tf,公式为:tf(w,d) = count(w, d) / size(d)
    * 即词w在文档d中出现次数count(w, d)和文档d中总词数size(d)的比值
     */
    private static HashMap<String, Double> tf(Map<String, Integer> segWordsResult) { 
    	
        HashMap<String, Double> tf = new HashMap<String, Double>();// 正规化  
        if(segWordsResult==null || segWordsResult.size()==0){
    		return tf;
    	}
        Double size=Double.valueOf(segWordsResult.size());
        Set<String> keys=segWordsResult.keySet();
        for(String key: keys){
        	Integer value=segWordsResult.get(key);
        	tf.put(key, Double.valueOf(value)/size);
        }
        return tf;  
    }  
    
    /**
    * @Description: 得到所有文件的tf
     */
    public static Map<String, Map<String, Double>> allTf(String dir){
    	try{
    		fileList=readDirs(dir);
    		for(String filePath : fileList){
    			String content=readFile(filePath);
    			Map<String, Integer> segs=segString(content);
  			    allSegsMap.put(filePath, segs);
    			allTfMap.put(filePath, tf(segs));
    		}
    	}catch(FileNotFoundException ffe){
    		ffe.printStackTrace();
    	}catch(IOException io){
    		io.printStackTrace();
    	}
    	return allTfMap;
    }
    
    /**
    * @Description: 返回分词结果,以LinkedHashMap保存
     */
    public static Map<String, Map<String, Integer>> wordSegCount(String dir){
    	try{
    		fileList=readDirs(dir);
    		for(String filePath : fileList){
    			String content=readFile(filePath);
    			Map<String, Integer> segs=segStr(content);
  			    allSegsMap.put(filePath, segs);
    		}
    	}catch(FileNotFoundException ffe){
    		ffe.printStackTrace();
    	}catch(IOException io){
    		io.printStackTrace();
    	}
    	return allSegsMap;
    }
    
    
    /**
    * @Description: 统计包含单词的文档数  key:单词  value:包含该词的文档数
     */
    private static Map<String, Integer> containWordOfAllDocNumber(Map<String, Map<String, Integer>> allSegsMap){
    	if(allSegsMap==null || allSegsMap.size()==0){
    		return containWordOfAllDocNumberMap;
    	}
    	
    	Set<String> fileList=allSegsMap.keySet();
    	for(String filePath: fileList){
    		Map<String, Integer> fileSegs=allSegsMap.get(filePath);
    		//获取该文件分词为空或为0,进行下一个文件
    		if(fileSegs==null || fileSegs.size()==0){
    			continue;
    		}
    		//统计每个分词的idf
    		Set<String> segs=fileSegs.keySet();
    		for(String seg : segs){
    			if (containWordOfAllDocNumberMap.containsKey(seg)) {
    				containWordOfAllDocNumberMap.put(seg, containWordOfAllDocNumberMap.get(seg) + 1);
                } else {
                	containWordOfAllDocNumberMap.put(seg, 1);
                }
    		}
    		
    	}
    	return containWordOfAllDocNumberMap;
    }
    
    /**
    * @Description: idf = log(n / docs(w, D)) 
     */
    public static Map<String, Double> idf(Map<String, Map<String, Integer>> allSegsMap){
    	if(allSegsMap==null || allSegsMap.size()==0){
    		return idfMap;
    	}
    	containWordOfAllDocNumberMap=containWordOfAllDocNumber(allSegsMap);
    	Set<String> words=containWordOfAllDocNumberMap.keySet();
    	Double wordSize=Double.valueOf(containWordOfAllDocNumberMap.size());
    	for(String word: words){
    		Double number=Double.valueOf(containWordOfAllDocNumberMap.get(word));
    		idfMap.put(word, Math.log(wordSize/(number+1.0d)));
    	}
    	return idfMap;
    }
    
    /**
    * @Description: tf-idf
     */
    public static Map<String, Map<String, Double>> tfIdf(Map<String, Map<String, Double>> allTfMap,Map<String, Double> idf){
    	
    	Set<String> fileList=allTfMap.keySet();
     	for(String filePath : fileList){
    		Map<String, Double> tfMap=allTfMap.get(filePath);
    		Map<String, Double> docTfIdf=new HashMap<String,Double>();
    		Set<String> words=tfMap.keySet();
    		for(String word: words){
    			Double tfValue=Double.valueOf(tfMap.get(word));
        		Double idfValue=idf.get(word);
        		docTfIdf.put(word, tfValue*idfValue);
    		}
    		tfIdfMap.put(filePath, docTfIdf);
    	}
    	return tfIdfMap;
    }
    
    
    public static void main(String[] args){
    	
    	System.out.println("tf--------------------------------------");
    	Map<String, Map<String, Double>> allTfMap=TfIdf.allTf("d://dir");
    	Set<String> fileList=allTfMap.keySet();
      	for(String filePath : fileList){
     		Map<String, Double> tfMap=allTfMap.get(filePath);
     		Set<String> words=tfMap.keySet();
     		for(String word: words){
     			System.out.println("fileName:"+filePath+"     word:"+word+"      tf:"+tfMap.get(word));
     		}
     	}
      	
      	System.out.println("idf--------------------------------------");
    	Map<String, Double> idfMap=TfIdf.idf(allSegsMap);
    	Set<String> words=idfMap.keySet();
      	for(String word : words){
     		System.out.println("word:"+word+"     idf:"+idfMap.get(word));
     	}
    	
      	System.out.println("tf-idf--------------------------------------");
      	Map<String, Map<String, Double>> tfIdfMap=TfIdf.tfIdf(allTfMap, idfMap);
      	Set<String> files=tfIdfMap.keySet();
      	for(String filePath : files){
      		Map<String, Double> tfIdf=tfIdfMap.get(filePath);
    		Set<String> segs=tfIdf.keySet();
    		for(String word: segs){
    			System.out.println("fileName:"+filePath+"     word:"+word+"        tf-idf:"+tfIdf.get(word));
    		}
      	}
    }
}