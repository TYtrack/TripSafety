package com.example.dell.tripsafety.TimingCall;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.example.dell.tripsafety.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import es.dmoral.toasty.Toasty;

public class TimingCall extends AppCompatActivity implements CountdownView.OnCountdownEndListener ,View.OnClickListener{
    CountdownView mCvCountdownViewTest6;
    @BindView(R.id.start_timing)
    Button start_timing;

    private List<String> numbers=new ArrayList<>();
    long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_call);
       // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS} , -1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE} , 0);

        ButterKnife.bind(this);
        start_timing.setOnClickListener(this);

        mCvCountdownViewTest6 = (CountdownView)findViewById(R.id.cv_countdownViewTest6);
        time = (long)2 * 60 * 60 * 1000;
        time=5*1000;

        //mCvCountdownViewTest6.start(time);
        mCvCountdownViewTest6.setOnCountdownEndListener(this);
        getNumber();
        initTimePicker1();

    }

    public void getNumber(){
        AVQuery<AVObject> avQuery = new AVQuery<>("Contact");
        avQuery.whereEqualTo("user_number", AVUser.getCurrentUser().getMobilePhoneNumber());

        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size()==0)
                    return;
                AVObject Ao1=list.get(0);
                if (!Ao1.get("Contact_1").equals(""))
                {
                    numbers.add(Ao1.get("Contact_1").toString());
                    Log.e("Contact_1",Ao1.get("Contact_1").toString());
                }
                if (!Ao1.get("Contact_2").equals(""))
                {
                    numbers.add(Ao1.get("Contact_2").toString());
                    Log.e("Contact_2",Ao1.get("Contact_2").toString());
                }
                if (!Ao1.get("Contact_3").equals(""))
                {
                    numbers.add(Ao1.get("Contact_3").toString());
                    Log.e("Contact_3","a"+Ao1.get("Contact_3").toString()+"b");
                }

            }
        });

    }

    @Override
    public void onEnd(CountdownView cv) {
        SmsManager smsManager = SmsManager.getDefault();
        String SMScontent="102";
        Log.e("num",""+numbers.size());
        for(String num :numbers)
        {
            Log.w("已经发","!!!!!!!!!!!!!!!!!!!!");
            Log.e("AKB","num:"+num+"  ");
            smsManager.sendTextMessage(num,null,SMScontent,null,null);
        }
        Toasty.success(TimingCall.this,"倒计时结束", Toast.LENGTH_LONG).show();
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

        SimpleDateFormat format_hour= new SimpleDateFormat("hh");
        String hour_str = format_hour.format(curDate);


        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        //startDate.set(1900, 0, 1);
        startDate.set(0, 0, 1,0,0,0);

        Calendar endDate = Calendar.getInstance();
        endDate.set(0,0,1,24,60,60);


        //时间选择器
        TimePickerView pvTime1 = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                //birthText.setText(getTime(date));
                time=getTime(date);
                mCvCountdownViewTest6.updateShow(time);
                //mCvCountdownViewTest6.start(time);
                //time=
                //

            }
        })      .setType(new boolean[]{ false, false, false,true, true, true}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
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


    private long getTime(Date date) {//可根据需要自行截取数据显示
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (date.getHours()*60*60+date.getMinutes()*60+date.getSeconds())*1000;
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // return format.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_timing:
                mCvCountdownViewTest6.start(time);
                break;
        }
    }
}
