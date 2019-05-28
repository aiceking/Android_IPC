package com.example.aidl_serve;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.aidl.test.IOnUserRaise;
import com.aidl.test.ITestAidlInterface;
import com.aidl.test.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginService extends Service {
    private AtomicBoolean raiseUserStart = new AtomicBoolean(false);
    private final int MSG_WHAT_GETUSERS = 1024;
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_GETUSERS:
                Toast.makeText(getApplicationContext(), "客户端发来的user：" + ((User)msg.obj).getName(), Toast.LENGTH_SHORT).show();
                break;
            default:
                super.handleMessage(msg);

        }
        super.handleMessage(msg);
    }
};

    //因为跨进程的IOnUserRaise对象不是同一个，但是中间的Binder是同一个，所以可以实现解绑
    private RemoteCallbackList<IOnUserRaise> remoteCallbackList=new RemoteCallbackList<>();
    private List<User> userList=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        raiseUser();
    }

    //模拟不断的增加User
    private void raiseUser() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!raiseUserStart.get()){
                    try {
                        //模拟耗时操作
                        Thread.sleep(3000);
                        userList.add(new User(1,"name 1"));
                        int n=remoteCallbackList.beginBroadcast();
                        for (int i=0;i<n;i++){
                            remoteCallbackList.getBroadcastItem(i).onUserRaise(new User(userList.size()*10,"name "+(userList.size()*10)));
                        }
                        remoteCallbackList.finishBroadcast();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        raiseUserStart.set(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ITestAidlInterface.Stub() {
            @Override
            public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
                if (packages != null && packages.length > 0) {
                    if (!packages[0].startsWith("com.example")){
                     return false;
                    }
                }
                return super.onTransact(code, data, reply, flags);
            }

            @Override
            public List<User> getUsers() throws RemoteException {
                List<User> list=new ArrayList<>();
                list.add(new User(2,"张三"));
                try {
                    //模拟耗时操作
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return list;
            }

            @Override
            public void addUser(User user) throws RemoteException {
                try {
                    //模拟耗时操作
                    Thread.sleep(3000);
                    Message message=Message.obtain();
                    message.what=MSG_WHAT_GETUSERS;
                    message.obj=user;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void registerListener(IOnUserRaise listener) throws RemoteException {
                remoteCallbackList.register(listener);

            }

            @Override
            public void unRegisterListener(IOnUserRaise listener) throws RemoteException {
                remoteCallbackList.unregister(listener);


            }
        };
    }
}
