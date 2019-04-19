package com.example.dell.tripsafety.fragment;



import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.dell.tripsafety.AddUserActivity;
import com.example.dell.tripsafety.Protect.ProtectActivity;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.listenpowerbutton.listenPowerKeyService;
import com.example.dell.tripsafety.user_profile.UserProfileSettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    @BindView(R.id.setting_num)
    TextView setting_num;
    @BindView(R.id.setting_user)
    TextView setting_user;
    @BindView(R.id.head)
    CircleImageView head_image;
    @BindView(R.id.card_Contact)
    CardView card_Contact;
    @BindView(R.id.card_Protect)
    CardView card_Protect;

    @BindView(R.id.three_switch)
    SwitchCompat threeSwitchCompat;



    public static SettingFragment newInstance(){

        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;

    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);//绑定framgent
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String currentUsername = AVUser.getCurrentUser().getUsername();
        setting_user.setText(currentUsername);

        String currentTelenumber=AVUser.getCurrentUser().getMobilePhoneNumber();
        setting_num.setText(currentTelenumber);
        head_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), UserProfileSettingActivity.class);
                startActivity(intent);
            }
        });
        card_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddUserActivity.class);
                startActivity(intent);
            }
        });

        card_Protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(getActivity(), ProtectActivity.class);
                startActivity(intent);
            }
        });


        SharedPreferences preference=getActivity().getSharedPreferences("oneKey",getActivity().MODE_PRIVATE);
        if(preference.getString("threeKey","off").equals("on"))
        {
            threeSwitchCompat.setChecked(true);
        }

        threeSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId())
                {
                    case R.id.three_switch:
                        if(isChecked){
                            //获取短信权限
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions( getActivity(),new  String[] {Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE},2);
                            }
                            //记住状态
                            SharedPreferences.Editor editor=getActivity().getSharedPreferences("oneKey",getActivity().MODE_PRIVATE).edit();
                            editor.putString("threeKey","on");
                            editor.apply();
                            Intent i=new Intent(getActivity(),listenPowerKeyService.class);
                            getActivity().startService(i);
                        }
                        else {


                            SharedPreferences.Editor editor=getActivity().getSharedPreferences("oneKey",getActivity().MODE_PRIVATE).edit();
                            editor.putString("threeKey","off");
                            editor.apply();
                        }
                        break;
                    default:
                        break;

                }
            }
        });

    }



}
