package com.utar.merchant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.utar.merchant.cardreader.HCECardReader;


public class ReceiveActivity extends AppCompatActivity {
    private final static String TAG = "ReceiveActivity";

    Button btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        btn_cancel = findViewById(R.id.nfc_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences pref = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String strAmount = pref.getString("amount", "NA");

        if(strAmount.equals("NA")){
            Log.e(TAG, "strAmount from shared preferences is null value");
        }
        else{
            Log.i(TAG, "strAmount: " + strAmount);
        }


    }
}