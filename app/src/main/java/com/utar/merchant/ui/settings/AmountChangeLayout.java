package com.utar.merchant.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.utar.merchant.R;

import java.util.regex.Pattern;

public class AmountChangeLayout {
    private TextView resultTxtView;
    private TextView textView;
    private Context context;
    private View v;
    private PopupWindow popupWindow;
    private LinearLayout mainLayout;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            String exp = resultTxtView.getText().toString();

            if (R.id.enter == id) {
                if (resultTxtView.length() == 0) {
                    Toast.makeText(context, context.getString(R.string.require_amount), Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(resultTxtView.getText().toString());
                textView.setText(String.format("%.2f", amount));
                popupWindow.dismiss();

            } else if (R.id.zero == id) {
                generateExpression("0");
            } else if (R.id.one == id) {
                generateExpression("1");
            } else if (R.id.two == id) {
                generateExpression("2");
            } else if (R.id.three == id) {
                generateExpression("3");
            } else if (R.id.four == id) {
                generateExpression("4");
            } else if (R.id.five == id) {
                generateExpression("5");
            } else if (R.id.six == id) {
                generateExpression("6");
            } else if (R.id.seven == id) {
                generateExpression("7");
            } else if (R.id.eight == id) {
                generateExpression("8");
            } else if (R.id.nine == id) {
                generateExpression("9");
            } else if (R.id.multiply == id) {
                generateExpression("*");
            } else if (R.id.clear == id) {
                //expTxtView.setText("");
                resultTxtView.setText("");
            } else if (R.id.backspace == id) {
                int expLength = exp.length();
                if (!(expLength < 1)) {
                    if (expLength == 1) {
                        resultTxtView.setText("");
                    } else {
                        resultTxtView.setText(exp.substring(0, expLength - 1));
                    }
                }
            } else if (R.id.dot == id) {
                generateExpression(".");
            }
        }
    };

    public AmountChangeLayout(Context context, TextView textView, LinearLayout mainLayout) {
        this.context = context;
        this.textView = textView;
        this.mainLayout = mainLayout;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layout_amount_customize, null);
        resultTxtView = v.findViewById(R.id.out);
        v.findViewById(R.id.zero).setOnClickListener(listener);
        v.findViewById(R.id.one).setOnClickListener(listener);
        v.findViewById(R.id.two).setOnClickListener(listener);
        v.findViewById(R.id.three).setOnClickListener(listener);
        v.findViewById(R.id.four).setOnClickListener(listener);
        v.findViewById(R.id.five).setOnClickListener(listener);
        v.findViewById(R.id.six).setOnClickListener(listener);
        v.findViewById(R.id.seven).setOnClickListener(listener);
        v.findViewById(R.id.eight).setOnClickListener(listener);
        v.findViewById(R.id.nine).setOnClickListener(listener);

        v.findViewById(R.id.dot).setOnClickListener(listener);
        v.findViewById(R.id.clear).setOnClickListener(listener);
        v.findViewById(R.id.backspace).setOnClickListener(listener);
        v.findViewById(R.id.enter).setOnClickListener(listener);

        resultTxtView.setText(textView.getText().toString());
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        mainLayout.setBackgroundColor(Color.GRAY);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mainLayout.setBackgroundColor(Color.WHITE);
            }
        });
    }

    private void generateExpression(String val) {
        String exp = resultTxtView.getText().toString();
        if (isSpecialChar(val)) {
            if (exp.equals("") || isSpecialChar("" + exp.charAt(exp.length() - 1))) {
                return;
            }
        }
        String previousAmount = String.valueOf(resultTxtView.getText());
        if (val.equals(".")) {
            for (int i = 0; i < previousAmount.length(); i++) {
                if (previousAmount.charAt(i) == '.') {
                    return;
                }
            }
        }

        for (int i = 0; i < previousAmount.length(); i++) {
            if (previousAmount.charAt(i) == '.') {
                if (previousAmount.length() - 1 == (i + 2)) {
                    return;
                }
            }
        }

        resultTxtView.setText(String.format("%s%s", resultTxtView.getText().toString(), val));
    }

    private boolean isSpecialChar(String str) {
        return Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]").matcher(str).find();
    }
}
