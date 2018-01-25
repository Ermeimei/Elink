package cn.nju.ws.distance;

import com.wcohen.ss.Levenstein;

public class Distance {
	public static double Levensteindistance(String x,String y) {
		return new Levenstein().score(x, y);
	}
	public static double JensenShannondistance(double[] x, double[] y) {
        if (x.length != y.length)
            throw new IllegalArgumentException(String.format("Arrays have different length: x[%d], y[%d]", x.length, y.length));

        return Math.sqrt(JensenShannonDivergence(x, y));
    }
	private static double JensenShannonDivergence(double[] x, double[] y) {
        double[] m = new double[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = (x[i] + y[i]) / 2;
        }

        return (KullbackLeiblerDivergence(x, m) + KullbackLeiblerDivergence(y, m)) / 2;
    }
	private static double KullbackLeiblerDivergence(double[] x, double[] y) {
        boolean intersection = false;
        double kl = 0.0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0.0 && y[i] != 0.0) {
                intersection = true;
                kl += x[i] * Math.log(x[i] / y[i]);
            }
        }

        if (intersection) {
            return kl;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }
}
