package com.utar.merchant.ui.home;


import com.utar.merchant.ui.home.transaction.FilterDate;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.data.Transaction;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;

import com.utar.merchant.R;
import com.utar.merchant.ui.home.transaction.FilterPopupWindows;
import com.utar.merchant.ui.home.transaction.TransactionViewHolder;

public class TransactionActivity extends AppCompatActivity{
    private static final String TAG = "TransactionActivity";
    private DatabaseReference databaseReference;
    private List<Transaction> transactionList = new ArrayList<>(), thirtyDaysList;


    private ImageView iv_filter;
    private TextView tv_start_date, tv_end_date;
    private RecyclerView recyclerView;

    private static TransactionActivity transactionActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        transactionActivity = this;

        recyclerView = findViewById(R.id.transaction_list);
        iv_filter = findViewById(R.id.iv_filter);

        iv_filter.setOnClickListener(v -> new FilterPopupWindows(TransactionActivity.this, findViewById(R.id.mainLayout)));


        String userID = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    transactionList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);

                        transactionList.add(0,transaction);
                    }

                    thirtyDaysList = new ArrayList<>();
                    long currentTimestamp = System.currentTimeMillis();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTimestamp);

                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long todayTimestamp = calendar.getTimeInMillis();
                    calendar.add(Calendar.DAY_OF_MONTH, -30);

                    long previous30days = calendar.getTimeInMillis();

                    for(int i = transactionList.size()-1; i >= 0; i--){
                        if(isInRange(transactionList.get(i), previous30days, todayTimestamp)){
                            thirtyDaysList.add(0, transactionList.get(i));
                        }
                    }
                    if(thirtyDaysList.isEmpty()){
                        toast(getString(R.string.noRecord));
                        return;
                    }
                    displayList(thirtyDaysList);
                }
                else {
                    toast(getString(R.string.noRecord));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                toast(error.getMessage());
            }
        });
    }

    public static TransactionActivity getInstance(){
        return transactionActivity;
    }

    public static String getIdString(int resId){
        return transactionActivity.getString(resId);
    }

    public void displayList(List list){
        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
        recyclerView.setAdapter(new RecyclerView.Adapter<TransactionViewHolder>() {

            @Override
            public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
                return new TransactionViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(TransactionViewHolder holder, int position) {
                Transaction transaction = (Transaction) list.get(position);
                holder.bind(transaction);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public List getTransactionList(){
        return transactionList;
    }
    public boolean isInRange(Transaction transaction, long startTime, long endTime){
        return ((transaction.getTimestamp() > startTime) && (transaction.getTimestamp() < endTime));
    }
}