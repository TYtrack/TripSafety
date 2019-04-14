package com.example.dell.tripsafety;

import android.graphics.Rect;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.dell.tripsafety.adapter.TripAdapter;
import com.example.dell.tripsafety.entity.TabEntity;
import com.example.dell.tripsafety.fragment.CircleFragment;
import com.example.dell.tripsafety.fragment.FirstFragment;
import com.example.dell.tripsafety.fragment.FourFragment;
import com.example.dell.tripsafety.fragment.RadarFragment;
import com.example.dell.tripsafety.fragment.SecondFragment;
import com.example.dell.tripsafety.fragment.SettingFragment;
import com.example.dell.tripsafety.utils.Constant;
import com.example.dell.tripsafety.utils.RxPermissionUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripAvtivity extends AppCompatActivity {
    //底部导航栏实例
    private BottomNavigationBar mBottomNavigationBar;

    //Fragment管理器实例
    private FragmentManager mFragmentManager;

    //五个内容界面
    //private FirstFragment mFirstFragment;
    private CircleFragment mCircleFragment;
    private SecondFragment mSecondFragment;
    private RadarFragment mRadarFragment;
    private FourFragment mFourthFragment;
    private SettingFragment mSettingFragment;

    //默认选择第一个fragment
    private int lastSelectedPosition = 0;

    //Fragment事务
    private FragmentTransaction transaction;


    /**
     * 再次返回键退出程序
     */
    private long lastBack = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_avtivity);
        ButterKnife.bind(this);
        Log.e("TripActivity","log2");




        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        initBottomBar();

        Log.e("TripActivity","log4");

        //从login进入的画面
        Explode explode = new Explode();
        explode.setDuration(50);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTitleBarHeight();
        getStatusBarHeight();
        getAppViewHeight();
    }


    public void getTitleBarHeight() {
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);

        int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();   //要用这种方法
        int titleBarH = viewTop - outRect1.top;

        Log.d("PGN", "标题栏高度-计算:" + titleBarH);
        Log.d("PGN", "viewTop" + viewTop);
        Log.d("PGN", "outRect1.top" + outRect1.top);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        Log.d("PGN", "getStatusBarHeight: 状态栏高度"+result);
        return result;
    }

    public void getAppViewHeight(){
        //屏幕
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int statusBar = dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度

        Log.d("PGN", "dm.heightPixels:" + dm.heightPixels);
        Log.d("PGN", "outRect1.height()" + outRect1.height());
        Log.d("PGN", "应用区高度:" + statusBar);
    }



    private void initBottomBar()
    {
        mBottomNavigationBar
                //MODE_FIXED_NO_TITLE:表示不会显示title，图标不会移动
                //MODE_SHIFTING:点击显示title，图片会点击浮动
                //上面两个都可以增加或去掉_NO_TITLE，表示title有无
                .setMode(BottomNavigationBar.MODE_FIXED)
                //BACKGROUND_STYLE_RIPPLE:点击会有水波纹
                //BACKGROUND_STYLE_STATIC:点击的时候没有水波纹效果
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)  // 背景样式
                .setBarBackgroundColor("#FFFFFF") // 背景颜色
                .setFirstSelectedPosition(lastSelectedPosition)

                .addItem(new BottomNavigationItem(R.drawable.ic_1, "Home")
                        .setActiveColor("#FF3333"))
                .addItem(new BottomNavigationItem(R.drawable.ic_2, "Books")
                        .setActiveColor("#FF3333"))
                .addItem(new BottomNavigationItem(R.drawable.ic_3, "Music")
                        .setActiveColor("#FF3333"))
                .addItem(new BottomNavigationItem(R.drawable.ic_4, "Movies")
                        .setActiveColor("#FF3333"))
                .addItem(new BottomNavigationItem(R.drawable.ic_5, "Games")
                        .setActiveColor("#FF3333"))
                .initialise();

        setDefaultFragment();


        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                lastSelectedPosition = position;//每次点击赋值
                //开启事务
                transaction = mFragmentManager.beginTransaction();

                hideFragment(transaction);

                switch (position) {
                    case 0:
                        if (mCircleFragment == null) {
                            mCircleFragment = new CircleFragment();
                            transaction.add(R.id.fragment_content, mCircleFragment);
                        } else {
                            transaction.show(mCircleFragment);
                        }
                        // transaction.replace(R.id.tb, firstFragment);
                        break;
                    case 1:
                        if (mSecondFragment == null) {
                            mSecondFragment = new SecondFragment();
                            transaction.add(R.id.fragment_content, mSecondFragment);
                        } else {
                            transaction.show(mSecondFragment);
                        }
                        break;
                    case 2:
                        if (mRadarFragment == null) {
                            mRadarFragment = new RadarFragment();
                            transaction.add(R.id.fragment_content, mRadarFragment);
                        } else {
                            transaction.show(mRadarFragment);
                        }
                        break;
                    case 3:
                        if (mFourthFragment == null) {
                            mFourthFragment = new FourFragment();
                            transaction.add(R.id.fragment_content, mFourthFragment);
                        } else {
                            transaction.show(mFourthFragment);
                        }
                        break;
                    case 4:
                        if (mSettingFragment== null){
                            mSettingFragment = new SettingFragment();
                            transaction.add(R.id.fragment_content, mSettingFragment);
                        }else{
                            transaction.show(mSettingFragment);
                        }
                        break;
                }
                // 事务提交
                transaction.commit();
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });
    }



    /**
     * 设置默认开启的fragment
     */
    private void setDefaultFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mSettingFragment = new SettingFragment();
        transaction.add(R.id.fragment_content,mSettingFragment);
        transaction.commit();
    }


    /**
     * 隐藏当前fragment
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (mCircleFragment != null) {
            transaction.hide(mCircleFragment);
        }
        if (mSecondFragment != null) {
            transaction.hide(mSecondFragment);
        }
        if (mRadarFragment != null) {
            transaction.hide(mRadarFragment);
        }
        if (mFourthFragment != null) {
            transaction.hide(mFourthFragment);
        }
        if (mSettingFragment != null){
            transaction.hide(mSettingFragment);
        }
    }



    @Override
    public void onBackPressed() {
        if (lastBack == 0 || System.currentTimeMillis() - lastBack > 2000) {
            Toast.makeText(this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
            lastBack = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }


}
