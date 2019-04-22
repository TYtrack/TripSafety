package com.example.dell.tripsafety.HelpOther;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dell.tripsafety.R;

import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CustomReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    try {
      if (intent.getAction().equals("com.pushdemo.action")) {
        JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
        final String message = json.getString("alert");
        Log.e("NML",message);
        Intent intent2 = new Intent();//只显示通知，无页面跳转
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent2,0);

        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

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
          Notification notification = new Notification.Builder(context, id)
                  .setCategory(Notification.CATEGORY_MESSAGE)
                  .setSmallIcon(R.drawable.bule102)
                  .setContentTitle("消息")
                  .setContentText(message)
                  .setContentIntent(pendingIntent)
                  .setAutoCancel(true)
                  .build();
          manager.notify(1, notification);
        }
        else
        {
          //当sdk版本小于26
          Notification notification = new NotificationCompat.Builder(context)
                  .setContentTitle("消息")
                  .setContentText(message)
                  .setContentIntent(pendingIntent)
                  .setSmallIcon(R.drawable.bule102)
                  .build();
          manager.notify(1,notification);
        }

        /*
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(AVOSCloud.applicationContext)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(
                    AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setTicker(message);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        int mNotificationId = 10086;
        NotificationManager mNotifyMgr =
            (NotificationManager) AVOSCloud.applicationContext
                .getSystemService(
                    Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        */
      }
    } catch (Exception e) {

    }
  }
}
