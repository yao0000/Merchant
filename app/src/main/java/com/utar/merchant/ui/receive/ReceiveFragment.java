package com.utar.merchant.ui.receive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utar.merchant.R;
import com.utar.merchant.ui.settings.AmountCustomizationActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ReceiveFragment extends Fragment implements View.OnClickListener {

    private TextView resultTxtView;
    private List<Button> btn_amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_receive, container, false);
        resultTxtView = v.findViewById(R.id.out);
        btn_amount = new ArrayList<>();
        btn_amount.add(v.findViewById(R.id.btn_amount_1));
        btn_amount.add(v.findViewById(R.id.btn_amount_2));
        btn_amount.add(v.findViewById(R.id.btn_amount_3));
        btn_amount.add(v.findViewById(R.id.btn_amount_4));
        btn_amount.add(v.findViewById(R.id.btn_amount_5));
        for (int i = 0; i < btn_amount.size(); i++) {
            btn_amount.get(i).setOnClickListener(this::onClick);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                String exp = resultTxtView.getText().toString();

                if (R.id.enter == id) {
                    if (resultTxtView.length() == 0) {
                        Toast.makeText(getContext(), getString(R.string.require_amount), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
                    if (nfcAdapter.isEnabled()) {
                        SharedPreferences pref = getActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = pref.edit();

                        prefEditor.putString("amount", String.valueOf(resultTxtView.getText()));
                        prefEditor.commit();

                        startActivity(new Intent(getContext(), ReceiveActivity.class));
                        getActivity().finish();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.nfc_disable_alert))
                                .setMessage(getString(R.string.nfc_enable_alert))
                                .setPositiveButton(getString(R.string.proceed_to_enable), (dialog, which) ->
                                        startActivity(new Intent("android.settings.NFC_SETTINGS")))
                                .setNegativeButton(getString(R.string.cancel), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

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

        v.findViewById(R.id.iv_amount_editor).setOnClickListener(e ->
                startActivity(new Intent(getContext(), AmountCustomizationActivity.class)));

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("amount_list", Context.MODE_PRIVATE);

        String amountString = sharedPreferences.getString("amountList",
                "[\"10.00\", \"20.00\", \"50.00\", \"100.00\", \"200.00\"]");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList <Double>>() {}.getType();

        ArrayList<Double> amountList = gson.fromJson(amountString, type);
        Collections.sort(amountList);
        for(int i = 0; i < btn_amount.size(); i++){
            btn_amount.get(i).setText("RM " + String.format("%.2f",amountList.get(i)));
        }
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void generateExpression(String val) {
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

    public boolean isSpecialChar(String str) {
        return Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]").matcher(str).find();
    }

    @Override
    public void onClick(View v) {
        View view = null;
        switch (v.getId()) {
            case R.id.btn_amount_1: {
                view = btn_amount.get(0);
                break;
            }
            case R.id.btn_amount_2: {
                view = btn_amount.get(1);
                break;
            }
            case R.id.btn_amount_3: {
                view = btn_amount.get(2);
                break;
            }
            case R.id.btn_amount_4: {
                view = btn_amount.get(3);
                break;
            }
            case R.id.btn_amount_5: {
                view = btn_amount.get(4);
                break;
            }
        }

        String amount = ((Button) view).getText().toString();
        resultTxtView.setText(amount.substring(3));
    }
}