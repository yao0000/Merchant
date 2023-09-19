package com.utar.merchant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.data.Account;
import com.utar.merchant.ui.home.HomeFragment;
import com.utar.merchant.ui.receive.ReceiveFragment;
import com.utar.merchant.ui.settings.SettingsFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //first page
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();

        checkDeviceID();
    }

    //Bottom Navigation Set up
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.home:
                    fragment = new HomeFragment();
                    break;

                case R.id.receive:
                    fragment = new ReceiveFragment();
                    break;
                case R.id.settings:
                    fragment = new SettingsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();
            return true;
        }
    };

    private void checkDeviceID(){
        if(FirebaseAuth.getInstance().getUid() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Account account = snapshot.getValue(Account.class);
                        if(account == null) return;

                        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        if(!account.getDeviceId().equals(deviceId)){
                            FirebaseAuth.getInstance().signOut();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getUid() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}