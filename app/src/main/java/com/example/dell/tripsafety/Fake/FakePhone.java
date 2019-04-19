package com.example.dell.tripsafety.Fake;

import android.app.Activity;
import android.app.Service;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dell.tripsafety.R;

import java.io.IOException;

public class FakePhone extends AppCompatActivity implements View.OnClickListener{
    private Button start;
    private Button pause;
    boolean isVirating;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_phone);
        //Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);

        isVirating = true;
        vibrate(FakePhone.this, new long[]{1000, 1000, 1000, 1000}, 0);
        init();
        initMediaPlayer();
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource("http://lc-epdj1kx2.cn-n1.lcfile.com/ad4c65ea1d1d249ce503/ios.mp3");
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                //关闭震动
                if (isVirating) {
                    isVirating = false;
                    virateCancle(FakePhone.this);
                }
                break;
        }
    }



    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    //以pattern[]方式震动
    public static void vibrate(final Activity activity, long[] pattern,int repeat){
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern,repeat);
    }
    //取消震动
    public static void virateCancle(final Activity activity){
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }

}
