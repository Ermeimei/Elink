package cn.nju.ws.test;

import cn.nju.ws.util.WordCut;
import junit.framework.TestCase;
import opencc.OpenCC;

public class Test extends TestCase{
	
    public static void testChineseConvert() {
        OpenCC openCC = new OpenCC("tw2s");
        String toConvert = "當轉換有兩個以上的字詞可能時";
        String converted = openCC.convert(toConvert);
        System.out.println(converted);
    }
    public static void testWordCut() {
    	System.out.println(WordCut.wordCut("卡尔·文森\\\"\\\"号"));
     System.out.println(WordCut.wordCut("“爱国者”PAC-3防空反导系统"));
       System.out.println(WordCut.wordCut("“幻影2000”战斗机"));
       System.out.println(WordCut.wordCut("太平洋-2014”演习舰艇"));
       System.out.println(WordCut.wordCut("伊朗内政部长阿卜杜勒雷扎·拉赫曼尼·夫斯利"));
       System.out.println(WordCut.wordCut("俄罗斯太平洋舰队旗舰“瓦良格”号导弹巡洋舰"));
       System.out.println(WordCut.wordCut("F-16战斗机"));
       System.out.println(WordCut.wordCut("“北极星-2”型导弹"));
       System.out.println(WordCut.wordCut("美国核动力航母“卡尔，文森”号"));
       System.out.println(WordCut.wordCut("“库兹涅佐夫”号航母编队"));
       System.out.println(WordCut.wordCut("F-15K战斗轰炸机"));
       System.out.println(WordCut.wordCut("航空母舰CVN78“福特”号"));
       System.out.println(WordCut.wordCut("日本日本海上自卫队“宙斯盾”驱逐舰"));
      
    }

}
