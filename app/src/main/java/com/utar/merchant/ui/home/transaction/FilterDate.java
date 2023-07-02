package com.utar.merchant.ui.home.transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.utar.merchant.data.Transaction;
import com.utar.merchant.ui.home.TransactionActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import com.utar.merchant.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FilterDate implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private Context context;
    private Activity activity;
    private View layout;
    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private AlertDialog dialog;
    private TextView tv_start_date, tv_end_date;

    public FilterDate(Context context) {
        this.context = context;
        activity = (Activity) context;
        initialization();
        pickerInitialization();
    }

    public void show() {
        dialog.show();
    }

    private void initialization() {
        layout = activity.getLayoutInflater().inflate(R.layout.layout_date_picker, null);

        tv_start_date = layout.findViewById(R.id.tv_start_date);
        tv_end_date = layout.findViewById(R.id.tv_end_date);

        layout.findViewById(R.id.date_tv_search).setOnClickListener(this::onClick);
        layout.findViewById(R.id.btn_start_date).setOnClickListener(this::onClick);
        layout.findViewById(R.id.btn_end_date).setOnClickListener(this::onClick);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(layout);
        dialog = alert.create();
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void pickerInitialization() {
        Calendar currentDate = Calendar.getInstance();

        startDatePickerDialog = DatePickerDialog.newInstance(this::onDateSet
                , currentDate.get(Calendar.YEAR)
                , currentDate.get(Calendar.MONTH)
                , currentDate.get(Calendar.DATE));

        endDatePickerDialog = DatePickerDialog.newInstance(this::onDateSet
                , currentDate.get(Calendar.YEAR)
                , currentDate.get(Calendar.MONTH)
                , currentDate.get(Calendar.DATE));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"
                , "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String selectedDate = dayOfMonth + " " + month[monthOfYear] + " " + year;

        if (view == startDatePickerDialog) {
            tv_start_date.setText(selectedDate);
        } else if (view == endDatePickerDialog) {
            tv_end_date.setText(selectedDate);
        }
    }

    private void search() {
        String startDateSelected = tv_start_date.getText().toString();
        String endDateSelected = tv_end_date.getText().toString();

        if (startDateSelected.equals(activity.getString(R.string.selectDate))
                || endDateSelected.equals(activity.getString(R.string.selectDate))) {
            toast(activity.getString(R.string.pleaseFillInDate));
            return;
        }

        startDateSelected += " 00:00:00";
        endDateSelected += " 23:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");

        try {
            long startTimestamp = simpleDateFormat.parse(startDateSelected).getTime();
            long endTimestamp = simpleDateFormat.parse(endDateSelected).getTime();

            if (startTimestamp > endTimestamp) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(activity.getString(R.string.alert));
                alertDialog.setMessage(activity.getString(R.string.date_duration_error));
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogOwn, int which) {
                                dialogOwn.dismiss();
                            }
                        });
                alertDialog.create().show();
                return;
            }

            TransactionActivity transactionActivity = TransactionActivity.getInstance();
            List<Transaction> transactionList = transactionActivity.getTransactionList();

            List<Transaction> searchList = new ArrayList<>();
            for (int i = transactionList.size() - 1; i >= 0; i--) {
                if (transactionActivity.isInRange(transactionList.get(i), startTimestamp, endTimestamp)) {
                    searchList.add(0, transactionList.get(i));
                }
            }

            if (searchList.size() == 0) {
                toast(activity.getResources().getString(R.string.noRecord));
                return;
            }

            transactionActivity.displayList(searchList);
            dialog.dismiss();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_tv_search: {
                search();
                break;
            }
            case R.id.btn_start_date: {
                startDatePickerDialog
                        .show(TransactionActivity.getInstance().getSupportFragmentManager()
                                , "startDatePicker");
                break;
            }
            case R.id.btn_end_date: {
                endDatePickerDialog
                        .show(TransactionActivity.getInstance().getSupportFragmentManager()
                                , "endDatePicker");
                break;
            }
        }
    }
}
