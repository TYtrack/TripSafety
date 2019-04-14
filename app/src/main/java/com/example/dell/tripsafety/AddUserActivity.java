package com.example.dell.tripsafety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.example.dell.tripsafety.Contact.editContacts;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class AddUserActivity extends AppCompatActivity {


    @BindView(R.id.no_1_contact)
    LinearLayout contact_layout_1;
    @BindView(R.id.no_2_contact)
    LinearLayout contact_layout_2;
    @BindView(R.id.no_3_contact)
    LinearLayout contact_layout_3;

    TextView text1;
    TextView text2;
    TextView text3;

    TextView contact_text_1;
    TextView contact_text_2;
    TextView contact_text_3;

    FancyButton add_Button_1;
    FancyButton add_Button_2;
    FancyButton add_Button_3;

    TextView nickname1;
    TextView nickname2;
    TextView nickname3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        ButterKnife.bind( this ) ;
        setText();
    }

    public void setText(){
        add_Button_1=(FancyButton)contact_layout_1.findViewById(R.id.btn_add_contact);
        add_Button_2=(FancyButton)contact_layout_2.findViewById(R.id.btn_add_contact);
        add_Button_3=(FancyButton)contact_layout_3.findViewById(R.id.btn_add_contact);

        add_Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddUserActivity.this,editContacts.class);
                intent.putExtra("num",1);

                startActivity(intent);
                finish();
            }
        });
        add_Button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddUserActivity.this,editContacts.class);
                intent.putExtra("num",2);
                startActivity(intent);
                finish();
            }
        });
        add_Button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddUserActivity.this,editContacts.class);
                intent.putExtra("num",3);
                startActivity(intent);
                finish();

            }
        });

        ((TextView) contact_layout_1.findViewById(R.id.default_text_content)).setText("第一紧急联系人：");
        ((TextView) contact_layout_2.findViewById(R.id.default_text_content)).setText("第二紧急联系人：");
        ((TextView) contact_layout_3.findViewById(R.id.default_text_content)).setText("第三紧急联系人：");



        nickname1=(TextView) contact_layout_1.findViewById(R.id.contact_nickname);
        nickname2=(TextView) contact_layout_2.findViewById(R.id.contact_nickname);
        nickname3=(TextView) contact_layout_3.findViewById(R.id.contact_nickname);

        getNickname();

    }

    public void getNickname(){
        AVQuery<AVObject> avQuery = new AVQuery<>("Contact");
        avQuery.whereEqualTo("user_number", AVUser.getCurrentUser().getMobilePhoneNumber());

        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size()==0)
                    return;
                AVObject Ao1=list.get(0);
                if (Ao1.get("Contact_1_nick")!=null)
                {
                    nickname1.setText(Ao1.get("Contact_1_nick").toString());
                }
                if (Ao1.get("Contact_2_nick")!=null)
                {
                    nickname2.setText(Ao1.get("Contact_2_nick").toString());
                }
                if (Ao1.get("Contact_3_nick")!=null)
                {
                    nickname3.setText(Ao1.get("Contact_3_nick").toString());
                }

            }
        });
    }


}
