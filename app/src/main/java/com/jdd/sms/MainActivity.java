package com.jdd.sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
//    private SmsObserver smsObserver;
//    private final Handler smsHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//        }
//    };
    private SmsReceiver smsReceiver;
    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isReadSMS = ActivityCompat.checkSelfPermission(
                MainActivity.this,
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;
        boolean isRECEIVE_SMS = ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED;
        boolean isWRITE_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        boolean isMOUNT_UNMOUNT_FILESYSTEMS = ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED;
        if (isReadSMS || isRECEIVE_SMS || isWRITE_EXTERNAL_STORAGE || isMOUNT_UNMOUNT_FILESYSTEMS) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.RECEIVE_SMS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                    }, 1);
        }//动态申请权限
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
        initSmsReceiver();
        initSmsObserver();
//        if (NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
//
//            Intent serviceIntent = new Intent(this, SmsNotificationListenerService.class);
//
//            startService(serviceIntent);
//
//        } else {
//            // 去开启 监听通知权限
//            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//
//        }
//        smsObserver = new SmsObserver(getContentResolver(),smsHandler);
//        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
    }

    /**
     * 初始化短信监听广播
     */
    private void initSmsReceiver() {
        //生成广播处理
        smsReceiver = new SmsReceiver();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(SmsReceiver.SMS_RECEIVED_ACTION);
        //优先级最高
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        this.registerReceiver(smsReceiver, intentFilter);
//        smsReceiver.setOnReceivedMessageListener(new SmsReceiver.MessageListener() {
//            @Override
//            public void onReceived(String message) {
//
//            }
//        });
    }

    /**
     * 初始化短信监听数据库
     */
    private void initSmsObserver() {
        smsObserver = new SmsObserver(new Handler(), this, (ListView) findViewById(R.id.myListView));
        //注册短信内容监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);

    }
}