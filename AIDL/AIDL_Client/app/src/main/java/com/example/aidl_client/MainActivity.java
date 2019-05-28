package com.example.aidl_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aidl.test.IOnUserRaise;
import com.aidl.test.ITestAidlInterface;
import com.aidl.test.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_aidl)
    Button btnAidl;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_unregister)
    Button btnUnregister;
    @BindView(R.id.tv_users)
    TextView tvUsers;
    @BindView(R.id.tv_onraise_users)
    TextView tvOnraiseUsers;
    private ITestAidlInterface iTestAidlInterface;
    private final int MSG_WHAT_GETUSERS = 1024;
    private final int MSG_WHAT_USER_RAISE = 2048;
    private final int MSG_BINDER_DISCONNECT = 4096;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (iTestAidlInterface == null)
                return;
            iTestAidlInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iTestAidlInterface = null;
            // 这里重新绑定远程Service
            try {
                Thread.sleep(2000);
                handler.sendEmptyMessage(MSG_BINDER_DISCONNECT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_GETUSERS:
                    List<User> list = (List<User>) msg.obj;
                    tvUsers.setText("从服务端拿回的数据：" + list.get(0).getName());
                    break;
                case MSG_WHAT_USER_RAISE:
                    tvOnraiseUsers.setText(tvOnraiseUsers.getText().toString() + "\n" + "新增一人: " + ((User) msg.obj).getName());
                    break;
                case MSG_BINDER_DISCONNECT:
                    //断开后重连
//                    Toast.makeText(MainActivity.this, "DeathRecipient监听到断开连接", Toast.LENGTH_SHORT).show();
//                    iTestAidlInterface=null;
//                    startAIDL();
                    break;
                default:
                    super.handleMessage(msg);

            }
        }

    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iTestAidlInterface = ITestAidlInterface.Stub.asInterface(service);
//            try {
//                //监听Binder通信状态
//                iTestAidlInterface.asBinder().linkToDeath(mDeathRecipient,0);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                //解绑跨进程的监听接口
                iTestAidlInterface.unRegisterListener(iOnUserRaise);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "ServiceConnection监听到断开连接", Toast.LENGTH_SHORT).show();
            //断开后重连
            iTestAidlInterface=null;
            startAIDL();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //启动服务端连接
        startAIDL();
    }

    //链接服务端
    private void startAIDL() {
        Intent aidlIntent = new Intent();
        aidlIntent.setComponent(new ComponentName("com.example.aidl_serve", "com.example.aidl_serve.LoginService"));
        bindService(aidlIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private IOnUserRaise iOnUserRaise = new IOnUserRaise.Stub() {

        @Override
        public void onUserRaise(User user) throws RemoteException {
            Message message = Message.obtain();
            message.what = MSG_WHAT_USER_RAISE;
            message.obj = user;
            handler.sendMessage(message);
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iTestAidlInterface!=null&&iTestAidlInterface.asBinder().isBinderAlive()){
        try {
            //解绑跨进程的监听接口
            iTestAidlInterface.unRegisterListener(iOnUserRaise);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        }
        unbindService(serviceConnection);
    }

    @OnClick({R.id.btn_aidl, R.id.btn_register, R.id.btn_unregister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_aidl:
                if (iTestAidlInterface==null)return;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            iTestAidlInterface.addUser(new User(1, "老大"));
                            Message message = Message.obtain();
                            message.what = MSG_WHAT_GETUSERS;
                            message.obj = iTestAidlInterface.getUsers();
                            handler.sendMessage(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.btn_register:
                if (iTestAidlInterface==null)return;

                //注册跨进程接口监听
                try {
                    iTestAidlInterface.registerListener(iOnUserRaise);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister:
                if (iTestAidlInterface==null)return;
                //解跨进程接口监听
                try {
                    iTestAidlInterface.unRegisterListener(iOnUserRaise);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
