package com.chetan.myfamilytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;

public class login extends AppCompatActivity {

    EditText EDT;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        EDT=(EditText)findViewById(R.id.EDTNumber);
    }

    public void BuNext(View view) {
        GlobalInfo.PhoneNumber=GlobalInfo.FormatPhoneNumber(EDT.getText().toString());
        GlobalInfo.UpdateInfo(GlobalInfo.PhoneNumber);
        finish();
        Intent intent=new Intent(this,MyTracker.class);
        startActivity(intent);
    }

}
