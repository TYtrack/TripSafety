package com.example.dell.tripsafety.user_profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.OptionsPickerView;

import com.bigkoo.pickerview.TimePickerView;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.login.LoginActivity;
import com.example.dell.tripsafety.user_profile.constant.UserConstant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = UserProfileSettingActivity.class.getSimpleName();

    // constant
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;



    // view
    private CircleImageView userHead;
    private RelativeLayout nickLayout;
    private RelativeLayout genderLayout;
    private RelativeLayout birthLayout;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout signatureLayout;

    private TextView nickText;
    private TextView genderText;
    private TextView birthText;
    private TextView phoneText;
    private TextView emailText;
    private TextView signatureText;

    // data
    //AbortableFuture<String> uploadAvatarFuture;
    private AVUser avUser;
    //private NimUserInfo userInfo;

    private Button submit_Button;
    private ArrayList<String> genderList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_set_activity);


        avUser=AVUser.getCurrentUser();

        findViews();
        updateUI();

    }



    private void findViews() {
        userHead = findViewById(R.id.user_head);
        nickLayout = findViewById(R.id.nick_layout);
        genderLayout = findViewById(R.id.gender_layout);
        birthLayout = findViewById(R.id.birth_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);
        signatureLayout = findViewById(R.id.signature_layout);

        ((TextView) nickLayout.findViewById(R.id.attribute)).setText(R.string.nickname);
        ((TextView) genderLayout.findViewById(R.id.attribute)).setText(R.string.gender);
        ((TextView) birthLayout.findViewById(R.id.attribute)).setText(R.string.birthday);
        ((TextView) phoneLayout.findViewById(R.id.attribute)).setText(R.string.phone);
        ((TextView) emailLayout.findViewById(R.id.attribute)).setText(R.string.email);
        ((TextView) signatureLayout.findViewById(R.id.attribute)).setText(R.string.signature);

        nickText = (EditText) nickLayout.findViewById(R.id.value);
        genderText = (TextView) genderLayout.findViewById(R.id.value);
        birthText = (TextView) birthLayout.findViewById(R.id.value);
        phoneText = (EditText) phoneLayout.findViewById(R.id.value);
        emailText = (EditText) emailLayout.findViewById(R.id.value);
        signatureText = (EditText) signatureLayout.findViewById(R.id.value);
        submit_Button=(Button)findViewById(R.id.submit_button_setting);

        findViewById(R.id.head_layout).setOnClickListener(this);

        genderLayout.setOnClickListener(this);
        birthLayout.setOnClickListener(this);
        genderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOption();
            }
        });


        birthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker1();
            }
        });

        submit_Button.setOnClickListener(this);

        genderList.add("男");
        genderList.add("女");
        genderList.add("其他");

        genderText.setFocusable(false);
        birthText.setFocusable(false);
    }

    private void submit_profile(){
        avUser.setMobilePhoneNumber(phoneText.getText().toString());
        avUser.setEmail(emailText.getText().toString());
        avUser.setUsername(nickText.getText().toString());
        avUser.put("signature",signatureText.getText().toString());
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                avUser.setMobilePhoneNumber(phoneText.getText().toString());
                avUser.setEmail(emailText.getText().toString());
                avUser.setUsername(nickText.getText().toString());
                avUser.put("gender",genderText.getText().toString());
                avUser.put("signature",signatureText.getText().toString());
                avUser.put("birthday",birthText.getText().toString());
                avUser.saveInBackground();

                if (e==null)
                    Toasty.success(UserProfileSettingActivity.this, "更改成功!", Toast.LENGTH_SHORT, true).show();

            }
        });


    }



    private void updateUI() {
        userHead.setImageResource(R.mipmap.icon_test_1);

        //userHead.loadBuddyAvatar(account);
        nickText.setText(avUser.getUsername());
        if (avUser.getString("gender") != null) {
            if (avUser.getString("gender").equals("男")) {
                genderText.setText("男");
            } else if (avUser.getString("gender").equals("女")) {
                genderText.setText("女");
            } else {
                genderText.setText("其他");
            }
        }
        if (avUser.getString("birthday")!= null) {
            birthText.setText(avUser.getString("birthday"));
        }
        if (avUser.getMobilePhoneNumber()!= null) {
            phoneText.setText(avUser.getMobilePhoneNumber());
        }
        if (avUser.getEmail()!= null) {
            emailText.setText(avUser.getEmail());
        }
        if (avUser.getString("signature") != null) {
            signatureText.setText(avUser.getString("signature"));
        }
    }

    public void selectOption(){

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String strGender = genderList.get(options1);
                genderText.setText(strGender);//将选中的数据返回设置在TextView 上。

            }
        })      .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)//设置文字大小
                .setOutSideCancelable(false)// default is true
                .build();
        pvOptions.setPicker(genderList);//条件选择器
        pvOptions.show();

    }


    private void initTimePicker1() {//选择出生年月日
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy ");
        String year_str = formatter_year.format(curDate);
        int year_int = (int) Double.parseDouble(year_str);


        SimpleDateFormat formatter_mouth = new SimpleDateFormat("MM ");
        String mouth_str = formatter_mouth.format(curDate);
        int mouth_int = (int) Double.parseDouble(mouth_str);

        SimpleDateFormat formatter_day = new SimpleDateFormat("dd ");
        String day_str = formatter_day.format(curDate);
        int day_int = (int) Double.parseDouble(day_str);


        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(year_int, mouth_int - 1, day_int);

        //时间选择器
        TimePickerView pvTime1 = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                birthText.setText(getTime(date));
            }
        })      .setType(new boolean[]{true, true, true, false, false, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("年", "月", "日", "", "", "")//默认设置为年月日时分秒
                .isCenterLabel(false)
                .setDividerColor(Color.RED)
                .setTextColorCenter(Color.RED)//设置选中项的颜色
                .setTextColorOut(Color.BLUE)//设置没有被选中项的颜色
                .setContentSize(21)
                .setDate(selectedDate)
                .setLineSpacingMultiplier(1.2f)
                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
        pvTime1.setDate(selectedDate);
        pvTime1.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button_setting:
                submit_profile();
                break;
            case R.id.gender_layout:
                selectOption();
                break;
            case R.id.birth_layout:
                initTimePicker1();
                break;

        }
    }









}
