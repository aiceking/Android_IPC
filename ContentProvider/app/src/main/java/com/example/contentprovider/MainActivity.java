package com.example.contentprovider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_insert)
    Button btnInsert;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    private String TAG = "ContentResolver: ";
    private MyContentObserver myContentObserver;
    private Uri userUri = Uri.parse("content://com.example.contentprovider/user");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //注册观察者
        myContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(userUri, true, myContentObserver);
    }
    @OnClick({R.id.btn_insert, R.id.btn_update, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_insert:
                //测试insert
                ContentValues values = new ContentValues();
                values.put("_id", 3);
                values.put("name", "王五");
                getContentResolver().insert(userUri, values);
                break;
            case R.id.btn_update:
                ContentValues valuesUpdate = new ContentValues();
                valuesUpdate.put("_id", 3);
                valuesUpdate.put("name", "王六");
                getContentResolver().update(userUri, valuesUpdate,"_id=?", new String[]{"3"});
                break;
            case R.id.btn_delete:
                //测试delete
                getContentResolver().delete(userUri, "_id=?", new String[]{"3"});
                break;
        }
    }

    public class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Cursor userCursor = getContentResolver().query(uri, new String[]{"_id", "name"}, null, null, null);
            while (userCursor.moveToNext()) {
                User user = new User();
                user.setId(userCursor.getInt(0));
                user.setName(userCursor.getString(1));
                Log.d(TAG, "query user:" + user.toString());
            }
            userCursor.close();
        }
    }
}
