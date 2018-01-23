package cn.nju.ws.util;

import opencc.OpenCC;

public class ChineseConvert {
	public static String convert(String s) {
		
        // use default conversion "s2t", convert from Simplified Chinese to Traditional Chinese
        OpenCC openCC = new OpenCC("tw2s");
        
        // can also set conversion when constructing
        // convert from Simplified Chinese to Traditional Chinese (Taiwan Standard)
        // OpenCC openCC = new OpenCC("s2tw"); 
        
        // also can set a new conversion when needed
        // opencCC.setConversion("s2hk");
        
       return openCC.convert(s);
	}
}
