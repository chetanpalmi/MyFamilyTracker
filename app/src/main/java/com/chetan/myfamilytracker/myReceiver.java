package com.chetan.myfamilytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class myReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            GlobalInfo info=new GlobalInfo(context);
            info.LoadData();
            if (!tracklocation.IsRunning){
                tracklocation trackloc=new tracklocation();
                LocationManager lm=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,trackloc);
            }
            if (!myService.IsRunning){
                Intent intent1=new Intent(context,myService.class);
                context.startService(intent);
            }
        }
    }
}
