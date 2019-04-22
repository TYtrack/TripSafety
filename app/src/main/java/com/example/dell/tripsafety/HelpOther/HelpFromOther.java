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
}
