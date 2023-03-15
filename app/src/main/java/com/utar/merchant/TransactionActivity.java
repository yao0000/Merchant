package com.utar.merchant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<Transaction> transactionList = new ArrayList<>(), searchList;
    private int displayHeight, displayWidth;
    private int blue;

    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private TextView tv_startDate, tv_endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Transaction History");

        blue = getResources().getColor(R.color.soft_blue);
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
                    setView(showList(transactionList));

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



    private void setView(View view){

        initDatePicker();
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        tv_startDate = findViewById(R.id.startDate);
        ((ViewGroup)tv_startDate.getParent()).removeView(tv_startDate);
        tv_startDate.setTextSize(displayWidth * displayHeight / 100000 - 5);
        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        tv_endDate = findViewById(R.id.endDate);
        ((ViewGroup)tv_endDate.getParent()).removeView(tv_endDate);
        tv_endDate.setTextSize(displayWidth * displayHeight / 100000 - 5);
        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        LinearLayout searchLayoutTop = new LinearLayout(this);
        searchLayoutTop.setOrientation(LinearLayout.HORIZONTAL);
        searchLayoutTop.setGravity(Gravity.CENTER);
        searchLayoutTop.setBackgroundColor(getResources().getColor(R.color.white_grey));

        TextView tv_to = new TextView(this);
        tv_to.setText(" to ");
        tv_to.setTextSize(displayWidth * displayHeight / 100000 - 5);
        searchLayoutTop.addView(tv_startDate);
        searchLayoutTop.addView(tv_to);
        searchLayoutTop.addView(tv_endDate);

        TextView tv_search = new TextView(this);
        tv_search.setText("SEARCH");
        tv_search.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tv_search.setTextColor(Color.BLUE);
        tv_search.setTextSize(displayWidth * displayHeight / 100000 - 5);
        tv_search.setGravity(Gravity.RIGHT);
        tv_search.setPadding(0,0,(int)(displayWidth * 0.05),0);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = String.valueOf(tv_startDate.getText());
                String endDate = String.valueOf(tv_endDate.getText());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ss");
                startDate += " 00:00:00";
                endDate += " 23:59:59";
                searchList = new ArrayList<>();
                try {
                    long startTime = simpleDateFormat.parse(startDate).getTime();
                    long endTime = simpleDateFormat.parse(endDate).getTime();
                    if(startTime > endTime){
                        tv_startDate.setError("Invalid Date selected");
                        return;
                    }
                    for(int i = transactionList.size()-1; i >= 0; i--){
                        if(isInRange(transactionList.get(i), startTime, endTime)){
                            searchList.add(transactionList.get(i));
                        }

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(searchList.size() == 0){
                    toast("No record found!");
                }

                setView(showList(searchList));
            }
        });

        TextView tv_reset = new TextView(this);
        tv_reset.setText("RESET");
        tv_reset.setTextColor(Color.BLUE);
        tv_reset.setTextSize(displayWidth * displayHeight / 100000 - 3);
        tv_reset.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tv_reset.setPadding((int)(displayWidth * 0.05),0, 0,0);
        tv_reset.setGravity(Gravity.LEFT);
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(showList(transactionList));
            }
        });

        LinearLayout searchLayoutBottom = new LinearLayout(this);
        searchLayoutBottom.setBackgroundColor(getResources().getColor(R.color.white_grey));
        searchLayoutBottom.setOrientation(LinearLayout.HORIZONTAL);

        searchLayoutBottom.addView(tv_reset);


        LinearLayout searchLayoutBottom2 = new LinearLayout(searchLayoutBottom.getContext());
        searchLayoutBottom2.setOrientation(LinearLayout.HORIZONTAL);
        searchLayoutBottom2.setGravity(Gravity.RIGHT);

        searchLayoutBottom2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        searchLayoutBottom2.addView(tv_search);
        searchLayoutBottom2.setBackgroundColor(getResources().getColor(R.color.white_grey));

        searchLayoutBottom.addView(searchLayoutBottom2);

        mainLayout.addView(searchLayoutTop);
        mainLayout.addView(searchLayoutBottom);

        View separator = new View(this);
        separator.setBackgroundColor(Color.BLACK);
        separator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (displayHeight * 0.005)));
        separator.setPadding(0, 10, 0, 10);
        mainLayout.addView(separator);

        mainLayout.addView(view);

        mainLayout.setPadding((int)(displayWidth * 0.05), (int)(displayHeight * 0.01),(int)(displayWidth * 0.05), (int)(displayHeight * 0.01));

        setContentView(mainLayout);
    }

    private boolean isInRange(Transaction transaction, long startTime, long endTime){
        return ((transaction.getTimestamp() > startTime) && (transaction.getTimestamp() < endTime));
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                tv_startDate.setText(date);
            }
        };

        DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                tv_endDate.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        startDatePickerDialog = new DatePickerDialog(this, style, startDateSetListener, year, month, day);
        endDatePickerDialog = new DatePickerDialog(this, style, endDateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "Jan";
        if(month == 2)
            return "Feb";
        if(month == 3)
            return "Mar";
        if(month == 4)
            return "Apr";
        if(month == 5)
            return "May";
        if(month == 6)
            return "Jun";
        if(month == 7)
            return "Jul";
        if(month == 8)
            return "Aug";
        if(month == 9)
            return "Sep";
        if(month == 10)
            return "Oct";
        if(month == 11)
            return "Nov";
        if(month == 12)
            return "Dec";

        //default should never happen
        return "JAN";
    }

    // To display the payment history
    private View showList(List<Transaction> list){
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
            wholeTransaction.setPadding(0,0,0,0);

        }
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(wholeTransaction);

        //scrollView.setPadding((int)(displayWidth * 0.05), (int)(displayHeight * 0.01),0, (int)(displayHeight * 0.01));
        //setContentView(scrollView);
        return scrollView;
    }

}