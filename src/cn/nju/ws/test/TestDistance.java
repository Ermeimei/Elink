package cn.nju.ws.test;

import cn.nju.ws.distance.Distance;
import junit.framework.TestCase;

public class TestDistance extends TestCase{
	public static void test() {
		System.out.println(Distance.Levensteindistance("邓小平同志","邓小平同志论民主与法制"));
		System.out.println(Distance.Levensteindistance("邓小平同志","邓小平"));
	}
}
