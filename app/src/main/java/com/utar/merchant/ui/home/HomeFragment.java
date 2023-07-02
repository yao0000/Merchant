package com.utar.merchant.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utar.merchant.R;
import com.utar.merchant.data.*;


public class HomeFragment extends Fragment implements View.OnClickListener {
    TextView tv_amount, tv_name;
    DatabaseReference databaseReference;
    Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        v.findViewById(R.id.home_iv_reload).setOnClickListener(this::onClick);
        v.findViewById(R.id.home_iv_withdraw).setOnClickListener(this::onClick);
        v.findViewById(R.id.home_iv_history).setOnClickListener(this::onClick);

        tv_amount = v.findViewById(R.id.home_tv_balance);
        tv_name = v.findViewById(R.id.home_tv_name);

        String userID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("user").child(userID).keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                account = snapshot.getValue(Account.class);
                tv_amount.setText("RM " + account.getBalance());
                tv_name.setText(account.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_iv_reload: {
                Intent intent = new Intent(getContext(), ReloadWithdrawActivity.class);
                intent.putExtra("mode", ReloadWithdrawActivity.RELOAD);
                startActivity(intent);
                break;
            }
            case R.id.home_iv_withdraw: {
                Intent intent = new Intent(getContext(), ReloadWithdrawActivity.class);
                intent.putExtra("mode", ReloadWithdrawActivity.WITHDRAW);
                startActivity(intent);
                break;
            }
            case R.id.home_iv_history: {
                startActivity(new Intent(getContext(), TransactionActivity.class));
                break;
            }
        }
    }
}