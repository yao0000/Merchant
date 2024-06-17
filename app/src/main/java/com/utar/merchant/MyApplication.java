package com.utar.merchant;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.data.Account;

public class MyApplication extends Application {

    private static MyApplication myApplication;
    private static final String TAG = "MyApplication";

    //for screen size
    private int displayHeight, displayWidth;
    private Account account;

    @Override
    public void onCreate() {
        super.onCreate();

        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this,
                        "com.utar.merchant.cardreader.AccountExportHCEService"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        myApplication = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        displayHeight = getResources().getDisplayMetrics().heightPixels;
        displayWidth = getResources().getDisplayMetrics().widthPixels;

        firebaseUserUpdate();

    }

    public static MyApplication getInstance(){
        return myApplication;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void firebaseUserUpdate(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Log.e(TAG, "No user login");
            return;
        }

        FirebaseDatabase.getInstance().getReference("user")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        account = snapshot.getValue(Account.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "firebase user update error: " + error.getMessage());
                    }
                });
    }

    public Account getAccount(){
        return account;
    }

}