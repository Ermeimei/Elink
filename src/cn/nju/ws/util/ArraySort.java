package cn.nju.ws.util;

import java.util.Arrays;
import java.util.Comparator;


public class ArraySort {
    Point[] arr;
    
    ArraySort(double[] temp,int n){
        arr = new Point[n];    //定义对象数组arr，并分配存储的空间
        for(int i = 0;i < n;i++) {
            arr[i] = new Point();
            arr[i].sim = temp[i];
            arr[i].index = i;
        }
    }
    
	public static void sort(double[] temp,int[] rs,int n) {
        ArraySort sort = new ArraySort(temp,n);
  
        Arrays.sort(sort.arr, new MyComprator<Point>());    //使用指定的排序器，进行排序
        for(int i=0;i<n;i++)    //输出排序结果
            rs[i] = sort.arr[i].index;
    }
}

class Point{
    double sim;
    int index;
}

//比较器，x坐标从大到小排序；x相同时，按照y从大到小排序
class MyComprator<T> implements Comparator<T> {
    public int compare(Object arg0, Object arg1) {
        Point t1=(Point)arg0;
        Point t2=(Point)arg1;
        if(t1.sim != t2.sim)
            return t1.sim<t2.sim? 1:-1;
        else
            return t1.index<t2.index? 1:-1;
    }
}
