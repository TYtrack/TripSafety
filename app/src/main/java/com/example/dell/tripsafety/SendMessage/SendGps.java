package com.example.dell.tripsafety.SendMessage;

import android.os.AsyncTask;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import java.util.Arrays;

public class SendGps extends AsyncTask<String,Integer,Boolean> {
    public static long LONGITUDE = 0;//经度
    public static long LATITUDE = 0;//纬

    @Override
    protected Boolean doInBackground(String... voids) {
        try {
            while (true){
                sendMessage(voids[0],LONGITUDE,LATITUDE);
            }
        }catch (Exception e){
            return false;
        }
    }

    public void sendMessage(final String phone_num, final long longitude , final long latitude){
        AVIMClient my_client = AVIMClient.getInstance(AVUser.getCurrentUser().getMobilePhoneNumber());
        // 与服务器连接
        my_client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    // 创建与Jerry之间的对话
                    client.createConversation(Arrays.asList(phone_num), "Map_Contact", null, new AVIMConversationCreatedCallback() {

                        @Override
                        public void done(AVIMConversation conversation, AVIMException e) {
                            if (e == null) {
                                AVIMTextMessage msg = new AVIMTextMessage();
                                msg.setText(""+longitude+" "+latitude);
                                // 发送消息
                                conversation.sendMessage(msg, new AVIMConversationCallback() {

                                    @Override
                                    public void done(AVIMException e) {
                                        if (e == null) {
                                            Log.d("Map_Contact", "发送成功！");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LATITUDE=(long)location.getLatitude();
            LONGITUDE=(long)location.getLongitude();
            Log.e("22ccc", "位置：纬度" + location.getLatitude()+"经度"+location.getLongitude());


            if(location.getLocType()==BDLocation.TypeGpsLocation){
                Log.e("zzz","GPS");
            }else if (location.getLocType()==BDLocation.TypeNetWorkLocation){
                Log.e("zzz","NetWork");
            }
        }
    }



}
