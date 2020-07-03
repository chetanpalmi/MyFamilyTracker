package com.chetan.myfamilytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<AdapterItems> contactslsting=new ArrayList<AdapterItems>();
    MyCustom myc;
    DatabaseReference mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalInfo info=new GlobalInfo(this);
        info.LoadData();
        mydb= FirebaseDatabase.getInstance().getReference();
        CheckUserPermsions();
        myc=new MyCustom(contactslsting);
        ListView lv=(ListView)findViewById(R.id.trackers);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterItems adapterItems=contactslsting.get(position);
                GlobalInfo.UpdateInfo(adapterItems.PhoneNumber);
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("PhoneNumber",adapterItems.PhoneNumber);
                startActivity(intent);
            }
        });
        lv.setAdapter(myc);

    }

    @Override
    public void onResume(){
        super.onResume();
        Refresh();
    }
    void Refresh(){
        contactslsting.clear();
        mydb.child("Users").child(GlobalInfo.PhoneNumber).
                child("Finders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                contactslsting.clear();
                if (td == null)  //no one allow you to find him
                {
                    contactslsting.add(new AdapterItems("NoTicket", "no_desc"));
                    myc.notifyDataSetChanged();
                    return;
                }

                ArrayList<AdapterItems> list_contact = new ArrayList<AdapterItems>();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list_contact.add(new AdapterItems(  name,GlobalInfo.FormatPhoneNumber(phoneNumber)
                    ));


                }
                String tinfo;
                for (  String Numbers : td.keySet()) {
                    for (AdapterItems cs : list_contact) {

                        //IsFound = SettingSaved.WhoIFindIN.get(cs.Detals);  // for case who i could find list
                        if (cs.PhoneNumber.length() > 0)
                            if (Numbers.contains(cs.PhoneNumber)) {
                                contactslsting.add(new AdapterItems(cs.UserName, cs.PhoneNumber));
                                break;
                            }

                    }

                }
                myc.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        myc.notifyDataSetChanged();
    }
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
        StartServices();
    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartServices();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void StartServices(){
        if (!tracklocation.IsRunning){
            tracklocation trackloc=new tracklocation();
            LocationManager lm=(LocationManager)getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,trackloc);
        }
        if (!myService.IsRunning){
            Intent intent=new Intent(this,myService.class);
            startService(intent);
        }
    }

    public  boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.help:
                return true;

            case R.id.addtracker:
                Intent intent=new Intent(this,MyTracker.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyCustom extends BaseAdapter {
        ArrayList<AdapterItems> contactlist;
        MyCustom(ArrayList<AdapterItems> colist){
            this.contactlist=colist;
        }

        @Override
        public int getCount() {
            return contactlist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
           final AdapterItems s=contactlist.get(position);
            if (s.UserName.equals("NoTicket")){
                View myview=(View)inflater.inflate(R.layout.news_ticket_no_news,parent,false);
                return myview;
            }
            else {
                View myview=(View)inflater.inflate(R.layout.single_row_conact,parent,false);

                TextView tvuser = (TextView) myview.findViewById(R.id.tv_user_name);
                TextView tvphone = (TextView) myview.findViewById(R.id.tv_phone);
                tvuser.setText(s.UserName);
                tvphone.setText(s.PhoneNumber);

            return myview;}

        }
    }

}
