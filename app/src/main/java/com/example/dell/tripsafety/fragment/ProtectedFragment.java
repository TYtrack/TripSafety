package com.example.dell.tripsafety.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.example.dell.tripsafety.Messagereceive.ReceiveProtectService;
import com.example.dell.tripsafety.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProtectedFragment extends Fragment {
    public static double LONGITUDE = 0;//经度
    public static double LATITUDE = 0;//纬
    public LocationClient mLocationClient;
    private boolean isFirstLocation = true;

    private BaiduMap baidumap;
    protected MapView map;
    public Timer timer;

    //算是map的索引，通过此id 来按顺序取出坐标点
    private List<String> ids = new ArrayList<>();
    //用来存储坐标点
    private Map<String, LatLng> latlngs = new HashMap<>();

    private double la;
    private double lo;
    private double latitude;
    private double longitude;
    private int size;
    //
    private boolean polygonContainsPoint;

    //marker 相关
    private Marker marker;
    List<Marker> markers = new ArrayList<>();
    public String polygon_name;


    //线
    private Polyline mPolyline;
    //多边形
    private Polygon polygon;

    public ProtectedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_protected, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationClient=new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        map = (MapView) getActivity().findViewById(R.id.map);
        baidumap = map.getMap();
        baidumap.setMyLocationEnabled(true);
        onMapInit();

        timer = new Timer();
        TimerTask task = new TimerTask() {
            private int count;
            @Override
            public void run() {
                sendMessage("13164127008",LONGITUDE,LATITUDE);
                if (!inPolygon(LONGITUDE,LATITUDE))
                {
                    //该点超出范围，发短信。

                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 5*1000,5*1000);

        requestLocation();
        addPolygon();



    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }



    public void sendMessage(final String phone_num, final double longitude , final double latitude){
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
                                Log.e("sendMess",""+longitude+" "+latitude);
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


    public void requestLocation(){
        mLocationClient.start();
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LATITUDE=location.getLatitude();
            LONGITUDE=location.getLongitude();
            Log.e("22ccc", "位置：纬度" + location.getLatitude()+"经度"+location.getLongitude());
            if(location.getLocType()==BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }

            if(location.getLocType()==BDLocation.TypeGpsLocation){
                Log.e("zzz","GPS");
            }else if (location.getLocType()==BDLocation.TypeNetWorkLocation){
                Log.e("zzz","NetWork");
            }
        }
    }


    public void onMapInit(){

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        mLocationClient.setLocOption(option);

        List<String> permissonList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissonList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissonList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissonList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissonList.isEmpty()){
            String []permissions=permissonList.toArray(new String[permissonList.size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }else{
            requestLocation();
        }
    }

    private void navigateTo(BDLocation location){
        if (isFirstLocation){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
            baidumap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(15.5f);
            baidumap.animateMapStatus(update);
            isFirstLocation=false;
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData=locationBuilder.build();
        baidumap.setMyLocationData(locationData);

    }


    //判断点是否在多边形内
    public boolean inPolygon(double longitude_1,double latitude_1) {

        String name = null;
        name = polygon_name;
        Log.e("aaa", "检查的别名是：" + name);
        //判断一个点是否在多边形中
        polygonContainsPoint = SpatialRelationUtil.isPolygonContainsPoint(polygon.getPoints(), new LatLng(latitude_1, longitude_1));
        if (polygonContainsPoint) {
            Toast.makeText(getActivity(), "该点在 " + name + " 区域内。", Toast.LENGTH_SHORT).show();
        }

        return polygonContainsPoint;

    }



    /**
     * 根据坐标来添加marker
     *
     * @param latitude
     * @param longitude
     */
    private void addMarler(double latitude, double longitude) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.point);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                //.zIndex(9)
                .draggable(true);
        //在地图上添加Marker，并显示
        marker = (Marker) baidumap.addOverlay(option);
        markers.add(marker);
        String id = marker.getId();
        Log.e("id:",id);
        latlngs.put(id, new LatLng(latitude, longitude));
        ids.add(id);
    }


    public void addPolygon(){

        latlngs.clear();
        ids.clear();
        la=0;
        lo=0;

        AVQuery<AVObject> query = new AVQuery<>("Circle");
        query.whereEqualTo("protected_num", AVUser.getCurrentUser().getMobilePhoneNumber());
        // 如果这样写，第二个条件将覆盖第一个条件，查询只会返回 priority = 1 的结果
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list==null||list.size()==0)
                    return;
                AVObject av1=list.get(0);
                size=av1.getInt("size");
                for (int s=0;s<size;s++){
                    longitude=av1.getDouble("long_"+s);
                    lo+=longitude;
                    latitude=av1.getDouble("lati_"+s);
                    la+=latitude;
                    addMarler(latitude, longitude);
                }
                drawPolygon();
                // 添加文字，求出多边形的中心点向中心点添加文字
                LatLng llText = new LatLng(la / size, lo / size);
                polygon_name=av1.getString("polygon_name");
                OverlayOptions ooText = new TextOptions()
                        .fontSize(90).fontColor(0xFFFF00FF).text(av1.getString("polygon_name"))
                        .position(llText);
                baidumap.addOverlay(ooText);
                //polygonMap.put(trim_1, polygon);
                //aliasname.add(trim_1);
                //Log.e("aaa", "多边形有几个：" + polygonMap.size());
                //Log.e("aaa", "别名有：" + aliasname.toString());
                for (int j = 0; j < markers.size(); j++) {
                    markers.get(j).remove();
                }
                //polygons.add(polygon);
                //polygon = null;
                latlngs.clear();
                ids.clear();
            }
        });

    }



    /**
     * 如果有大于两个点，就画多边形
     */
    private void drawPolygon() {
        if (polygon != null) {
            polygon.remove();
        }
        LatLng ll = null;
        List<LatLng> pts = new ArrayList<LatLng>();
        for (int i = 0; i < ids.size(); i++) {
            String s = ids.get(i);
            Log.e("aaa", "key= " + s + " and value= " + latlngs.get(s).toString());
            ll = latlngs.get(s);
            pts.add(ll);
        }

        OverlayOptions ooPolygon = new PolygonOptions().points(pts)
                .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
        polygon = (Polygon) baidumap.addOverlay(ooPolygon);
    }
}
