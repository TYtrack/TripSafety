package com.example.dell.tripsafety.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.dell.tripsafety.fragment.RadarFragment;
import com.example.dell.tripsafety.fragment.SettingFragment;
import com.example.dell.tripsafety.utils.Constant;

public class TripAdapter extends FragmentPagerAdapter {
    public TripAdapter(FragmentManager fm){

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.e("TripAdapter","log1");
                return RadarFragment.newInstance();

            case 1:
                return RadarFragment.newInstance();
            case 2:
                return RadarFragment.newInstance();
            case 3:
                return SettingFragment.newInstance();

            default:
                return null;

        }
    }


    @Override
    public int getCount() {
        return Constant.sTabTitles.length;
    }
}
