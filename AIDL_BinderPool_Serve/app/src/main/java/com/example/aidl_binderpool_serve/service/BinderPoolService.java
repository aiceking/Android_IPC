package com.example.aidl_binderpool_serve.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.aidl_binderpool_serve.aidl.BinderPoolImpl;

public class BinderPoolService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("BinderPoolService: ","onBind");
        return new BinderPoolImpl();
    }
}
