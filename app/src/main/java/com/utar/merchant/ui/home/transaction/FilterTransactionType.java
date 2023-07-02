package com.utar.merchant.ui.home.transaction;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupWindow;

import com.utar.merchant.R;

import java.util.ArrayList;
import java.util.List;

public class FilterTransactionType implements View.OnClickListener{

    private Context context;

    private PopupWindow popupWindow, filterWindows;
    private View view;
    private List<CheckBox> checkBoxList;

    public FilterTransactionType(Context context, PopupWindow filterWindows){
        this.context = context;
        this.filterWindows = filterWindows;
        this.checkBoxList = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_transaction_type, null);
        checkBoxList.add(view.findViewById(R.id.cb_reload));
        checkBoxList.add(view.findViewById(R.id.cb_withdraw));
        checkBoxList.add(view.findViewById(R.id.cb_payment_receive));

        view.findViewById(R.id.backBtn).setOnClickListener(this::onClick);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this::onClick);
        view.findViewById(R.id.btn_reset).setOnClickListener(this::onClick);
        view.findViewById(R.id.btn_done).setOnClickListener(this::onClick);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn: {
                popupWindow.dismiss();
                break;
            }
            case R.id.btn_cancel: {
                popupWindow.dismiss();
                filterWindows.dismiss();
                break;
            }
            case R.id.btn_reset: {
                for (int i = 0; i < checkBoxList.size(); i++) {
                    checkBoxList.get(i).setChecked(false);
                }
                break;
            }
            case R.id.btn_done: {

            }
        }
    }
}
