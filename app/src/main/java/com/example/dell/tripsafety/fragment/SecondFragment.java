package com.example.dell.tripsafety.fragment;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.TripAvtivity;
import com.yinglan.scrolllayout.ScrollLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

	private View mSecondView;

	//上拉隐藏菜单
	private ScrollLayout mScrollLayout;
	//临时菜单拖动把手
	private TextView mTextView;

	//上拉菜单拖动监听器
	private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
		@Override
		public void onScrollProgressChanged(float currentProgress) {
			if (currentProgress >= 0) {
				float precent = 255 * currentProgress;
				if (precent > 255) {
					precent = 255;
				} else if (precent < 0) {
					precent = 0;
				}
				mScrollLayout.getBackground().setAlpha(255 - (int) precent);
				Log.d("PGN", "onScrollProgressChanged: currentProgress = "+currentProgress);
			}
/*
			if (mTextView.getVisibility() == View.VISIBLE)
				Log.d("PGN", "onScrollProgressChanged: 我不见了");
			mTextView.setVisibility(View.GONE);*/
		}

		@Override
		public void onScrollFinished(ScrollLayout.Status currentStatus) {
			if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
				mTextView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onChildScroll(int top) {

		}
	};

	public SecondFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mSecondView = inflater.inflate(R.layout.fragment_second, container, false);
		return mSecondView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initScollLayoutView();
	}

	private void initScollLayoutView(){
		mScrollLayout = (ScrollLayout) mSecondView.findViewById(R.id.scroll_down_layout);
		mTextView = (TextView) mSecondView.findViewById(R.id.text_handler);

		//获得底部导航菜单栏的高度
		int heightOfBottomBar;
		heightOfBottomBar = getBottomBarHeight();
		Log.d("PGN", "initScollLayoutView: 底部菜单栏高度为"+heightOfBottomBar);

		//获得上拉菜单把手的高度
		int heightOfHandler;
		heightOfHandler = getScollHandlerHeight();
		Log.d("PGN", "initScollLayoutView: 下拉菜单把手的高度为"+heightOfHandler);

		//获得上部ToolBar的高度
		int heightOfActionBar;
		heightOfActionBar = getActionBarHeight();
		Log.d("PGN", "initScollLayoutView: 顶部菜单栏的高度为"+heightOfActionBar);
		if(heightOfActionBar == -88)
		{
			heightOfActionBar = 0;
			Log.e("PGN", "initScollLayoutView: ToolBar获取失败！");
		}

		//完全打开时最上方的预留位置
		mScrollLayout.setMinOffset(300);

		//部分打开时的空间大小
		//mScrollLayout.setMaxOffset(mScrollLayout.getScreenHeight());
		mScrollLayout.setMaxOffset(heightOfActionBar + heightOfBottomBar + 500);

		Log.d("PGN", "initScollLayoutView: 屏幕高度为"+mScrollLayout.getScreenHeight());

		//可以关闭时，无法关闭的剩余空间的大小
		mScrollLayout.setExitOffset(heightOfActionBar + heightOfBottomBar + heightOfHandler);
		//mScrollLayout.setExitOffset(400);
		mScrollLayout.setIsSupportExit(true);

		//mScrollLayout.setIsSupportExit(true);

		mScrollLayout.setAllowHorizontalScroll(true);

		//mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
		//不显示背景
		mScrollLayout.getBackground().setAlpha(0);

		mScrollLayout.setToOpen();

		mTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mScrollLayout.setToOpen();
			}
		});
	}

	private int getBottomBarHeight()
	{
		int heightOfBottomBar;
		TripAvtivity tripAvtivity;
		BottomNavigationBar bottomNavigationBar;

		tripAvtivity = (TripAvtivity) getActivity();
		bottomNavigationBar = (BottomNavigationBar) tripAvtivity.findViewById(R.id.bottom_navigation_bar);

		heightOfBottomBar = getViewHeight(bottomNavigationBar);
		return heightOfBottomBar;
	}

	private int getScollHandlerHeight()
	{
		int heightOfTextHandler;
		TextView textHandler = (TextView)mSecondView.findViewById(R.id.text_handler);
		heightOfTextHandler = getViewHeight(textHandler);
		return heightOfTextHandler;
	}


	private int getActionBarHeight()
	{
		int actionBarHeight;
		TypedValue tv =new TypedValue();
		if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getActivity().getResources().getDisplayMetrics());
			return actionBarHeight;
		}
		return -88;
	}


	private int getViewHeight(View view)
	{
		int widthMeasure = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int heightMeasure = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasure, heightMeasure);
		int height = view.getMeasuredHeight();
		int width = view.getMeasuredWidth();

		return height;
	}
}
