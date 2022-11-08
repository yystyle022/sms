package com.jdd.sms;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CommonUtils {
    protected static final String TAG = "CommonUtilsLog";
    //将内容写入文件11
    public static void written(String f,String content){
        BufferedReader br = null;
        BufferedWriter bw = null;
        try{
            // Environment.getExternalStorageDirectory()，获取sd卡的内存位置；
            // 当没有sd卡时，获取的是手机的内存，即手机插上电脑后，显示的磁盘根目录；
            File file = new File(Environment.getExternalStorageDirectory(),f);
            if (!file.exists()){
                file.createNewFile();
            }
            br = new BufferedReader(new FileReader(file));
            String s;
            int flag = 0;
            StringBuffer sb = new StringBuffer();
            while ((s=br.readLine())!=null){
                sb.append(s+System.getProperty("line.separator","\n"));
                if (flag==3){
                    break;
                }
                flag++;
            }
            bw = new BufferedWriter(new FileWriter(file));
            //将数据写入缓冲区，并没有写入目的文件
            bw.write(content);
            bw.newLine();
            bw.write(sb.toString());
            //刷新缓冲流，也就是会把数据写入到目的文件里
            bw.flush();
            Log.i(TAG,"写入成功");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
