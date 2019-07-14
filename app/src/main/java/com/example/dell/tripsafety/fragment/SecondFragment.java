package com.example.dell.tripsafety.fragment;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.CoordinateConverter;

import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.SecondMap.GridMap;
import com.example.dell.tripsafety.SecondMap.Net;
import com.example.dell.tripsafety.SecondMap.PathDeviateDetection;
import com.example.dell.tripsafety.TripAvtivity;
import com.yinglan.scrolllayout.ScrollLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment implements BaiduMap.OnMapClickListener,
		OnGetRoutePlanResultListener, OnGetSuggestionResultListener , View.OnClickListener {

	private View mSecondView;

	//上拉隐藏菜单
	private ScrollLayout mScrollLayout;
	//临时菜单拖动把手
	private TextView mTextView;
	private ImageView mImageView;
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


	//创建定位客户程序
	public LocationClient mLocationClient;

	private MapView map_view;

	private BaiduMap baiduMap;
	private boolean isFirstLocate = true;

	//创建OverlayOptions的List
	private List<OverlayOptions> point_options = new ArrayList<OverlayOptions>();
	private List<OverlayOptions> area_options = new ArrayList<OverlayOptions>();

	//创建Overlay的List
	private List<Marker> point_overlays = new ArrayList<Marker>();
	private List<GroundOverlay> area_overlays = new ArrayList<GroundOverlay>();

	//创建记录覆盖区是否被点击过的List
	private List<Integer> area_covers = new ArrayList<Integer>();

	//创建网格地图对象
	private GridMap gridMap;
	private int[] regPath;
	private PathDeviateDetection deviateTest;   //偏离测试对象

	//储存中间经纬度
	private List<Double> lat_list = new ArrayList<Double>();
	private List<Double> lngt_list = new ArrayList<Double>();


	//覆盖物图片
	private BitmapDescriptor bdGround_1;
	private BitmapDescriptor bdGround_2;
	private BitmapDescriptor bdGround_3;
	private BitmapDescriptor bdGround_click;

	//路径搜索模块
	private RoutePlanSearch mSearch = null;

	//地点输入提示检索
	private SuggestionSearch mSuggestionSearch = null;

	//poi检索模块
	private PoiSearch mPoiSearch = null;

	//按钮
	private Button walk_search;
	private Button net_test;



	//文本编辑框
	private EditText start_city;
	//private EditText start_place;
	private AutoCompleteTextView start_place;//自动填充文本框
	private EditText end_city;
	//private EditText end_place;
	private AutoCompleteTextView end_place;//自动填充文本框

	//自动填充文本框的适配器
	private ArrayAdapter<String> startAdapter = null;
	private ArrayAdapter<String> endAdapter = null;

	//用于区分AuTo文本框
	private int auToEditNum = 0;



	private LinkedList<String> linkedListTest = new LinkedList<String>();


	//路径显示测试
	private List<LatLng> linePoints = new ArrayList<LatLng>();
	StringBuilder outStr = new StringBuilder();




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.d("TimeTest", "onActivityCreated: 99999999999999");
		//利用getApplicationContext()获取全局context对象参数
		//mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient = new LocationClient(getActivity().getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		SDKInitializer.initialize(getActivity().getApplicationContext());

		Log.d("TimeTest", "onActivityCreated: 888888888888889");

		// Inflate the layout for this fragment
		mSecondView = inflater.inflate(R.layout.fragment_second, container, false);

		Log.d("TimeTest", "onActivityCreated: ****************");

		return mSecondView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//初始化上拉窗口布局
		initScollLayoutView();


		Log.d("TimeTest", "onActivityCreated: 111111");
		map_view = (MapView) mSecondView.findViewById(R.id.baidu_map);
		//利用.getMap()从view中获取地图实例
		baiduMap = map_view.getMap();

		baiduMap.setMyLocationEnabled(true);
		Log.d("TimeTest", "onActivityCreated: 2222222");

		walk_search = (Button) mSecondView.findViewById(R.id.walk_search);
		net_test = (Button) mSecondView.findViewById(R.id.net_test);

		walk_search.setOnClickListener(this);
		net_test.setOnClickListener(this);


		start_city = (EditText) mSecondView.findViewById(R.id.start_city);
		//start_place = (EditText) findViewById(R.id.start_place);
		start_place = (AutoCompleteTextView) mSecondView.findViewById(R.id.start_place);

		end_city = (EditText) mSecondView.findViewById(R.id.end_city);
		//end_place = (EditText) findViewById(R.id.end_place);
		end_place = (AutoCompleteTextView) mSecondView.findViewById(R.id.end_place);
		Log.d("TimeTest", "onActivityCreated: 33333333333");


		startAdapter = new ArrayAdapter<>(mSecondView.getContext(), android.R.layout.simple_dropdown_item_1line);
		endAdapter = new ArrayAdapter<>(mSecondView.getContext(), android.R.layout.simple_dropdown_item_1line);

		//绑定适配器
		start_place.setAdapter(startAdapter);
		start_place.setThreshold(1);       //表示检测到一个字符就开始匹配
		end_place.setAdapter(endAdapter);
		end_place.setThreshold(1);        //表示检测到一个字符就开始匹配

		Log.d("TimeTest", "onActivityCreated: 444444444");



/////////////////////////////////////////////////////////
		String[] color = new String[]{"#33FFFF", "#33FF33", "#FFFF00", "#FF3333"};
		bdGround_1 = BitmapDescriptorFactory.fromBitmap(operateBitMap(color[0]));
		bdGround_2 = BitmapDescriptorFactory.fromBitmap(operateBitMap(color[1]));
		bdGround_3 = BitmapDescriptorFactory.fromBitmap(operateBitMap(color[2]));
		bdGround_click = BitmapDescriptorFactory.fromBitmap(operateBitMap(color[3]));
/////////////////////////////////////////////////////////

		baiduMap.setOnMapClickListener(this);
		Log.d("TimeTest", "onActivityCreated: 55555555");



		//创建路径搜索实例
		mSearch = RoutePlanSearch.newInstance();
		Log.d("PGNmap", "onCreate: 创建搜索实例" + mSearch.toString());
		//设置路线规划检索监听器
		mSearch.setOnGetRoutePlanResultListener(this);


		Log.d("PGNmap", "onCreate: 准备开始检索");

		// 初始化建议搜索模块，注册建议搜索事件监听
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		Log.d("TimeTest", "onActivityCreated: 66666666");



		/* 当输入关键字变化时，动态更新建议列表 */
		start_place.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() <= 0) {
					return;
				}

				auToEditNum = 1;
				Log.d("PGNmap", "onTextChanged: 此时的文本框标号为"+auToEditNum);

				/* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
				mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
						.keyword(s.toString())
						.city(start_city.getText().toString()));
			}
		});

		/* 当输入关键字变化时，动态更新建议列表 */
		end_place.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() <= 0) {
					return;
				}

				auToEditNum = 2;
				Log.d("PGNmap", "onTextChanged: 此时的文本框标号为"+auToEditNum);

				/* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
				mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
						.keyword(s.toString())
						.city(end_city.getText().toString()));
			}
		});



	}

	private void requestLocation() {
		mLocationClient.start();
	}



	private void navigateTo(BDLocation location) {
		if (isFirstLocate) {
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			update = MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			isFirstLocate = false;
		}
		MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData = locationBuilder.build();
		baiduMap.setMyLocationData(locationData);
	}

	private void navigateToStart(LatLng location) {
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(location);
		baiduMap.animateMapStatus(update);
		update = MapStatusUpdateFactory.zoomTo(16f);
		baiduMap.animateMapStatus(update);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.walk_search:
				searchButtonProcess();
				break;
			case R.id.net_test:
				netTransitionTest();
				break;
		}
	}

	public void searchButtonProcess() {
		String startCity;
		String startPlace;
		String endCity;
		String endPlace;

		startCity = start_city.getText().toString().trim();
		startPlace = start_place.getText().toString().trim();
		endCity = end_city.getText().toString().trim();
		endPlace = end_place.getText().toString().trim();

		//准备起终点信息
		PlanNode stNode = PlanNode.withCityNameAndPlaceName(startCity, startPlace);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName(endCity, endPlace);

		mSearch.walkingSearch(new WalkingRoutePlanOption()
				.from(stNode)
				.to(enNode));

	}

	public void netTransitionTest(){
		//Net.NetTransition("192.168.43.183",4555, "asfad", outStr);
		//NetTransition("192.168.43.183",4555, "asfad", outStr);
		new Thread(new Runnable() {
			@Override
			public void run() {
				double lat;
				double lngt;
				LatLng linePoint;
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map);

				//Net.NetTransition("120.77.203.183",7865, "asfad", outStr);
				Net.NetTransition("192.168.43.183",12398, "asfad", outStr);

				String recStr = outStr.toString();
//			    String recStr = "22.8026833333,108.328345*22.810142,108.328307*22.809769,108.331587*22.814742,108.331385*22.818143,108.320319*22.813654,108.325708*" +
//					    "22.8109666667,108.325485*22.8181283333,108.332218333*22.8181233333,108.32362*22.809213,108.33108*22.8124816667,108.334326667*22.8182233333,108.324943333*" +
//					    "22.8187616667,108.33117*22.8022683333,108.328148333*22.818143,108.320319*22.813695,108.33389*22.814742,108.331385*22.8112433333,108.33121*22.8181233333,108.32362*" +
//					    "22.814205,108.32275*22.814205,108.32275*" +
//					    "22.8181233333,108.32362*22.814742,108.331385*22.818265,108.32037*22.8109666667,108.325485*22.813695,108.33389*22.8181,108.326518333*" +
//					    "22.818265,108.32037*22.8100566667,108.3283*22.8181,108.326518333*22.8181233333,108.32362";
				Log.d("PGNline", "run: 接收到的路径位置信息为："+recStr);
				for(String route : recStr.split("\\+")){
					for(String point : route.split("\\*")){
						String singlePoint[] = point.split(",");
						lat = Double.parseDouble(singlePoint[0]);
						lngt = Double.parseDouble(singlePoint[1]);

						Log.d("PGNline", "netTransitionTest: 原始纬度："+lat+",经度："+lngt);

						linePoint = coordinateChange(lat, lngt);
						Log.d("PGNline", "netTransitionTest: 坐标转换后"+linePoint.toString());

						linePoints.add(linePoint);

						//生成点Overlay的对象
						//OverlayOptions point_option_1 = creatPointOption(linePoint.latitude, linePoint.longitude, bitmap);

						//baiduMap.addOverlay(point_option_1);
					}
					//设置折线的属性
					if(linePoints.size() > 1){
						OverlayOptions mOverlayOptions = new PolylineOptions()
								.width(10)
								.color(0xAAFF0000)
								.points(linePoints);
						//在地图上绘制折线
						//mPloyline 折线对象
						Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);
						Log.d("PGNline", "netTransitionTest: 1 条路线显示完毕");
					}
					linePoints.clear();
				}

				Log.d("PGNline", "netTransitionTest: 所有路线显示完毕。");
			}
		}).start();
	}

	/**
	 * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
	 *
	 * @param res    Sug检索结果
	 */
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}

		List<String> suggest = new ArrayList<>();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null) {
				suggest.add(info.key);
			}
		}

		switch (auToEditNum) {
			case 1:
				startAdapter = new ArrayAdapter<>(mSecondView.getContext(), android.R.layout.simple_dropdown_item_1line,
						suggest);
				start_place.setAdapter(startAdapter);
				startAdapter.notifyDataSetChanged();
				break;
			case 2:
				endAdapter = new ArrayAdapter<>(mSecondView.getContext(), android.R.layout.simple_dropdown_item_1line,
						suggest);
				end_place.setAdapter(endAdapter);
				endAdapter.notifyDataSetChanged();
			default:
				break;
		}
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		Log.d("PGNmap", "onGetWalkingRouteResult: 开始调用步行路径搜索算法");
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Log.d("PGNmap", "onGetWalkingRouteResult: 错误" + result.error);
			Toast.makeText(mSecondView.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			AlertDialog.Builder builder = new AlertDialog.Builder(mSecondView.getContext());
			builder.setTitle("提示");
			builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.d("PGNmap", "onGetWalkingRouteResult: 错误" + result.error);
			Log.d("PGNmap", "onGetWalkingRouteResult: 错误" + result.getClass().getName().toString());

			Log.d("PGNmap", "onGetWalkingRouteResult: 步行路径规划的条数为" + result.getRouteLines().size());

			if (result.getRouteLines().size() <= 0) {
				Toast.makeText(mSecondView.getContext(), "抱歉，没有找到相关路径", Toast.LENGTH_SHORT).show();
			} else if (result.getRouteLines().size() > 0) {
				//获取路径规划数据,(以返回的第一条数据为例)
				//为WalkingRouteOverlay实例设置路径数据
				//overlay.setData(result.getRouteLines().get(0));

				//添加新路径之前，清空之前的路径列表
				lat_list.clear();
				lngt_list.clear();

				WalkingRouteLine routeLine = result.getRouteLines().get(0);
				Log.d("PGNmap", "onGetDrivingRouteResult: 一条路线中Step的个数" + routeLine.getAllStep().size());
				for (WalkingRouteLine.WalkingStep step : routeLine.getAllStep()) {
					for (LatLng point : step.getWayPoints()) {
						lat_list.add(point.latitude);
						lngt_list.add(point.longitude);
						//Log.d("PGNmap", "onGetWalkingRouteResult: "+point.toString());
					}
				}
				double lat_min_value = getRouteMinValue(lat_list);
				double lngt_min_value = getRouteMinValue(lngt_list);
				double lat_max_value = getRouteMaxValue(lat_list);
				double lngt_max_value = getRouteMaxValue(lngt_list);

				gridMap = new GridMap(lat_min_value, lngt_min_value, lat_max_value, lngt_max_value, 10);
				gridMapGenerate(gridMap);
				deviateTest = new PathDeviateDetection(gridMap);
				//在地图上绘制WalkingRouteOverlay
				//overlay.addToMap();
			}
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mSecondView.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {


		}
	}

	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mSecondView.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点模糊，获取建议列表
			result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

		}
	}


	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mSecondView.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.d("PGNmap", "onGetDrivingRouteResult:  错误" + result.error);

			if (result.getRouteLines().size() > 0) {
				//获取路径规划数据,(以返回的第一条数据为例)
				//为WalkingRouteOverlay实例设置路径数据
				// overlay.setData(walkingRouteResult.getRouteLines().get(0));

				DrivingRouteLine routeLine = result.getRouteLines().get(0);
				Log.d("PGNmap", "onGetDrivingRouteResult: 一条路线中Step的个数" + routeLine.getAllStep().size());
				for (DrivingRouteLine.DrivingStep step : routeLine.getAllStep()) {
					for (LatLng point : step.getWayPoints()) {
						lat_list.add(point.latitude);
						lngt_list.add(point.longitude);
						Log.d("PGNmap", "onGetWalkingRouteResult: " + point.toString());
					}
				}
			}
		}
	}

	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mSecondView.getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			AlertDialog.Builder builder = new AlertDialog.Builder(mSecondView.getContext());
			builder.setTitle("提示");
			builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

		}
	}

	@Override
	public void onMapClick(LatLng point) {
		double pointLat = point.latitude;
		double pointLngt = point.longitude;
		Log.d("PGNmap", "onMapClick: 点击位置的坐标为 " + point.toString());

		int gridId;

		if(gridMap != null  && gridMap.isContainPoint(pointLat, pointLngt)) {
			gridId = gridMap.positionToGrid(pointLat, pointLngt);
			Log.d("PGNmap", "onMapClick: 需要查找的覆盖对象的编号为 "+gridId);

			Bundle bundle = new Bundle();
			int id_info;

			Log.d("PGNmap", "onMapClick: 开始搜索点击的覆盖对象");
			//从之前存储的区域覆盖List中遍历查找相应的gridId覆盖对象
			for (GroundOverlay overlay : area_overlays) {
				//将父类转换为子类
				Log.d("PGNmap", "onMapClick: 查看变量 1 的地址 "+overlay.toString());
				//GroundOverlay groundOverlay = (GroundOverlayOptions) option;
				//Log.d("PGNmap", "onMapClick: 查看变量 2 的地址 "+groundOption.toString());

				//获得该覆盖的额外信息
				bundle = overlay.getExtraInfo();
				id_info = bundle.getInt("gridId");
				Log.d("PGNmap", "onMapClick:  此时遍历到的ID为 "+id_info);

				if (id_info == gridId)//找到网格编号一致的覆盖对象
				{
					if (area_covers.contains(id_info))//判断它是否被点击过
					{
						//如果被点击过,将其从点击列表中删去，然后改变其的覆盖图片
						Iterator<Integer> it = area_covers.iterator();
						while (it.hasNext()) {//删除列表中的指定元素
							int id = it.next();
							if (id == id_info) {
								it.remove();
							}
						}
						overlay.setImage(bdGround_2); //更改覆盖的图片，变为初始化时的图片
						//OverlayOptions overlayOption = (OverlayOptions)groundOption;
						//baiduMap.addOverlay(overlayOption);
						break;
					}
					else{
						//如果没有被点击过，将其加入点击列表，图片变为按压后的图片
						area_covers.add(id_info);
						overlay.setImage(bdGround_click);//图片变为按压后的图片
						//OverlayOptions overlayOption = (OverlayOptions)groundOption;


						//测试用
						linkedListTest.add(String.valueOf(id_info));
						//测试用


						//Log.d("PGNmap", "onMapClick: 查看变量 3 的地址 "+overlayOption.toString());
						//Log.d("PGNmap", "onMapClick: 查看变量 0 的地址 "+area_options.get(0).toString());

						//baiduMap.addOverlay(overlayOption);
						break;
					}
				}
			}
		}
		else{
			String path [] = null;
			double distance;
			if(!linkedListTest.isEmpty())
				path = (String[]) linkedListTest.toArray(new String[0]);

			deviateTest.setCurrentPath(path);
			distance = deviateTest.deviateDetection(point);
			Log.d("PGNdis", "onMapClick: distance为："+distance);
		}
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}


	/**
	 * 在地图上显示当前网格地图
	 *
	 * @param gridMap
	 */
	public void gridMapGenerate(GridMap gridMap) {
		int gridNums;
		int gridColumns;
		int gridRows;
		double gridSize;

		gridRows = gridMap.getGridRows();
		gridColumns = gridMap.getGridColumns();
		gridSize = gridMap.getGridSize();
		gridNums = gridMap.getGridNums();

		Log.d("PGNmap", "gridMapGenerate: 该网格横标号的最大值" + gridRows);
		Log.d("PGNmap", "gridMapGenerate: 该网格列标号的最大值" + gridColumns);

		double first_grid_position[];
		first_grid_position = gridMap.gridToPosition(1);
//        double min_lat = first_grid_position[0];
//        double min_lngt = first_grid_position[1];
//        double max_lat = first_grid_position[2];
//        double max_lngt = first_grid_position[3];


		//创建编号为 1 的网格副本，在此副本上进行修改
		double position[] = Arrays.copyOf(first_grid_position, first_grid_position.length);

		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map);

		double point_lat = (position[0] + position[2]) / 2;
		double point_lngt = (position[1] + position[3]) / 2;


		double last_grid_position[];
		last_grid_position = gridMap.gridToPosition(gridNums);
		double last_1 = (last_grid_position[0] + last_grid_position[2]) / 2;
		double last_2 = (last_grid_position[1] + last_grid_position[3]) / 2;

		//area_options.add(creatAreaOption(position,bdGround));

		//area_options.add(creatPointOption(last_1,last_2,bitmap));
		//area_options.add(creatPointOption(point_lat,point_lngt,bitmap));
		//point_options.add(creatPointOption(lat_list.get(0),lngt_list.get(0),bitmap));
		//point_options.add(creatPointOption(lat_list.get(lat_list.size()-1),lngt_list.get(lngt_list.size()-1),bitmap));


		//在地图上批量添加Marker，储存并显示
		//point_overlays = (List<Marker>) baiduMap.addOverlays(point_options);


		//显示网格路径之前先清空之前地图上的Overlay
		baiduMap.clear();
		point_overlays.clear();
		area_overlays.clear();


		//生成点Overlay的对象
		OverlayOptions point_option_1 = creatPointOption(lat_list.get(0), lngt_list.get(0), bitmap);
		OverlayOptions point_option_2 = creatPointOption(lat_list.get(lat_list.size() - 1), lngt_list.get(lngt_list.size() - 1), bitmap);
		//储存关于点的Overlay对象
		point_overlays.add((Marker) baiduMap.addOverlay(point_option_1));
		point_overlays.add((Marker) baiduMap.addOverlay(point_option_2));

		//将地图移到起点
		LatLng startPoint = new LatLng(lat_list.get(0), lngt_list.get(0));
		navigateToStart(startPoint);

		Log.d("PGNmap", "gridMapGenerate: point_overlays的长度为 " + point_overlays.size());

		int gridId;
		OverlayOptions area_option;
		for (int r = 1; r <= gridColumns; r++) {
			for (int i = 1; i <= gridRows; i++)  //直到把行网格都遍历完
			{
				gridId = (r - 1) * gridRows + i;   //计算出当前网格的编号
				area_option = creatAreaOption(position, bdGround_3, gridId);
				//储存网格区域的覆盖对象
				area_overlays.add((GroundOverlay) baiduMap.addOverlay(area_option));


				//创建网格区域,并加入options列表
				//area_options.add(creatAreaOption(position, bdGround_3, gridId));
				//Log.d("PGNmap", "gridMapGenerate: 此时在第"+i+"行，第"+r+"列");
				//Log.d("PGNmap", "gridMapGenerate: 位置是 "+position[0]+"  "+position[1]+"  "+position[2]+"  "+position[3]);
				//baiduMap.addOverlay(creatAreaOption(position,bdGround_2, gridId));

				//point_lat = (position[0]+position[2])/2;
				//point_lngt = (position[1]+position[3])/2;

				//point_options.add(creatPointOption(point_lat,point_lngt,bitmap));

				//此时只对经度位置进行变化
				if (i < (gridRows - 1)) {
					position[1] += gridSize;
					position[3] += gridSize;
				} else if (i == (gridRows - 1)) {
					position[1] += gridSize;
					position[3] = gridMap.getMapMaxLngt();
				}
			}
			//一行遍历结束,将position的经度移至行起点
			//接着把position向上移一格
			position[1] = first_grid_position[1];
			position[3] = first_grid_position[3];
			if (r < (gridColumns - 1)) {
				position[0] += gridSize;
				position[2] += gridSize;
			} else if (r == (gridColumns - 1)) {
				position[0] += gridSize;
				position[2] = gridMap.getMapMaxLat();
			}
		}
		//在地图上批量添加Marker，并显示
		//area_overlays = baiduMap.addOverlays(area_options);
	}

	public double getRouteMinValue(List<Double> routeList) {
		if (routeList.isEmpty()) {
			Log.d("PGNmap", "getRouteMinValue: 路径经纬度列表为空！");
			return 0;
		}
		return Collections.min(routeList);
	}

	public double getRouteMaxValue(List<Double> routeList) {
		if ((routeList.isEmpty())) {
			Log.d("PGNmap", "getRouteMaxValue: 路径经纬度列表为空！");
			return 0;
		}
		return Collections.max(routeList);
	}

	/**
	 * 将原始GPS设备采集的位置数据转换为百度地图编码地址
	 * @param lat 源纬度值
	 * @param lngt 源经度值
	 * @return 返回转换后的坐标，LatLng类型
	 */
	public LatLng coordinateChange(double lat, double lngt){
		LatLng sourceLatLngt = new LatLng(lat, lngt);
		//初始化坐标转换工具类，指定源坐标类型和坐标数据
		// sourceLatLngt待转换坐标
		CoordinateConverter converter  = new CoordinateConverter()
				.from(CoordinateConverter.CoordType.GPS)
				.coord(sourceLatLngt);

		//desLatLng 转换后的坐标
		LatLng desLatLng = converter.convert();

		return desLatLng;
	}


	/**
	 * 返回该点生成的option类型变量
	 *
	 * @param lat    该点的纬度
	 * @param lngt   该点的经度
	 * @param bitmap 显示点的图案
	 * @return 返回该点生成的option类型变量
	 */
	public OverlayOptions creatPointOption(double lat, double lngt, BitmapDescriptor bitmap) {
		//定义Maker坐标点
		LatLng point = new LatLng(lat, lngt);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(point)
				//.perspective(true)
				.flat(false)
				.icon(bitmap);
		return option;
	}

	/**
	 * 返回由西南坐标和东北坐标确定的一个区域option
	 *
	 * @param positions 这是一个长度为四的数组变量，前两个为西南坐标经纬度，后面为东北坐标经纬度
	 * @param bdGround  这是在该区域显示的图片
	 * @return 返回生成的option变量
	 */
	public OverlayOptions creatAreaOption(double[] positions, BitmapDescriptor bdGround, int gridId) {
		//设置该覆盖物对象的id额外信息
		Bundle bundle = new Bundle();
		bundle.putInt("gridId", gridId);

		//定义Ground的显示地理范围
		LatLng southwest = new LatLng(positions[0], positions[1]);
		LatLng northeast = new LatLng(positions[2], positions[3]);
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(northeast)
				.include(southwest)
				.build();

		//定义GroundOverlayOptions对象
		OverlayOptions areaOption = new GroundOverlayOptions()
				.positionFromBounds(bounds)
				.image(bdGround)
				.extraInfo(bundle)
				.transparency(0.3f); //覆盖物透明
		return areaOption;
	}

	/**
	 * 制作一张纯色的Bitmap图片
	 *
	 * @param color 填充的颜色
	 * @return Bitmap类型的图片
	 */
	public Bitmap operateBitMap(String color) {
		Bitmap bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.parseColor(color));//填充颜色
		return bitmap;
	}

























	//初始化上拉底部窗口参数
	private void initScollLayoutView(){
		mScrollLayout = (ScrollLayout) mSecondView.findViewById(R.id.scroll_down_layout);
		mTextView = (TextView) mSecondView.findViewById(R.id.text_handler);
		//mImageView=(ImageView)mSecondView.findViewById(R.id.text_handler);
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
		//ImageView textHandler = (ImageView) mSecondView.findViewById(R.id.text_handler);
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


	public class MyLocationListener extends BDAbstractLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
            /*
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经度：").append(location.getLongitude()).append("\n");
            currentPosition.append("定位方式：");

            if (location.getLocType() == BDLocation.TypeGpsLocation)
            {
                currentPosition.append("GPS");
            }
            else if(location.getLocType() == BDLocation.TypeNetWorkLocation)
            {
                currentPosition.append("网络");
            }
            positionText.setText(currentPosition);
            */
			if (location.getLocType() == BDLocation.TypeGpsLocation
					|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
				navigateTo(location);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("PGN", "onDestroyView: 调用！");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSearch != null) {
			mSearch.destroy();
			Log.d("PGNmap", "onDestroy: 搜索实例不为空，销毁！");
		} else
			Log.d("PGNmap", "onDestroy: 搜索实例为空");
		//mPoiSearch.destroy();
		//碎片销毁，结束定位，这里只是结束当前碎片中的定位
		//整个应用应该有个整体定位，互不干扰
		mLocationClient.stop();

		mSuggestionSearch.destroy();
		map_view.onDestroy();
		baiduMap.setMyLocationEnabled(false);
	}
}
