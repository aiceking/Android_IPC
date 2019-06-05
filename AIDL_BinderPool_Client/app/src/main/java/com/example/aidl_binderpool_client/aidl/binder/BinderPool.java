package com.example.aidl_binderpool_client.aidl.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.example.aidl_binderpool_client.IBinderPool;
import com.example.aidl_binderpool_client.MainActivity;

import java.util.concurrent.CountDownLatch;

public class BinderPool {
    private String TAG="BinderPool: ";
    private Context context;
    private static volatile BinderPool binderPool;
    private IBinderPool iBinderPool;
    private CountDownLatch mConnectBinderPoolCountDownLatch;
    private BinderPool(Context context) {
        this.context=context.getApplicationContext();
        connectBinderPoolService();
    }
    public static BinderPool getInstance(Context context) {     // 2
        if (binderPool == null) {
            synchronized (BinderPool.class) {
                if (binderPool == null) {
                    binderPool = new BinderPool(context);
                }
            }
        }
        return binderPool;
    }
    private  synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent();
        service.setComponent(new ComponentName("com.example.aidl_binderpool_serve", "com.example.aidl_binderpool_serve.service.BinderPoolService"));
        context.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public IBinder queryBinder(int binderCode) {          // 4
        IBinder binder = null;
        try {
            if (iBinderPool != null) {
                binder = iBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {       // 5

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG,"链接断开");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
             Log.v(TAG,"链接成功");
            iBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                iBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {    // 6
        @Override
        public void binderDied() {
            iBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            iBinderPool = null;
            connectBinderPoolService();
        }
    };
}
