package cn.nju.ws.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException {  
        List<String> fileList = new ArrayList<String>();
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
}
