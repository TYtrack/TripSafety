package com.example.dell.tripsafety.Messagereceive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.dell.tripsafety.App.App;
import com.example.dell.tripsafety.Protect.ProtectActivity;
import com.example.dell.tripsafety.R;

import es.dmoral.toasty.Toasty;

//接受被监护人的消息
public class ReceiveProtectService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("zzz%^&Q*","开启了");
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
        new ReceiveClass().jerryReceiveMsgFromTom();
        showNotifictionIcon("您所绑定的被守护者长时间未大范围活动，可能处于不安全");

    }


    public class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){
            if(message instanceof AVIMTextMessage){
                Toasty.success(getApplicationContext(),((AVIMTextMessage)message).getText(), Toast.LENGTH_SHORT).show();
                showNotifictionIcon(((AVIMTextMessage)message).getText());
                Log.e("aaaaaa Tom & Jerry",((AVIMTextMessage)message).getText());
            }
        }

        public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){
            Log.e("!!!Tom & Jerry",((AVIMTextMessage)message).getText());
        }
    }



    public ReceiveProtectService() {
    }

    public void showNotifictionIcon(String s1) {
        Intent intent = new Intent();//只显示通知，无页面跳转
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26)
        {
            //当sdk版本大于26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);//
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.bule102)
                    .setContentTitle("消息")
                    .setContentText(s1)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //当sdk版本小于26
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("消息")
                    .setContentText(s1)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.bule102)
                    .build();
            manager.notify(1,notification);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
