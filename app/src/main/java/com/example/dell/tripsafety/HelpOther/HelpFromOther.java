package com.example.dell.tripsafety.HelpOther;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baoyz.actionsheet.ActionSheet;
import com.example.dell.tripsafety.ChoosePhoto.ChoosePhotoListAdapter;
import com.example.dell.tripsafety.ChoosePhoto.UILImageLoader;
import com.example.dell.tripsafety.ChoosePhoto.UILPauseOnScrollListener;
import com.example.dell.tripsafety.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.HorizontalListView;
import es.dmoral.toasty.Toasty;

public class HelpFromOther extends AppCompatActivity {
    @BindView(R.id.lv_photo)
    HorizontalListView mLvPhoto;
    @BindView(R.id.help_button)
    Button help_button;
    @BindView(R.id.help_ed1)
    EditText help_ed1;
    @BindView(R.id.help_ed2)
    EditText help_ed2;

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int REQUEST_CODE_CROP = 1002;
    private final int REQUEST_CODE_EDIT = 1003;

    double Latitude=0;
    double Longitude=0;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private Button mOpenGallery;
    private List<PhotoInfo> mPhotoList;
    private ChoosePhotoListAdapter mChoosePhotoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_from_other);
        ButterKnife.bind(this);
        mLvPhoto = (HorizontalListView) findViewById(R.id.lv_photo);
        mPhotoList = new ArrayList<>();
        mChoosePhotoListAdapter = new ChoosePhotoListAdapter(this, mPhotoList);
        mLvPhoto.setAdapter(mChoosePhotoListAdapter);
        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", Environment.getExternalStorageDirectory() + "/LeanCloud.png");
                AVObject helpMessage = new AVObject("HelpMessage");
                helpMessage.put("title",help_ed1.getText().toString());
                helpMessage.put("message",help_ed2.getText().toString());
                helpMessage.put("photo_size",mPhotoList.size());
                helpMessage.put("mobilePhoneNumber", AVUser.getCurrentUser().getMobilePhoneNumber());
                PhotoInfo photoInfo=null;
                for (int i=0;i<mPhotoList.size();i++){
                    photoInfo=mPhotoList.get(i);
                    try {
                        final AVFile file = AVFile.withAbsoluteLocalPath("help.png", photoInfo.getPhotoPath());
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                Log.d("File 1", file.getUrl());//返回一个唯一的 Url 地址
                            }
                        });
                        helpMessage.put("photo_"+i,file);
                    }catch (FileNotFoundException e)
                    {
                        LogUtil.log.e("File","Bot found");
                    }
                }
                helpMessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Toasty.success(HelpFromOther.this,"发布求助成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mOpenGallery = (Button) findViewById(R.id.btn_open_gallery);

        mOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //公共配置都可以在application中配置，这里只是为了代码演示而写在此处
                ThemeConfig themeConfig = null;
                themeConfig = ThemeConfig.DEFAULT;

                FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
                cn.finalteam.galleryfinal.ImageLoader imageLoader;
                PauseOnScrollListener pauseOnScrollListener = null;
                imageLoader = new UILImageLoader();
                pauseOnScrollListener = new UILPauseOnScrollListener(false, true);

                boolean muti = true;

                functionConfigBuilder.setMutiSelectMaxSize(9);

                final boolean mutiSelect = muti;

                functionConfigBuilder.setEnableCamera(true);
                functionConfigBuilder.setEnablePreview(true);


                functionConfigBuilder.setSelected(mPhotoList);//添加过滤集合
                final FunctionConfig functionConfig = functionConfigBuilder.build();


                CoreConfig coreConfig = new CoreConfig.Builder(HelpFromOther.this, imageLoader, themeConfig)
                        .setFunctionConfig(functionConfig)
                        .setPauseOnScrollListener(pauseOnScrollListener)
                        .build();
                GalleryFinal.init(coreConfig);

                ActionSheet.createBuilder(HelpFromOther.this, getSupportFragmentManager())
                        .setCancelButtonTitle("取消(Cancel)")
                        .setOtherButtonTitles("打开相册(Open Gallery)", "拍照(Camera)", "裁剪(Crop)", "编辑(Edit)")
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                String path = "/sdcard/pk1-2.jpg";
                                switch (index) {
                                    case 0:
                                        if (mutiSelect) {
                                            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
                                        } else {
                                            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
                                        }
                                        break;
                                    case 1:

                                        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                                        break;
                                    case 2:
                                        if (new File(path).exists()) {
                                            GalleryFinal.openCrop(REQUEST_CODE_CROP, functionConfig, path, mOnHanlderResultCallback);
                                        } else {
                                            Toast.makeText(HelpFromOther.this, "图片不存在", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 3:
                                        if (new File(path).exists()) {
                                            GalleryFinal.openEdit(REQUEST_CODE_EDIT, functionConfig, path, mOnHanlderResultCallback);
                                        } else {
                                            Toast.makeText(HelpFromOther.this, "图片不存在", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        initLocationOption();
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        initImageLoader(this);
        //initFresco();
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /*
    private void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .build();
        Fresco.initialize(this, config);
    }
    */

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoList.addAll(resultList);
                mChoosePhotoListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(HelpFromOther.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };
    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
//注册监听函数
        locationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
//开始定位
        locationClient.start();
    }


    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            Latitude = location.getLatitude();
            //获取经度信息
            Longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

        }
    }


}
