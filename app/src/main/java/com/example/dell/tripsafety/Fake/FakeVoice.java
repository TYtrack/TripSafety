package com.example.dell.tripsafety.Fake;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dell.tripsafety.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FakeVoice extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<Voice> datas;
    private VoiceAdapter adapter;


    private Button start;
    private Button pause;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_voice);

        initRecyclerView();
        init();



    }

    public void initRecyclerView(){
        //初始化RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //模拟的数据（实际开发中一般是从网络获取的）
        datas = new ArrayList<Voice>();

        Voice voice;
        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereEqualTo("provider","qiniu");

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                Log.e("Fake",""+list.size());
                for(AVObject s:list){
                    Voice voice = new Voice();
                    voice.setTitle(s.get("name").toString());

                    voice.setContent(s.get("url").toString());
                    datas.add(voice);
                    LogUtil.log.e("Fake_name!!",""+datas.size());

                }
                Log.e("!!!size",""+datas.size());


                //创建适配器
                adapter = new VoiceAdapter(R.layout.item_voice, datas);

                //给RecyclerView设置适配器
                recyclerView.setAdapter(adapter);
                addListener();


            }
        });


        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }


    private void initMediaPlayer(String url) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(){
        //条目点击事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mediaPlayer.reset();

                initMediaPlayer(datas.get(position).getContent());

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                Toast.makeText(FakeVoice.this, "点击了第" + (position + 1) + "条条目", Toast.LENGTH_SHORT).show();
            }
        });
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
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}
