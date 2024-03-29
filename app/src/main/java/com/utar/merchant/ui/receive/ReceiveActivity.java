package com.utar.merchant.ui.receive;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;

import android.nfc.NfcAdapter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.utar.merchant.MainActivity;
import com.utar.merchant.R;
import com.utar.merchant.cardreader.HCECardReader;


public class ReceiveActivity extends AppCompatActivity implements HCECardReader.AccountCallback {
    private HCECardReader hceCardReader;
    private final static String TAG = "ReceiveActivity";

    LottieAnimationView lottieAnimationView;
    private CountDownTimer resetTimer;

    Button btn_cancel;
    TextView tv_receive_status, tv_receive_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        hceCardReader = new HCECardReader(this);

        lottieAnimationView = findViewById(R.id.receive_animation);
        tv_receive_status = findViewById(R.id.receive_status);
        tv_receive_amount = findViewById(R.id.receive_amount);
        tv_receive_amount.setText(String.format(getString(R.string.receiving) + ": RM%.2f", getAmount()));
        btn_cancel = findViewById(R.id.nfc_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReceiveActivity.this, MainActivity.class));
                finish();
            }
        });

        resetTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Log.i("Reset", "yes");
                lottieAnimationView.setAnimation(R.raw.nfc_scan_j);
                lottieAnimationView.loop(true);
                lottieAnimationView.playAnimation();

                tv_receive_status.setText(getString(R.string.waiting_tag));
            }
        };
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");

        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, hceCardReader, READER_FLAGS, null);
        }
    }
    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");

        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.disableReaderMode(this);
        }
    }

    @Override
    public void setStatusText(int id) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_receive_status.setText(getString(id));

            }
        });
    }

    @Override
    public void setAnimation(int rawRes, boolean repeat) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lottieAnimationView.setAnimation(rawRes);
                lottieAnimationView.loop(repeat);
                lottieAnimationView.playAnimation();

            }
        });
    }

    @Override
    public void countDownFinish() {

        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(ReceiveActivity.this, MainActivity.class));
                finish();
            }
        };

        countDownTimer.start();
    }

    @Override
    public double getAmount() {
        SharedPreferences pref = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String strAmount = pref.getString("amount", "NA");

        if (strAmount.equals("NA")) {
            Log.e(TAG, "strAmount from shared preferences is null value");
            return -1;
        } else {
            //Log.i(TAG, "strAmount: " + strAmount);
            return Double.parseDouble(strAmount);
        }
    }

    @Override
    public void countDownReset() {
        resetTimer.cancel();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                resetTimer.start();

            }
        });
    }
}