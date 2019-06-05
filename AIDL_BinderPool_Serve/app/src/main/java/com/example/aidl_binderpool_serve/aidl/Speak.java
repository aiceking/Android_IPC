package com.example.aidl_binderpool_serve.aidl;

import android.os.RemoteException;

import com.example.aidl_binderpool_client.ISpeakInterface;

public class Speak extends ISpeakInterface.Stub {
    @Override
    public void speek() throws RemoteException {

    }
}
