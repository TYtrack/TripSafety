package com.example.dell.tripsafety.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.example.dell.tripsafety.R;
import com.example.dell.tripsafety.TripAvtivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.cv_add)
    CardView cvAdd;
    @BindView(R.id.et_username)
    EditText user_edit;
    @BindView(R.id.et_telephone)
    EditText tele_edit;
    @BindView(R.id.et_pin)
    EditText pin_edit;
    @BindView(R.id.et_password)
    EditText pass_edit;
    @BindView(R.id.bt_go)
    Button next_Button;
    @BindView(R.id.btn_pin_register)
    FancyButton pin_Button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("Register","done");
            ShowEnterAnimation();
        }
        fab.setOnClickListener(this);
        pin_Button.setOnClickListener(this);
        next_Button.setOnClickListener(this);




    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(200);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(200);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                animateRevealClose();
                break;
            case R.id.btn_pin_register:
                sendTelephone();
                break;
            case R.id.bt_go:
                sendPin();
                break;




        }
    }

    //发送手机号码，获取验证码
    public void sendTelephone(){
        String Telephone=tele_edit.getText().toString();
        AVOSCloud.requestSMSCodeInBackground(Telephone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                // 发送失败可以查看 e 里面提供的信息
            }
        });
    }

    public void sendPin(){
        final String Username=user_edit.getText().toString();
        final String Telephone=tele_edit.getText().toString();
        final String Pin=pin_edit.getText().toString();
        final String Password=pass_edit.getText().toString();

        AVUser.signUpOrLoginByMobilePhoneInBackground(Telephone, Pin, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {

                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {

                        AVUser.getCurrentUser().put("username", Username);
                        AVUser.getCurrentUser().put("password", Password);

                        AVUser.getCurrentUser().saveInBackground();
                        Toasty.success(RegisterActivity.this, "注册成功 !", Toast.LENGTH_SHORT, true).show();

                        Intent intent=new Intent(RegisterActivity.this, TripAvtivity.class);
                        startActivity(intent);

                    }
                });
                // 如果 e 为空就可以表示登录成功了，并且 user 是一个全新的用户
            }
        });
    }


    public void submit(){
        // 测试 SDK 是否正常工作的代码
        AVObject testObject = new AVObject("User_info");
        String Username=user_edit.getText().toString();
        String Password=pass_edit.getText().toString();

        testObject.put("Username",Username);
        testObject.put("Password",Password);
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Toasty.success(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT, true).show();

                    Log.e("saved","success!");
                }else {
                    Toasty.error(RegisterActivity.this, "请检查网络环境", Toast.LENGTH_SHORT, true).show();

                }
            }
        });
    }


}
