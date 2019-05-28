package com.example.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {
    public final static int WHAT_MSG_FROM_CLIENT=1024;
    public final static int WHAT_MSG_FROM_SERVER=2048;
    private String tag="MessengerService：";
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case WHAT_MSG_FROM_CLIENT:
                Bundle bundle=msg.getData();
                Log.i(tag,bundle.getString("msg"));
                Messenger client=msg.replyTo;
                Message message=Message.obtain();
                message.what = MessengerService.WHAT_MSG_FROM_SERVER;
                Bundle data = new Bundle();
                data.putString("msg", "你好，我是Server");
                message.setData(data);
                try {
                    client.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.handleMessage(msg);
        }
    }
};
private Messenger messenger=new Messenger(handler);
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
