package com.example.dell.tripsafety.listenpowerbutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // Toast.makeText(context,"kaiji",Toast.LENGTH_LONG).show();
        SharedPreferences pref=context.getSharedPreferences("threeKey",MODE_PRIVATE);
        if(pref.getInt("threeKry",0)==1){
            Intent i=new Intent(context,listenPowerKeyService.class);
            context.startService(i);
        }

    }
}
