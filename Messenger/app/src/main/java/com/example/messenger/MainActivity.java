package com.example.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_client)
    Button btnClient;
    private Messenger messenger;
    private String tag="MainActivity：";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessengerService.WHAT_MSG_FROM_SERVER:
                    Bundle bundle=msg.getData();
                    Log.i(tag,bundle.getString("msg"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private Messenger replyMessenger=new Messenger(handler);
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent messengerIntent = new Intent(this, MessengerService.class);
        bindService(messengerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
public void senMsgToServer(){
    Message message = Message.obtain();
    message.what = MessengerService.WHAT_MSG_FROM_CLIENT;
    Bundle data = new Bundle();
    data.putString("msg", "你好，我是Client");
    message.setData(data);
    message.replyTo=replyMessenger;
    try {
        messenger.send(message);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
}
    @OnClick(R.id.btn_client)
    public void onViewClicked() {
        if(messenger==null)return;
        senMsgToServer();
    }
}
