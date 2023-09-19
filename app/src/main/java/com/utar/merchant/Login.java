package com.utar.merchant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.utar.merchant.data.Account;
import com.utar.merchant.ui.settings.LanguageActivity;

import java.util.Locale;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginFragment";
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();

        //FirebaseAuth currentUser = mAuth.getUid();
        if(FirebaseAuth.getInstance().getUid() != null){
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);

        findViewById(R.id.iv_lang).setOnClickListener(v -> startActivity(new Intent(this, LanguageActivity.class)));
        progressBar = findViewById(R.id.login_progressBar);

        findViewById(R.id.login_tv_register).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), Register.class)));

        findViewById(R.id.login_tv_forgotPassword).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));

        findViewById(R.id.tv_nfc_login).setOnClickListener(v ->
                startActivity(new Intent(Login.this, NfcLoginActivity.class)));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(email.trim().isEmpty()){
                    editTextEmail.setError(getString(R.string.require_field));
                    return;
                }

                if(!isEmailFormatValid(email.trim())){
                    editTextEmail.setError(getString(R.string.invalid_email));
                    return;
                }

                if(password.trim().isEmpty()){
                    editTextPassword.setError(getString(R.string.require_field));
                    return;
                }

                if(password.length() < 6){
                    editTextPassword.setError(getString(R.string.minimum_password_length));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if(!task.isSuccessful()) {
                                    Toast.makeText(Login.this, getString(R.string.invalid_account),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                FirebaseDatabase.getInstance()
                                        .getReference("user")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Account account = snapshot.getValue(Account.class);

                                                if(!account.getRole().equals(Account.FIX_MERCHANT)){
                                                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_account), Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    return;
                                                }


                                                String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                                if(account.getDeviceId() == null){
                                                    updateDeviceId(id);
                                                    account.setDeviceId(id);
                                                }
                                                if(!account.getDeviceId().equals(id)){
                                                    new AlertDialog.Builder(Login.this)
                                                            .setTitle(getString(R.string.new_device))
                                                            .setMessage(getString(R.string.log_out_from_old_device) + "?")
                                                            .setPositiveButton("OK", (dialog, which) -> {
                                                                Toast.makeText(Login.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                                                                if(account.getPassword() != null){
                                                                    if(!account.getPassword().equals(password)){
                                                                        FirebaseDatabase.getInstance()
                                                                                .getReference("user")
                                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                                .child("password")
                                                                                .setValue(password);
                                                                    }
                                                                }
                                                                updateDeviceId(id);

                                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                finish();
                                                            })
                                                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                                                FirebaseAuth.getInstance().signOut();
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }
                                                else{
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }



                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                        });
            }
        });
    }

    public static void updateDeviceId(String id){
        FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getUid())
                .child("deviceId")
                .setValue(id);
    }


    public boolean isEmailFormatValid(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}