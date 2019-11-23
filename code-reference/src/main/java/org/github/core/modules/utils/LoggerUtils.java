package org.github.core.modules.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.MDC;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 章磊 on 2016/12/28.
 */
public class LoggerUtils {
    public static Pattern pattern = Pattern.compile("【([^】]+)】");
    public static void putKeyWord(String value){
        String oldValue = MDC.get("keyword");
        if(oldValue==null){
            MDC.put("keyword",value);
        }else{
            MDC.put("keyword",oldValue+","+value);
        }

    }
    public static void removeKeyWord(){
        MDC.remove("keyword");
    }
    public static List<String> searchPage(String inFile,String keyword,StringBuffer pos) throws IOException {
        BufferedRandomAccessFile bufferedReader = new BufferedRandomAccessFile(inFile,"r",1024);
        try {
            StringBuilder sb = new StringBuilder();
            List<String> result = new ArrayList<String>();
            bufferedReader.seek(NumberUtils.toLong(pos.toString()));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    pos.setLength(0);//经测试，这种清除性能最好
                    pos.append(bufferedReader.getFilePointer());
                    break;
                }
                line = new String(line.getBytes("ISO-8859-1"),"UTF-8");


                if(result.size()==40){
                    pos.setLength(0);
                    pos.append(bufferedReader.getFilePointer());
                    break;
                }
                if (line.startsWith("logstart")) {
                    if (sb.length() != 0) {
                        Matcher matcher = pattern.matcher(sb);
                        //					//System.out.print(content);
                        while(matcher.find()) {
                            String time = matcher.group(1);
                            if (time.indexOf(keyword) > -1) {
                                result.add(sb.toString());
                                break;
                            }
                        }
                        sb.setLength(0);
                    }
                }
                sb.append(line).append("\n");
            }
            return result;
        }finally {
            bufferedReader.close();
        }
    }
    public static List<String> search(String inFile,String keyword) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile),"UTF-8"));
        try {
            StringBuilder sb = new StringBuilder();
            List<String> result = new ArrayList<String>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("logstart")) {
                    if (sb.length() != 0) {
                        Matcher matcher = pattern.matcher(sb);
                        //					//System.out.print(content);
                        while(matcher.find()) {
                            String time = matcher.group(1);
                            if (time.indexOf(keyword) > -1) {
                                result.add(sb.toString());
                                break;
                            }
                        }
                        sb.delete(0,sb.length());
                    }
                }
                sb.append(line).append("\n");
            }
            return result;
        }finally {
            bufferedReader.close();
        }
    }
}
