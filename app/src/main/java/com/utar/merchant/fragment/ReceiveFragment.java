package com.utar.merchant.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.utar.merchant.R;

import java.util.regex.Pattern;

public class ReceiveFragment extends Fragment {

  Button zeroBtn, oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn;
  Button dotBtn, clearBtn, backspaceBtn, enterBtn;
  private TextView resultTxtView;
  private TextView expTxtView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_receive, container, false);

    resultTxtView = v.findViewById(R.id.out);
    expTxtView = v.findViewById(R.id.exp);

    zeroBtn = v.findViewById(R.id.zero);
    oneBtn = v.findViewById(R.id.one);
    twoBtn = v.findViewById(R.id.two);
    threeBtn = v.findViewById(R.id.three);
    fourBtn = v.findViewById(R.id.four);
    fiveBtn = v.findViewById(R.id.five);
    sixBtn = v.findViewById(R.id.six);
    sevenBtn = v.findViewById(R.id.seven);
    eightBtn = v.findViewById(R.id.eight);
    nineBtn = v.findViewById(R.id.nine);

    dotBtn = v.findViewById(R.id.dot);
    clearBtn = v.findViewById(R.id.clear);
    backspaceBtn = v.findViewById(R.id.backspace);
    enterBtn = v.findViewById(R.id.enter);

    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int id = view.getId();
        String exp = resultTxtView.getText().toString();

        if (R.id.enter == id) {

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
          expTxtView.setText("");
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

    zeroBtn.setOnClickListener(listener);
    oneBtn.setOnClickListener(listener);
    twoBtn.setOnClickListener(listener);
    threeBtn.setOnClickListener(listener);
    fourBtn.setOnClickListener(listener);
    fiveBtn.setOnClickListener(listener);
    sixBtn.setOnClickListener(listener);
    sevenBtn.setOnClickListener(listener);
    eightBtn.setOnClickListener(listener);
    nineBtn.setOnClickListener(listener);

    dotBtn.setOnClickListener(listener);
    clearBtn.setOnClickListener(listener);
    backspaceBtn.setOnClickListener(listener);
    enterBtn.setOnClickListener(listener);

    // Inflate the layout for this fragment
    return v;
  }

  private void toast(String msg){
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
    if(val.equals(".")){
      for(int i = 0; i < previousAmount.length(); i++){
        if(previousAmount.charAt(i) == '.'){
          return;
        }
      }
    }

    for(int i = 0; i < previousAmount.length(); i++){
      if(previousAmount.charAt(i) == '.'){
        if(previousAmount.length()-1 == (i+2)){
          return;
        }
      }
    }

    resultTxtView.setText(String.format("%s%s", resultTxtView.getText().toString(), val));
  }

  public boolean isSpecialChar(String str) {
    return Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]").matcher(str).find();
  }
}