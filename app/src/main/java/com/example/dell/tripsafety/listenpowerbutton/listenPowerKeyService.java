package com.example.dell.tripsafety.listenpowerbutton;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.dell.tripsafety.R;

import java.util.ArrayList;
import java.util.List;

public class listenPowerKeyService extends Service {
    private IntentFilter intentFilter;
    private long[] mHits = new long[3];//按键三次用到
    private List<String> numbers=new ArrayList<>();


//这个用不上
    private final BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(action== Intent.ACTION_TIME_TICK)
            {
              //  Toast.makeText(context,"one minute",Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    public void  onCreate()
    {
        intentFilter=new IntentFilter();
        intentFilter.addAction( Intent.ACTION_TIME_TICK);
        registerReceiver(receiver,intentFilter);
        //作为前台服务
        //如果APi大于18 需要弹出一个可见通知
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            Notification.Builder builder=new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("keepAppAlive...");
            builder.setContentText  ("is running");
            startForeground(100,builder.build());
            //先不用
            //通过cancelService移除通知栏
            //Intent intent=new Intent(this,CancelNoticeService.class);
            // startService(intent);
        }else startForeground(100,new Notification());

        //动态注册广播监听电源键
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver, filter);


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
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        //Log.e("!!!!!!!!!","!!!!!!!!!!!!!!!!!!");
        //Toast.makeText(this,"on",Toast.LENGTH_SHORT).show();

        //定个闹钟每三秒通知一次
        long trrigerAtTime= SystemClock.elapsedRealtime()+3*1000;
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        Intent i=new Intent(this,listenPowerKeyService.class);
        PendingIntent pendingIntent= PendingIntent.getService(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trrigerAtTime,pendingIntent);
        //当Service 被终止
        //当资源条件允许条件下，重启service
        return START_STICKY;


    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        //如果Service被杀死 干掉通知
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(100);
        }
        //重启自己
        Intent localIntent = new Intent(getApplicationContext(),listenPowerKeyService.class);
        startService(localIntent);

    }

    //电源键
    // Intent.ACTION_SCREEN_OFF;
    // Intent.ACTION_SCREEN_ON;
    private BroadcastReceiver mBatInfoReceiver=new BroadcastReceiver(){
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            SharedPreferences preference=getSharedPreferences("oneKey",MODE_PRIVATE);
            if(preference.getString("threeKey","off").equals("on"))
            {
                if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.w("test", "PowerKey-off");
                    threeClickFinish();
                }else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.w("test", "PowerKey-on");
                    threeClickFinish();

                }
            }


        }
    };


    private void threeClickFinish() {
        Log.w("发","!!!!!!!!!!!!!!!!!!!!");
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();

        if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
           //发短信
            SmsManager smsManager = SmsManager.getDefault();
            String SMScontent="102";
            Log.e("num",""+numbers.size());
            for(String num :numbers)
            {
                Log.w("已经发","!!!!!!!!!!!!!!!!!!!!");
                smsManager.sendTextMessage(num,null,SMScontent,null,null);
            }
        }
    }

}
