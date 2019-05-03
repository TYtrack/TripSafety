package com.example.dell.tripsafety.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell.tripsafety.R;

import jaygoo.widget.rwv.RecordWaveView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment {
	private boolean isIndangerous_model=false;
	private View fourView;
	private RecordWaveView mRecordWaveView;
	private Button switch_mdoel;
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

	}
