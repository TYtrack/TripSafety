package com.example.dell.tripsafety.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class TripFragment extends Fragment {
    public static TripFragment newInstance(String arg) {
        Log.e("TripFragment","log1");
        Bundle args = new Bundle();
        Log.e("GankFragment","arg:"+arg);
        args.putString("ARG", arg);
        TripFragment fragment = new TripFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
