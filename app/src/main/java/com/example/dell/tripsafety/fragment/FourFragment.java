package com.example.dell.tripsafety.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.dell.tripsafety.App.App;
import com.example.dell.tripsafety.R;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment {
	EditText phone;
	Button submit;
	EditText message;

	public FourFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_four, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		phone=getActivity().findViewById(R.id.phone_four);
		message=getActivity().findViewById(R.id.phone_message);
		submit=getActivity().findViewById(R.id.button_phone);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

	}

	public void sendMessage() {
		final String other_Phone=phone.getText().toString();
		// Tom 用自己的名字作为clientId，获取AVIMClient对象实例
		AVIMClient my_client = AVIMClient.getInstance(AVUser.getCurrentUser().getMobilePhoneNumber());
		// 与服务器连接
		my_client.open(new AVIMClientCallback() {
			@Override
			public void done(AVIMClient client, AVIMException e) {
				if (e == null) {
					// 创建与Jerry之间的对话
					client.createConversation(Arrays.asList(other_Phone), "Map_Contact", null, new AVIMConversationCreatedCallback() {

								@Override
								public void done(AVIMConversation conversation, AVIMException e) {
									if (e == null) {
										AVIMTextMessage msg = new AVIMTextMessage();
										msg.setText(message.getText().toString());
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

}
