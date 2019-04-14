package com.example.dell.tripsafety.App;

import android.app.Application;

import com.avos.avoscloud.*;
import com.avos.avoscloud.AVOSCloud.*;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;


/**
 * Created by wli on 16/2/24.
 */
public class App extends Application {
  private final String APP_ID = "epDJ1Kx2Bg3SN9d8QLiAOh4s-gzGzoHsz";
  private final String APP_KEY = "RtpQ1mpWs9dSgOufneelrEVY";


  @Override
  public void onCreate() {
    super.onCreate();

    // 初始化参数依次为 this, AppId, AppKey
    AVOSCloud.initialize(this,APP_ID,APP_KEY);
    AVOSCloud.setDebugLogEnabled(true);

    SDKInitializer.initialize(this);
    SDKInitializer.setCoordType(CoordType.BD09LL);



  }
}
