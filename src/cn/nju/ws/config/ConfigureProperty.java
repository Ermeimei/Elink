package cn.nju.ws.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import cn.nju.ws.virtuoso.VirtGraphLoader;

public class ConfigureProperty {
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
        VirtGraphLoader.setUrl(VirtuosoServerUrl);
        VirtGraphLoader.setUser(VirtuosoServerUser);
        VirtGraphLoader.setPassword(VirtuosoServerPassword);
        init_flag = true;
	}
}
