package com.example.aidl_binderpool_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.aidl_binderpool_client.aidl.binder.BinderPool;
import com.example.aidl_binderpool_client.aidl.code.BinderCodeUtil;

public class MainActivity extends AppCompatActivity {
    private String TAG="cilent: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startAIDLConnect();
            }
        }).start();
    }

    private void startAIDLConnect() {
        Log.d(TAG,"获取BinderPool对象............");
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);     // 1
        Log.d(TAG,"获取speakBinder对象...........");
        IBinder speakBinder = binderPool.queryBinder(BinderCodeUtil.BINDER_SPEAK);  // 2
        Log.d(TAG,"获取speak的代理对象............");
        ISpeakInterface mSpeak =  ISpeakInterface.Stub.asInterface(speakBinder);    // 3
        try {
            mSpeak.speek();     // 4
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"获取calculateBinder对象...........");
        IBinder calculateBinder = binderPool.queryBinder(BinderCodeUtil.BINDER_CALCULATE);
        Log.d(TAG,"获取calculate的代理对象............");
        ICalculateInterface iCalculateInterface =  ICalculateInterface.Stub.asInterface(calculateBinder);
        try {
            Log.d(TAG,""+iCalculateInterface.add(5,6));
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
}
