package com.utar.merchant.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utar.merchant.Login;
import com.utar.merchant.MainActivity;
import com.utar.merchant.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utar.merchant.TransactionActivity;
import com.utar.merchant.data.Transaction;

public class SettingsFragment extends Fragment {

    Button btnLogout;
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        String userID = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userID).child("transactions");

        btnLogout = v.findViewById(R.id.logout);

        Button btn_push = v.findViewById(R.id.push);

        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = new Transaction("Shopee", 60, Transaction.PAYMENT);
                databaseReference.push().setValue(t);
            }
        });

        Button btn_positive = v.findViewById(R.id.positive);
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = new Transaction("ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ", 100, Transaction.TRANSFER_IN);
                databaseReference.push().setValue(t);
            }
        });

        Button btn_t = v.findViewById(R.id.transaction);
        btn_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TransactionActivity.class));
            }
        });




        //Setting Page
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        // Inflate the layout for this fragment
        return v;
    }
}