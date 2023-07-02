package com.utar.merchant.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.utar.merchant.Login;
import com.utar.merchant.R;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    TextView tv_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        tv_logout = v.findViewById(R.id.setting_tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                Toast.makeText(getContext(), getString(R.string.log_out_success), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });

        v.findViewById(R.id.tv_language).setOnClickListener(event ->
                startActivity(new Intent(getActivity(), LanguageActivity.class)));

        v.findViewById(R.id.tv_amount_customization).setOnClickListener(e ->
                startActivity(new Intent(getActivity(), AmountCustomizationActivity.class)));
        return v;
    }
}