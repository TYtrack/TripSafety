package com.example.dell.tripsafety.App;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.*;
import com.avos.avoscloud.AVOSCloud.*;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.dell.tripsafety.Messagereceive.ReceiveProtectService;


/**
 * Created by wli on 16/2/24.
 */
public class App extends Application {
  private final String APP_ID = "epDJ1Kx2Bg3SN9d8QLiAOh4s-gzGzoHsz";
  private final String APP_KEY = "RtpQ1mpWs9dSgOufneelrEVY";

  private static App instance;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;

    // 初始化参数依次为 this, AppId, AppKey
    AVOSCloud.initialize(this,APP_ID,APP_KEY);
    //AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
    AVOSCloud.setDebugLogEnabled(true);

    SDKInitializer.initialize(this);
    SDKInitializer.setCoordType(CoordType.BD09LL);

  }


  public static class CustomMessageHandler extends AVIMMessageHandler {
    //接收到消息后的处理逻辑
    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){
      if(message instanceof AVIMTextMessage){
        Log.e("Tom & Jerry",((AVIMTextMessage)message).getText());
      }
    }

    public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){

    }
  }



}
