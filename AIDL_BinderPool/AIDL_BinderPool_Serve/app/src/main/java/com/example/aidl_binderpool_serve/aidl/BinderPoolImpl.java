package com.example.aidl_binderpool_serve.aidl;

import android.os.IBinder;
import android.os.RemoteException;

import com.example.aidl_binderpool_client.IBinderPool;
import com.example.aidl_binderpool_serve.aidl.code.BinderCodeUtil;

public  class BinderPoolImpl extends IBinderPool.Stub {

    public BinderPoolImpl() {
        super();
    }

    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {
        IBinder binder = null;
        switch (binderCode) {
            case BinderCodeUtil.BINDER_SPEAK: {
                binder = new Speak();
                break;
            }
            case BinderCodeUtil.BINDER_CALCULATE: {
                binder = new Calculate();
                break;
            }
            default:
                break;
        }

        return binder;
    }
}