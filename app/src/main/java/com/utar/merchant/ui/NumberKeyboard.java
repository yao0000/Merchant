package com.utar.merchant.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.utar.merchant.R;

public class NumberKeyboard implements View.OnClickListener{

    private Context context;
    private PopupWindow popupWindow;
    private View popupView;
    private EditText editText;

    public NumberKeyboard(Context context, EditText et){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.buttons_layout, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        editText = et;
    }

    public void show(){
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0,0);
    }


    @Override
    public void onClick(View v) {

    }
}
