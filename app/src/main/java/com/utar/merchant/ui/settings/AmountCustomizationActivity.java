package com.utar.merchant.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utar.merchant.R;
import com.utar.merchant.ui.NumberKeyboard;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AmountCustomizationActivity extends AppCompatActivity implements View.OnClickListener{

    private List<TextView> textViewList;
    Gson gson = new Gson();
    Type type = new TypeToken<ArrayList<Double>>() {}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_customization);


        textViewList = new ArrayList<>();
        textViewList.add(findViewById(R.id.tv_amount_1));
        textViewList.add(findViewById(R.id.tv_amount_2));
        textViewList.add(findViewById(R.id.tv_amount_3));
        textViewList.add(findViewById(R.id.tv_amount_4));
        textViewList.add(findViewById(R.id.tv_amount_5));

        for(int i =0;i<textViewList.size();i++){
            textViewList.get(i).setOnClickListener(this::onClick);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("amount_list",
                MODE_PRIVATE);

        if (!(sharedPreferences.getAll().size() > 0)) {
            String defValue = "[\"10.00\", \"20.00\", \"50.00\", \"100.00\", \"200.00\"]";
            saveAmountList(gson.fromJson(defValue, type));
        }

        String amountString = sharedPreferences.getString("amountList",
                "[\"10.00\", \"20.00\", \"50.00\", \"100.00\", \"200.00\"]");

        ArrayList<Double> amountList = gson.fromJson(amountString, type);
        Collections.sort(amountList);

        for (int i = 0; i < amountList.size(); i++) {
            textViewList.get(i).setText(String.format("%.2f", amountList.get(i)));
        }
    }

    private void saveAmountList(ArrayList<String> arrayList) {
        Gson gson = new Gson();
        String arrayListJson = gson.toJson(arrayList);

        SharedPreferences sharedPreferences = getSharedPreferences("amount_list",
                MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("amountList", arrayListJson);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String amount = "[\"" +
                textViewList.get(0).getText().toString() + "\", \"" +
                textViewList.get(1).getText().toString() + "\", \"" +
                textViewList.get(2).getText().toString() + "\", \"" +
                textViewList.get(3).getText().toString() + "\", \"" +
                textViewList.get(4).getText().toString() + "\"]";

        saveAmountList(gson.fromJson(amount, type));
    }

    @Override
    public void onClick(View v) {
        TextView view = null;
        switch(v.getId()){
            case R.id.tv_amount_1:{
                view = textViewList.get(0);
                break;
            }
            case R.id.tv_amount_2:{
                view = textViewList.get(1);
                break;
            }
            case R.id.tv_amount_3:{
                view = textViewList.get(2);
                break;
            }
            case R.id.tv_amount_4:{
                view = textViewList.get(3);
                break;
            }
            case R.id.tv_amount_5:{
                view = textViewList.get(4);
                break;
            }
        }
        new AmountChangeLayout(AmountCustomizationActivity.this, view, findViewById(R.id.ll_amount_custom));
    }
}