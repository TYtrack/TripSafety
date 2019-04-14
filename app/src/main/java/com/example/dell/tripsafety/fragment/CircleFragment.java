package com.example.dell.tripsafety.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.dell.tripsafety.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CircleFragment extends Fragment {

    private MapView mMapView = null;
    private BaiduMap baiduMap;

    //marker 相关
    private Marker marker;
    List<Marker> markers = new ArrayList<>();
    //算是map的索引，通过此id 来按顺序取出坐标点
    private List<String> ids = new ArrayList<>();
    //用来存储坐标点
    private Map<String, LatLng> latlngs = new HashMap<>();

    private InfoWindow mInfoWindow;
    //线
    private Polyline mPolyline;
    //多边形
    private Polygon polygon;
    //private List<Polygon> polygons = new ArrayList<>();
    private double latitude;
    private double longitude;
    //是坐标点的多少，用来判断是画线，还是画多边形
    private int size;
    //根据别名来存储画好的多边形
    private Map<String, Polygon> polygonMap = new HashMap<>();
    //多边形的别名
    private List<String> aliasname = new ArrayList<>();
    //
    private boolean polygonContainsPoint;

    private List<Location> locationList=new ArrayList<>();
    //用来存储一个点所在的所有的区域
    List<String> areas = new ArrayList<>();

    private double la;
    private double lo;


    public CircleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragement_circle, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        baiduMap=mMapView.getMap();
        addListener();


    }
    public void addListener(){
        //给marker设置点击事件，用来删除marker
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getActivity());
                button.setBackgroundResource(R.drawable.popup);
                button.setText("删除");
                button.setTextColor(Color.BLACK);
                //button.setWidth(300);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        marker.remove();
                        String id1 = marker.getId();
                        ids.remove(id1);

                        latlngs.remove(id1);
                        Log.e("aaa", "删除后map的size--》" + latlngs.size());
                        baiduMap.hideInfoWindow();
                        if (ids.size() < 2) {
                            if (mPolyline != null) {
                                mPolyline.remove();
                            }
                            return;
                        }
                        drawLine();
                    }
                });
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(button, ll, -50);
                baiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        //给map设置监听事件，用来拿到点击地图的点的坐标
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getActivity(), "坐标是：" + latLng.latitude + ",,," + latLng.longitude, Toast.LENGTH_SHORT).show();
                Log.e("aaa", "ditu d zuobiao is -->" + latLng.latitude + ",,," + latLng.longitude);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                //向地图添加marker
                addMarler(latitude, longitude);
                if (ids.size() >= 2) {
                    drawLine();
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //给marker设置拖拽监听事件，用来获取拖拽完成后的坐标
        baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                String id = marker.getId();
                Log.e("aaa", "id-->" + id);
                double latitude1 = marker.getPosition().latitude;
                double longitude1 = marker.getPosition().longitude;
                //当拖拽完成后，需要把原来存储的坐标给替换掉
                latlngs.remove(id);
                latlngs.put(id, new LatLng(latitude1, longitude1));
                Toast.makeText(getActivity(), "拖拽结束，新位置：" + latitude1 + ", " + longitude1, Toast.LENGTH_LONG).show();

                Log.e("aaa", ids.size() + "---拖拽结束后map 的 " + latlngs.size());

                drawLine();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });


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
        marker = (Marker) baiduMap.addOverlay(option);
        markers.add(marker);
        String id = marker.getId();
        latlngs.put(id, new LatLng(latitude, longitude));
        ids.add(id);
    }

    /**
     * 如果此时有两个点，就画线
     */
    private void drawLine() {
        if (mPolyline != null) {
            mPolyline.remove();
        }
        List<LatLng> points = new ArrayList<LatLng>();
        LatLng l = null;
        for (int i = 0; i < ids.size(); i++) {
            l = latlngs.get(ids.get(i));
            points.add(l);
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(5)
                .color(0xAA00FF00).points(points);
        //AAFF0000
        mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
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
        polygon = (Polygon) baiduMap.addOverlay(ooPolygon);
    }

    public void addName(){
        LatLng l = null;
        la = 0;
        lo = 0;
        size = ids.size();
        if (size <= 2) {
            Toast.makeText(getActivity(), "点必须大于2", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < size; i++) {
            l = latlngs.get(ids.get(i));
            la = la + l.latitude;
            lo = lo + l.longitude;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入名字：");
        View inflate = View.inflate(getActivity(), R.layout.dialog_aliasname, null);
        final EditText edt_alias = inflate.findViewById(R.id.edt_alias);
        builder.setView(inflate);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String trim = edt_alias.getText().toString().trim();
                if (trim.equals("")) {
                    Toast.makeText(getActivity(), "别名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                drawPolygon();
                // 添加文字，求出多边形的中心点向中心点添加文字
                LatLng llText = new LatLng(la / size, lo / size);
                OverlayOptions ooText = new TextOptions()
                        .fontSize(90).fontColor(0xFFFF00FF).text(trim + "")
                        .position(llText);
                baiduMap.addOverlay(ooText);
                polygonMap.put(trim, polygon);
                aliasname.add(trim);
                polygon = null;
                Log.e("aaa", "多边形有几个：" + polygonMap.size());
                Log.e("aaa", "别名有：" + aliasname.toString());
                for (int j = 0; j < markers.size(); j++) {
                    markers.get(j).remove();
                }
                //polygons.add(polygon);
                //polygon = null;
                latlngs.clear();
                ids.clear();
            }
        });

        builder.setNeutralButton("确定并保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


}
