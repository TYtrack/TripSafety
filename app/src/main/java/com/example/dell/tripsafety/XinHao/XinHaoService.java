package com.example.dell.tripsafety.XinHao;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.dell.tripsafety.listenpowerbutton.listenPowerKeyService;

import java.util.Timer;
import java.util.TimerTask;

public class XinHaoService extends Service {
    private Timer timer;
    public XinHaoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        AVUser.getCurrentUser().put("ping", 2);
                        AVUser.getCurrentUser().saveInBackground();
                    }
                });
            }
        },0,1000*10);//每隔10秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行

        return super.onStartCommand(intent, flags, startId);
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
        Intent localIntent = new Intent(getApplicationContext(),XinHaoService.class);
        startService(localIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer=new Timer();

    }

}
