package cn.nju.ws.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import cn.nju.ws.util.FileUtil;
import junit.framework.TestCase;

public class TestFileUtil extends TestCase{
	public static void test() throws FileNotFoundException, IOException {
		System.out.println(FileUtil.readDirs("data/bear/"));
	}
}
