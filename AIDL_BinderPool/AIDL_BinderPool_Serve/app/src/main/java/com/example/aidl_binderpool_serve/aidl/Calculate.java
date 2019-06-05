package com.example.aidl_binderpool_serve.aidl;

import android.os.RemoteException;

import com.example.aidl_binderpool_client.ICalculateInterface;

public class Calculate extends ICalculateInterface.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
