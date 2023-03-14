package com.utar.merchant;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.data.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<Transaction> transactionList = new ArrayList<>();
    private int displayHeight, displayWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayHeight = getResources().getDisplayMetrics().heightPixels;
        displayWidth = getResources().getDisplayMetrics().widthPixels;

        String userID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("user").child(userID).keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userID).child("transactions");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    transactionList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        transactionList.add(transaction);
                    }
                    toast("Transaction histories sync done");
                    showList(transactionList);

                }
                else {
                    toast("No record found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                toast(error.getMessage());
            }
        });

    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // To display the payment history
    private void showList(List<Transaction> list){
        TextView tv_name, tv_amount, tv_time, tv_type;

        LinearLayout wholeTransaction = new LinearLayout(this);
        wholeTransaction.setOrientation(LinearLayout.VERTICAL);
        wholeTransaction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GridLayout gl;
        for(int i = list.size() - 1; i >= 0; i--){
            Transaction transaction = list.get(i);

            //for single transaction panel display
            LinearLayout singleTransaction = new LinearLayout(this);
            singleTransaction.setOrientation(LinearLayout.HORIZONTAL);
            singleTransaction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            gl = new GridLayout(this);
            gl.setOrientation(GridLayout.HORIZONTAL);
            gl.setRowCount(1);
            gl.setColumnCount(2);



            // text view name
            tv_name = new TextView(this);
            tv_name.setText(transaction.getObjectName());
            tv_name.setTextSize(displayWidth * displayHeight / 100000 - 4);
            tv_name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv_name.setTextColor(Color.BLACK);
            tv_name.setLayoutParams(new ViewGroup.LayoutParams((int) (displayWidth * 0.6), ViewGroup.LayoutParams.WRAP_CONTENT));
            tv_name.setGravity(Gravity.LEFT);

            tv_amount = new TextView(this);
            tv_amount.setTextSize(displayWidth * displayHeight / 100000 - 4);
            tv_amount.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv_amount.setLayoutParams(new ViewGroup.LayoutParams((int) (displayWidth * 0.3), ViewGroup.LayoutParams.MATCH_PARENT));

            if(transaction.getType().equals(Transaction.PAYMENT) || transaction.getType().equals(Transaction.TRANSFER_OUT)){
                tv_amount.setText(String.format("- RM%.2f", transaction.getAmount()));
                tv_amount.setTextColor(getResources().getColor(R.color.dark_red));
            }
            else{
                tv_amount.setText(String.format("RM%.2f",transaction.getAmount()));
                tv_amount.setTextColor(getResources().getColor(R.color.dark_green));
            }

            tv_amount.setGravity(Gravity.RIGHT);

            // Left Side Display
            LinearLayout leftSide = new LinearLayout(this);
            leftSide.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams((int) (displayWidth * 0.4), LinearLayout.LayoutParams.WRAP_CONTENT);
            leftParams.setMargins(0, 15, 0, 15);
            leftParams.weight = 1;
            leftSide.setLayoutParams(leftParams);

            //text view type
            tv_time = new TextView(this);
            tv_time.setText(transaction.getTime());
            tv_time.setTextSize(displayWidth * displayHeight / 100000 - 8);
            tv_time.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);

            tv_time.setTextColor(Color.BLACK);
            tv_time.setGravity(Gravity.BOTTOM);

            // Right Side Display (Name and type)
            LinearLayout rightSide = new LinearLayout(this);
            rightSide.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams((int) (singleTransaction.getResources().getDisplayMetrics().widthPixels * 0.5), LinearLayout.LayoutParams.MATCH_PARENT);
            //rightParams.gravity = Gravity.RIGHT;
            rightParams.setMargins(0, 15, 0, 15);
            rightParams.weight = 2;
            rightSide.setLayoutParams(leftParams);

            tv_type = new TextView(this);
            tv_type.setText(transaction.getType());
            tv_type.setTextSize(displayWidth * displayHeight / 100000 - 8);
            tv_type.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            tv_type.setTextColor(Color.BLACK);
            tv_type.setGravity(Gravity.RIGHT);

            gl.addView(tv_name);
            gl.addView(tv_amount);
            leftSide.addView(tv_time);
            rightSide.addView(tv_type);

            LinearLayout bottomLayout = new LinearLayout(this);
            bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
            bottomLayout.addView(leftSide);
            bottomLayout.addView(rightSide);

            wholeTransaction.addView(gl);
            wholeTransaction.addView(bottomLayout);

            View separator = new View(this);
            separator.setBackgroundColor(Color.BLACK);
            separator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (displayHeight * 0.001)));
            wholeTransaction.addView(separator);
            wholeTransaction.setPadding(0,0,(int)(displayWidth * 0.05),0);

        }
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(wholeTransaction);

        scrollView.setPadding((int)(displayWidth * 0.05), (int)(displayHeight * 0.01),0, (int)(displayHeight * 0.01));
        setContentView(scrollView);
    }

}