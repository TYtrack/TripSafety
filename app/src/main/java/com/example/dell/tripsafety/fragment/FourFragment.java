package com.example.dell.tripsafety.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.dell.tripsafety.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jaygoo.widget.rwv.RecordWaveView;

import static com.baidu.speech.audio.MicrophoneServer.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment {
	private boolean isIndangerous_model=false;
	private View fourView;
	private RecordWaveView mRecordWaveView;
	private Button switch_mdoel;
	private EventManager wakeup;
	public FourFragment() {
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		fourView=inflater.inflate(R.layout.fragment_four, container, false);
		mRecordWaveView=(RecordWaveView)fourView.findViewById(R.id.recordWaveView);
		switch_mdoel=(Button)fourView.findViewById(R.id.switch_model);
		return fourView;
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//权限检查
		getpermission();
		//按钮
		switch_mdoel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isIndangerous_model==false)
				{
					switch_mdoel.setText("切换到正常模式");
					switch_mdoel.setBackgroundColor(Color.GREEN);
					isIndangerous_model=true;
					Toast.makeText(getActivity(),"危险模式下，会同时开启关键词唤醒和语音情感分析",Toast.LENGTH_SHORT).show();
				}
				else {
					isIndangerous_model=false;
					switch_mdoel.setText("切换到危险模式");
					switch_mdoel.setBackgroundColor(Color.RED);
					Toast.makeText(getActivity(),"正常模式下，只开启关键词唤醒",Toast.LENGTH_SHORT).show();

				}
			}
		});
		//初始化唤醒EvenManager 注册输出事件
		ininWakeUp();
		//设置参数，开启唤醒
		start();
	}


	///////////////////////////////////////////////////////////
	@Override //与碎片关联的视图被移除时调用
	public  void onDestroyView ()
	{
		super.onDestroyView();
		//
	}
	//////////////////////////////////////////////////////////////
	//初始化唤醒EvenManager 注册输出事件
	private  void ininWakeUp(){
		wakeup= EventManagerFactory.create(getActivity(),"wp");
		wakeup.registerListener(new EventListener() {
			@Override
			public void onEvent(String name, String params, byte [] data, int
					offset, int length) {
				Log.d(TAG, String.format("myevent: name=%s, params=%s", name, params));
//唤醒事件
				if(name.equals("wp.data")){
					try {
						JSONObject json = new JSONObject(params);
						int errorCode = json.getInt("errorCode");
						if(errorCode == 0){
//唤醒成功
							Toast.makeText(getActivity(),"唤醒成功",Toast.LENGTH_SHORT).show();
						} else {
//唤醒失败
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if("wp.exit".equals(name)){
//唤醒已停止
				}
			}
		});
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//开始和停止语音唤醒函数
	/**
	 * 测试参数填在这里
	 * 基于SDK唤醒词集成第2.1 设置唤醒的输入参数
	 */
	private void start() {
		//txtLog.setText("");
		// 基于SDK唤醒词集成第2.1 设置唤醒的输入参数
		Map<String, Object> params = new TreeMap<String, Object>();
		params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
		params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
		// "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
		String json = null; // 这里可以替换成你需要测试的json
		json = new JSONObject(params).toString();
		wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
		//printLog("输入参数：" + json);
	}
	// 基于SDK唤醒词集成第4.1 发送停止事件
	private void stop() {
		wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//关闭唤醒
		stop();
	}
	@Override
	public void onHiddenChanged(boolean hidden)//碎片切换时关闭绘图线程，还有控件设为不可见
	{
		super.onHiddenChanged(hidden);
		if (hidden)
		{
			//mRecordWaveView.selfDestroy();
			mRecordWaveView.selfDestroy();
			mRecordWaveView.setVisibility(View.GONE);

			return;
		}
		else
		{
			mRecordWaveView.setVisibility(View.VISIBLE);
		}
	}





/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//权限
	private void  getpermission(){
		List<String> permissionList = new ArrayList<>();
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			//ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
			permissionList.add(Manifest.permission.RECORD_AUDIO);
		}
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			//ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
			permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}

		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			//ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
			permissionList.add(Manifest.permission.READ_PHONE_STATE);
		}

		if (!permissionList.isEmpty()) {
			String[] permissions = permissionList.toArray(new String[permissionList.size()]);
			ActivityCompat.requestPermissions(getActivity(), permissions, 1);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,String[ ] permissions,int[] grantResults)
	{
		switch (requestCode)
		{
			case 1:
				if(grantResults.length>0)
				{
					for ( int result:grantResults){
						if (result!=PackageManager.PERMISSION_GRANTED){
							//Toast

							return;
						}
					}

				}
				else {
					//Toast


				}
				break;
			default:

		}
	}

	}
