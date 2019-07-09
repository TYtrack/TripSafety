package com.example.dell.tripsafety.Protect;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSMS;
import com.avos.avoscloud.AVSMSOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.dell.tripsafety.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ProtectActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_save)
    Button button_save;
    @BindView(R.id.btn_pin)
    Button button_pin;
    @BindView(R.id.text_pin_protect)
    EditText text_pin_protect;
    @BindView(R.id.text_bind_number)
    EditText text_bind_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE} , 0);

        ButterKnife.bind(this);
        button_pin.setOnClickListener(this);
        button_save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pin:
                submit_pin();
                break;
            case R.id.btn_save:
                verify();
                break;

        }
    }

    public void submit_pin(){
        final String number=text_bind_number.getText().toString();
        AVSMSOption option = new AVSMSOption();
        option.setApplicationName("TS平台");
        //option.setTemplateName("default_verify");  // 控制台预设的模板名称
        //option.setSignatureName("TS平台");       // 控制台预设的短信签名
        // 往 number 这个手机号码发送短信，使用预设的模板和签名
        AVSMS.requestSMSCodeInBackground(number, option, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    Toasty.success(ProtectActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
                    /* 请求成功 */
                } else {
                    Toasty.error(ProtectActivity.this,"发送失败", Toast.LENGTH_SHORT).show();

                    /* 请求失败 */
                }
            }
        });


    }

    public void verify(){
        final String number=text_bind_number.getText().toString();
        final String pin=text_pin_protect.getText().toString();
        //验证短信
        AVSMS.verifySMSCodeInBackground(pin, number, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    //当前用户设置监护人

                    AVObject pro_Object = new AVObject("Protect");
                    pro_Object.put("mobilePhoneNumber",AVUser.getCurrentUser().getMobilePhoneNumber());
                    pro_Object.put("protected_num",number);
                    pro_Object.saveInBackground();

                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            AVUser.getCurrentUser().put("protected_num",number);
                            AVUser.getCurrentUser().saveInBackground();
                        }
                    });

                    AVQuery<AVObject> proQuery=new AVQuery<>("Protect");
                    //查询被监护人
                    //AVQuery<AVUser> userQuery = new AVQuery<>("Protect");
                    proQuery.whereEqualTo("mobilePhoneNumber",number);
                    proQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (list==null||list.size()==0)
                            {

                                //新建用户
                                //Toasty.error(ProtectActivity.this,"没有该人",Toast.LENGTH_SHORT).show();

                                AVObject pro_Object2 = new AVObject("Protect");
                                pro_Object2.put("mobilePhoneNumber",number);
                                pro_Object2.put("protect_num",AVUser.getCurrentUser().getMobilePhoneNumber());
                                pro_Object2.saveInBackground();
                                return;
                            }
                            final AVObject avObj1=list.get(0);
                            avObj1.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    //设置被监护人的监护人手机号码
                                    avObj1.put("protect_num",AVUser.getCurrentUser().getMobilePhoneNumber());
                                    avObj1.saveInBackground();
                                }
                            });
                            Toasty.success(ProtectActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();

                        }
                    });


                    //Toasty.success(ProtectActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
                    //AVUser

                    finish();
                    /* 验证成功 */
                } else {
                    /* 验证失败 */
                }
            }
        });

    }



}
