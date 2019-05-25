package com.example.dell.tripsafety.HelpOther;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil;
import com.example.dell.tripsafety.Fake.Voice;
import com.example.dell.tripsafety.Fake.VoiceAdapter;
import com.example.dell.tripsafety.R;

import java.util.ArrayList;
import java.util.List;

public class HelpListActivity extends AppCompatActivity {
    private List<HelpMessage> datas;
    private HelpAdapter adapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list);
        initRecyclerView();
    }


    public void initRecyclerView(){
        //初始化RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_help);

        //模拟的数据（实际开发中一般是从网络获取的）
        datas = new ArrayList<HelpMessage>();

        HelpMessage message;

        AVQuery<AVObject> query = new AVQuery<>("HelpMessage");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                for(AVObject s:list){
                    HelpMessage helpMessage=new HelpMessage();
                    helpMessage.setTitle(s.get("title").toString());
                    helpMessage.setContent(s.get("message").toString());
                    helpMessage.setHelper_Name(s.get("mobilePhoneNumber").toString());
                    helpMessage.setObj_id(s.getObjectId().toString());
                    datas.add(helpMessage);
                    LogUtil.log.e("Fake_name!!",""+datas.size());

                }
                Log.e("!!!size",""+datas.size());


                //创建适配器
                adapter = new HelpAdapter(R.layout.item_help, datas);
                //给RecyclerView设置适配器
                recyclerView.setAdapter(adapter);


            }
        });


        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

}
