package com.utar.merchant.cardreader;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.MyApplication;
import com.utar.merchant.data.Account;

import java.util.Arrays;

public class AccountExportHCEService extends HostApduService {

    private final String TAG = "AccountExportHCEService";
    private final String MERCHANT_LOGIN_AID = "F888222444";

    private final byte[] CORRECT_RESPOND_APP = {(byte) 0x90, (byte)0x00};


    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        Log.i(TAG, "Received APDU: " + Nfc.ByteArrayToHexString(bytes));

        if(Arrays.equals(Nfc.BuildSelectApdu(MERCHANT_LOGIN_AID), bytes)){
            final String[] profile = new String[2];

            if(MyApplication.getInstance().getAccount().getEmail() != null){
                profile[0] = MyApplication.getInstance().getAccount().getEmail();
                profile[1] = MyApplication.getInstance().getAccount().getPassword();

                byte[] accountInfo = (profile[0] + ";" + profile[1]).getBytes();
                return accountInfo;
            }
            else{
                MyApplication.getInstance().firebaseUserUpdate();
                sendResponseApdu(new byte[1]);
            }


        }
        return null;
    }

    @Override
    public void onDeactivated(int i) {

    }



}
