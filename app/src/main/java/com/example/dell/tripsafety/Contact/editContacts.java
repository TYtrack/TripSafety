package com.example.dell.tripsafety.Contact;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.dell.tripsafety.AddUserActivity;
import com.example.dell.tripsafety.R;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class editContacts extends AppCompatActivity implements View.OnClickListener{

    private EditText name;
    private EditText number;
    private Button save;
    Integer num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_contacts);

        Intent intent=getIntent();
        num=intent.getIntExtra("num",0);

        Log.e("num",""+num);
        //隐藏标题栏
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        name=(EditText)findViewById(R.id.text_name);
        number=(EditText)findViewById(R.id.text_number);
        save =(Button)findViewById(R.id.btn_save);
        save.setOnClickListener(this);

        Button back=(Button)findViewById(R.id.edit_back);
        back.setOnClickListener(this);
        Button tong=(Button)findViewById(R.id.from_list_contact);
        tong.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.edit_back:
                    Intent intent = new Intent(editContacts.this, AddUserActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_save:
                    save_contact();
                case R.id.from_list_contact:
                    loadByConcats();


            }
    }


    public void save_contact(){
        final String name_x=name.getText().toString();
        final String number_x=number.getText().toString();

        AVQuery<AVObject> avQuery = new AVQuery<>("Contact");
        avQuery.whereEqualTo("user_number", AVUser.getCurrentUser().getMobilePhoneNumber());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                AVObject todo = new AVObject("Contact");
                if (list.size()==0){
                    todo = new AVObject("Contact");


                }else {
                    todo = list.get(0);
                }
                todo.put("Contact_"+num+"_nick",name_x);
                todo.put("Contact_"+num,number_x);
                todo.put("user_number",AVUser.getCurrentUser().getMobilePhoneNumber());
                // 保存到云端
                todo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Toasty.success(editContacts.this,"提交成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


    //从通讯录导入联系人
    public void loadByConcats()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS }, 1);
        } else
        {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, 0);

        }


    }

    //把返回的数据添加到数据库
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if(data==null) { return; }
            //处理返回的data,获取选择的联系人信息
            String[] contacts= new String[2];
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    Cursor cursor = getContentResolver()
                            .query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);

                    while (cursor.moveToNext()) {
                        final String number = cursor.getString(0);
                        final String name = cursor.getString(1);
                        AVQuery<AVObject> avQuery = new AVQuery<>("Contact");
                        avQuery.whereEqualTo("user_number", AVUser.getCurrentUser().getMobilePhoneNumber());
                        avQuery.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                AVObject todo = new AVObject("Contact");
                                if (list.size()==0){
                                    todo = new AVObject("Contact");


                                }else {
                                    todo = list.get(0);
                                }
                                todo.put("Contact_"+num+"_nick",name);
                                todo.put("Contact_"+num,number);
                                todo.put("user_number",AVUser.getCurrentUser().getMobilePhoneNumber());
                                // 保存到云端
                                todo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        Toasty.success(editContacts.this,"提交成功", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

                    }
                    cursor.close();

                }
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(editContacts.this,AddUserActivity.class);
            startActivity(intent);
            this.finish();
        }
        return false;
    }

}
