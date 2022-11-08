package com.jdd.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    //设置静态常量TAG，android中有5种级别的log：Log.v(),Log.d(),Log.i(),Log.w(),Log.e(),i(info)输出提示信息；
    protected static final String TAG = "SmsReceiverLog";
    private static MessageListener mMessageListener;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";//只要注册声明权限即可收到、阻断
    private String separator = "@";


    @Override
    public void onReceive(Context context, Intent intent) {
        String str = "验证码";
        Log.i(TAG,intent.getAction());
        if(intent.getAction().equals(SMS_RECEIVED_ACTION)){
            //intent.getExtras()方法就是从过滤后的意图中获取携带的数据，
            // 这里携带的是以“pdus”为key、短信内容为value的键值对
            // android设备接收到的SMS是pdu形式的
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if (null != bundle){
                //生成一个数组，将短信内容赋值进去
                Object[] smsObg = (Object[]) bundle.get("pdus");
                //遍历pdus数组，将每一次访问得到的数据方法object中
                for (Object object:smsObg){
                    //获取短信
                    msg = SmsMessage.createFromPdu((byte[])object);
                    //获取短信内容
                    String content = msg.getDisplayMessageBody();
//                    //获取短信发送方地址
//                    String from = msg.getOriginatingAddress();
//                    Log.i(TAG,from);
                    long timestampMillis = msg.getTimestampMillis();
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampMillis);
                    Log.i(TAG, date);
                    Toast.makeText(context, content, Toast.LENGTH_LONG).show();
                    Log.i(TAG,content);
                    //调用written方法将6位数字验证码写入code.txt文件
//                    String data = new SimpleDateFormat("yyyyMMdd").format(new Date());
//                    CommonUtils.written("SmsReceiver.txt",date+separator+content);
                    String pattern = "\\d{6}";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(content);
                    if (m.find()){
                        CommonUtils.written("sms.txt", date + separator + m.group(0));
                    }

                }
            }
        }

    }

//    //将内容写入文件
//    public void written(String f,String content){
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        try{
//            // Environment.getExternalStorageDirectory()，获取sd卡的内存位置；
//            // 当没有sd卡时，获取的是手机的内存，即手机插上电脑后，显示的磁盘根目录；
//            File file = new File(Environment.getExternalStorageDirectory(),f);
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            br = new BufferedReader(new FileReader(file));
//            String s;
//            StringBuffer sb = new StringBuffer();
//            while ((s=br.readLine())!=null){
//                sb.append(s+System.getProperty("line.separator","\n"));
//            }
//            bw = new BufferedWriter(new FileWriter(file));
//            //将数据写入缓冲区，并没有写入目的文件
//            bw.write(content);
//            bw.newLine();
//            bw.write(sb.toString());
//            //刷新缓冲流，也就是会把数据写入到目的文件里
//            bw.flush();
//            Log.i(TAG,"写入成功");
////            mMessageListener.onReceived(content);
////            abortBroadcast();//中断广播的继续传递,防止优先级低的获取到
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if (br!=null){
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (bw!=null){
//                try {
//                    bw.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    //回调接口
    public interface MessageListener {
        public void onReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }
}
