package com.utar.merchant.ui.settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.utar.merchant.Login;
import com.utar.merchant.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        String[] languages = {"English", "中文"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, languages);

        findViewById(R.id.backBtn).setOnClickListener(e -> onBackPressed());

        ListView lv_language = findViewById(R.id.lv_language_list);
        lv_language.setAdapter(adapter);

        lv_language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lang = "";
                Log.i("Lang", String.valueOf(position));
                if(position == 0){
                    lang = "en";
                }
                else if(position == 1){
                    lang = "zh";
                }

                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                recreate();

                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", lang);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}