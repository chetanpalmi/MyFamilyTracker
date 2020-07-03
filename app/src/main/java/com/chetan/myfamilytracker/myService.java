package com.chetan.myfamilytracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myService extends IntentService {
    public static boolean IsRunning=false;
    DatabaseReference mydb;

    public myService() {
        super("myService");
        IsRunning=true;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mydb=FirebaseDatabase.getInstance().getReference();
        mydb.child("Users").child(GlobalInfo.PhoneNumber).child("Updates")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Location location=tracklocation.location;
                        mydb.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                                .child("lat").setValue(tracklocation.location.getLatitude());
                        mydb.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                                .child("lag").setValue(tracklocation.location.getLongitude());

                        DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                        Date date=new Date();
                        mydb.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                                .child("LastOnlineDate")
                                .setValue(df.format(date).toString());
                        Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
