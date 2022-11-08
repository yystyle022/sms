package com.jdd.sms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsObserver extends ContentObserver {
    protected static final String TAG = "SmsObserverLog";
    // 只检查收件箱1
    public static final Uri MMSSMS_ALL_MESSAGE_URI = Uri.parse("content://sms/");
    public static final String SORT_FIELD_STRING = "_id asc";  // 排序
    public static final String DB_FIELD_ID = "_id";
    public static final String DB_FIELD_ADDRESS = "address";
    public static final String DB_FIELD_PERSON = "person";
    public static final String DB_FIELD_BODY = "body";
    public static final String DB_FIELD_DATE = "date";
    public static final String DB_FIELD_TYPE = "type";
    public static final String DB_FIELD_THREAD_ID = "thread_id";
    public static final String[] ALL_DB_FIELD_NAME = {
            DB_FIELD_ID, DB_FIELD_ADDRESS, DB_FIELD_PERSON, DB_FIELD_BODY,
            DB_FIELD_DATE, DB_FIELD_TYPE, DB_FIELD_THREAD_ID};
    public static int mMessageCount = -1;
    private Activity myActivity;
    private ListView mylistView;
    private String separator = "@";
    private ArrayList<String> listValue = new ArrayList<>();

    public SmsObserver(Handler handler, Activity activity, ListView listView) {
        super(handler);
        this.myActivity = activity;
        this.mylistView = listView;
        try {
            mMessageCount = myActivity.getContentResolver().query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                    null, null, SORT_FIELD_STRING).getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        onReceiveSms();
        //读取短信操作
    }

    private void onReceiveSms() {
        Cursor cursor = null;
        // 添加异常捕捉
        try {
            cursor = myActivity.getContentResolver().query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                    null, null, SORT_FIELD_STRING);
            final int count = cursor.getCount();
            if (count <= mMessageCount) {
                mMessageCount = count;
                return;
            }
            // 发现收件箱的短信总数目比之前大就认为是刚接收到新短信---如果出现意外，请神保佑
            // 同时认为id最大的那条记录为刚刚新加入的短信的id---这个大多数是这样的，发现不一样的情况的时候可能也要求神保佑了
            mMessageCount = count;
            if (cursor != null) {
                cursor.moveToLast();
                @SuppressLint("Range") final long smsdate = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)));
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(smsdate);
                Log.i(TAG, date);
                @SuppressLint("Range") final String strbody = cursor.getString(cursor.getColumnIndex(DB_FIELD_BODY));          // 在这里获取短信信息
                Log.i(TAG, strbody);
//                String data = new SimpleDateFormat("yyyyMMdd").format(new Date());
//                CommonUtils.written("SmsObserver" + data + ".txt", date + separator + strbody);
                String pattern = "\\d{6}";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(strbody);
                if (m.find()){
//                    CommonUtils.written("SmsObserver.txt", date + separator + m.group(0));
                    if (listValue.size() == 5) {
                        listValue.remove(listValue.size() - 1);
                    }
                    listValue.add(0, date + separator + m.group(0));
                    ArrayAdapter myAdapter = new ArrayAdapter(myActivity, R.layout.default_item, listValue);
                    mylistView.setAdapter(myAdapter);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {  // 有可能cursor都没有创建成功
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
