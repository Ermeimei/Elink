package cn.nju.ws.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import cn.nju.ws.data.MapDictionary;
import cn.nju.ws.virtuoso.VirtGraphLoader;

public class Configure {
	public static String VirtuosoServerUrl;
	public static String VirtuosoServerUser;
	public static String VirtuosoServerPassword;
	public static String BdBkVirtGraph;
	public static String BdBkPrefix;
	public static String GeonamesVirtGraph;
	public static String GeonamesPrefix;
	public static String T28VirtGraph;
	public static String T28Prefix;
	public static String WikidataVirtGraph;
	public static String WikidataPrefix;
	public static String[] BaiduLabelKeys;
	public static String LocDict;
	public static MapDictionary WordCutDict;
	public static MapDictionary NrDict;
	public static MapDictionary DevDict;
	public static MapDictionary CountryDict;
//	public static MapDictionary NtDict;
//	public static MapDictionary NsDict;
//	public static MapDictionary RoleDict;
	private static boolean init_flag = false;
	public static void init() throws IOException {
		if(init_flag)
			return;
        Properties pro = new Properties();
        InputStreamReader in = new InputStreamReader(new FileInputStream("config.properties"),"utf-8");
        pro.load(in);
        in.close();
        VirtuosoServerUrl = pro.getProperty("VirtuosoServerUrl");
        VirtuosoServerUser = pro.getProperty("VirtuosoServerUser");
        VirtuosoServerPassword = pro.getProperty("VirtuosoServerPassword");
        BdBkVirtGraph = pro.getProperty("BdBkVirtGraph");
        BdBkPrefix = pro.getProperty("BdBkPrefix");
        GeonamesVirtGraph = pro.getProperty("GeonamesVirtGraph");
        GeonamesPrefix = pro.getProperty("GeonamesPrefix");
        T28VirtGraph = pro.getProperty("T28VirtGraph");
        T28Prefix = pro.getProperty("T28Prefix");
        WikidataVirtGraph = pro.getProperty("WikidataVirtGraph");
        WikidataPrefix = pro.getProperty("WikidataPrefix");
        String label = pro.getProperty("LabelKeys");
        BaiduLabelKeys = label.split(",");
        LocDict = pro.getProperty("LocDict");
        WordCutDict = new MapDictionary(pro.getProperty("WordCutDict"));
        NrDict = new MapDictionary(pro.getProperty("NrDict"));
        DevDict = new MapDictionary(pro.getProperty("DevDict"));
        CountryDict = new MapDictionary(pro.getProperty("CountryDict"));
    //    NtDict = new MapDictionary(pro.getProperty("NtDict"));
   //     NsDict = new MapDictionary(pro.getProperty("NsDict"));
    //    RoleDict = new MapDictionary(pro.getProperty("RoleDict"));
        VirtGraphLoader.setUrl(VirtuosoServerUrl);
        VirtGraphLoader.setUser(VirtuosoServerUser);
        VirtGraphLoader.setPassword(VirtuosoServerPassword);
        init_flag = true;
	}
}
