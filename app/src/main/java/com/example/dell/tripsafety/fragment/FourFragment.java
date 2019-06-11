package com.example.dell.tripsafety.fragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.utils.Voice_Forensics;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jaygoo.widget.rwv.RecordWaveView;
import tech.oom.vadlibrary.Vad;

import static com.baidu.speech.audio.MicrophoneServer.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment {
	private boolean isIndangerous_model=false;
	private View fourView;
	private RecordWaveView mRecordWaveView;
	private Button switch_mdoel;
	//唤醒
	private EventManager wakeup;
//录音
	private Vad vad = new Vad();//语音活动检测
//录音参数
	private static AudioRecord mRecord;//Android麦克风
	// 音频获取源
	private int audioSource = MediaRecorder.AudioSource.MIC;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static int sampleRateInHz = 16000;// 44100;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;// AudioFormat.CHANNEL_IN_STEREO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
//
	protected TextView txtStatus;
	protected  TextView txtResult;
	private int bufSize;
	private boolean isRecord=false;
	short audiodata[] = new short[1600];
	byte [] data_send=new byte[8000];//dian存放从
	String result_str;
	Socket socket=null;//
	InputStream in;
	InputStreamReader reader;
	BufferedReader bur;
	OutputStream out;
	DataOutputStream dos;
	Button mSwitch;
	private boolean isFirst=true;
	private double volume;
	private EditText setDb;
	private TextView dbstatus;
	Voice_Forensics vf=new Voice_Forensics();

	public FourFragment() {
		// Required empty public constructor
	}
	///////////////////////////////////////////////////////////
	//override
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		fourView=inflater.inflate(R.layout.fragment_four, container, false);
		mRecordWaveView=(RecordWaveView)fourView.findViewById(R.id.recordWaveView);
		switch_mdoel=(Button)fourView.findViewById(R.id.switch_model);
		txtResult=(TextView)fourView.findViewById(R.id.audio_Result);
		txtStatus=(TextView)fourView.findViewById(R.id.audio_state);
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
		startWakeUp();
		//
		my_audio_start();

	}
	@Override //与碎片关联的视图被移除时调用
	public  void onDestroyView ()
	{
		super.onDestroyView();
		//
	}
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
		stopWakeUp();
		//关闭麦克风
		my_audio_stop();


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
	//////////////////////////////////////////////////////
	//my_audio_start
	private void my_audio_start(){
		isRecord=true;
		mRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
				audioFormat, 8000);//1miao
		mRecord.startRecording();
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket = new Socket("120.77.203.183", 12397);
					// socket = new Socket("192.168.43.250", 12397);
					socket.setSoTimeout(30000);
					// Log.w(String.valueOf(socket.getSendBufferSize()),"输出缓冲区大小！！！！");//26w
					in =socket.getInputStream();
					reader=new InputStreamReader(in,"utf-8");
					bur=new BufferedReader(reader);
					out=socket.getOutputStream();
					dos=new DataOutputStream(out);
					while (isRecord){
						mRecord.read(audiodata, 0, 1600);
						if (vad.processBuffer(16000, audiodata, 1600)){
							//  if (cal_volume(audiodata)>Double.parseDouble(setDb.getText().toString())){
							//如果非静音,读取两秒并发送 data_send //32000 20次 1600点  3200bytes
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									txtStatus.setText(".............");
									txtStatus.setTextColor(Color.RED);
								}
							});
							int readsize=0;
							while (readsize<8){
								//Log.w("one","sad");
								mRecord.read(data_send, 0, 8000);
								dos.write(data_send);
								dos.flush();
								vf.add_data(data_send);
								readsize++;
							}
							//开启新线程等待结果
							Thread thread_receive_result=new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										result_str=bur.readLine();
										if(isFirst){
											result_str="0";
											isFirst=false;//刚开始录音时 不知道为什么 声音属于尖叫声
										}
										int result_index=Integer.parseInt(result_str);

										switch (result_index){
											case 0:
												Log.w("结果：环境","!!!!!");
												mRecordWaveView.setFirst_color(0);
												mRecordWaveView.setSecond_color(0);
												break;
											case 1:
												mRecordWaveView.setFirst_color(1);
												mRecordWaveView.setSecond_color(1);
												Log.w("结果：正常说话","!!!!!");
												break;
											case 2:
												mRecordWaveView.setFirst_color(3);
												mRecordWaveView.setSecond_color(3);
												Log.w("结果：尖叫","!!!!!");
												break;
											case 3:
												mRecordWaveView.setFirst_color(2);
												mRecordWaveView.setSecond_color(2);
												Log.w("结果：生气","!!!!!");
												break;
										}

									}catch (IOException e)
									{
										e.printStackTrace();
									}

									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											switch (result_str)
											{
												case "0":txtResult.setText("normal");break;
												case "1":txtResult.setText("normal");break;
												case "2":txtResult.setText("Abnormal!!!");break;
												case "3":txtResult.setText("Abnormal!!!");break;
											}
										}
									});
								}
							});
							thread_receive_result.start();

						}
						else {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									txtStatus.setText("静音");
									txtStatus.setTextColor(Color.GRAY);
								}
							});
						}
					}
					if(mRecord!=null){//停止占用，释放资源
						mRecord.stop();
						mRecord.release();
						mRecord=null;
					}
				}catch (IOException e){
				}finally {
					try {
						socket.close();
						reader.close();
						dos.close();
					}catch (IOException e) { }
				}
			}
		});
		t.start();
	}
	//////////////////////////////////////////////////////////////
	private void my_audio_stop(){
		isRecord=false;
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
	private void startWakeUp() {
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
	private void stopWakeUp() {
		wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





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
