package com.example.dell.tripsafety.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dell.tripsafety.Protect.ProtectActivity;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.TripAvtivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseProtectFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.undraw_fatherhood)
    ImageView undraw_fatherhood;
    @BindView(R.id.undraw_doll_play)
    ImageView undraw_doll_play;
    @BindView(R.id.enter_circle)
    Button enter_circle;



    public ChooseProtectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_choose_protect, container, false);
        ButterKnife.bind(this, view);//绑定framgent
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        undraw_doll_play.setOnClickListener(this);
        undraw_fatherhood.setOnClickListener(this);
        enter_circle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.undraw_fatherhood:
                Intent intent1=new Intent(getActivity(), ProtectActivity.class);
                startActivity(intent1);
                break;
            case R.id.enter_circle:
                TripAvtivity tripAvtivity=(TripAvtivity)getActivity();
                tripAvtivity.chooseFragment(5);
                tripAvtivity.isChoose=1;
                //replaceCircle(new CircleFragment());
                break;
            case R.id.undraw_doll_play:
                TripAvtivity tripAvtivity2=(TripAvtivity)getActivity();
                tripAvtivity2.chooseFragment(6);
                tripAvtivity2.isChoose=2;
                //replaceCircle(new CircleFragment());
                break;
        }
    }

    public void replaceCircle(Fragment fragment){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content,fragment);
        transaction.commit();

    }

}
