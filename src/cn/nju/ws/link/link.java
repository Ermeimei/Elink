package cn.nju.ws.link;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nju.ws.util.ListUtil;
import cn.nju.ws.util.WordCut;
import cn.nju.ws.virtuoso.QueryBaidu;
import cn.nju.ws.virtuoso.QueryT28;
import cn.nju.ws.virtuoso.QueryWiki;

public class link {
	public static void main(String[] args) throws IOException {
		QueryT28.init();
		System.out.println(eLink("http://28/event#sponsor12224","中国海军“哈尔滨”舰@zh"));
		/*Map<String,String> entities = SparqlQuery.queryT28Sponsor();
		FileOutputStream out = new FileOutputStream(new File("sponsor_link.txt"));
		for(Map.Entry<String,String> en:entities.entrySet()) {
			out.write(eLink(en.getKey(),en.getValue()).getBytes("UTF-8"));
			//System.out.println("***********************");
		}
		out.close();*/
		//queryWikiDataByName("中国");
		//queryBaiduBaikeByUri("http://baike.baidu.com/A911870");
	}
	public static String eLink(String e,String s) {
		    s = s.substring(0, s.length()-3);//去掉末尾的@zh和@en
			System.out.println(s);
			if(s.length() == 0) {
				System.out.println("sponsorName 为空");
				return "";
			}
			StringBuffer sb = new StringBuffer();
			sb.append(e + "\t" + s + "\n");
			List<String> words = WordCut.wordCut(s);//名称分词
			Set<String> alias = new HashSet<String>();
			for(String word:words) {
				alias.add(word);
			}
			QueryWiki.queryWikiDataByName(words,alias);//从wikidata中获取别名
			System.out.println(alias);
			Iterator<String> entry = alias.iterator();
			List<String> bdbkEntities = new ArrayList<String>();
			while(entry.hasNext()) {
				QueryBaidu.queryBaiduBaikeByName(entry.next(),bdbkEntities);
			}
			sb.append(ListUtil.listToString(bdbkEntities));
			sb.append("***********************\n");	
			return sb.toString();
	}
}
