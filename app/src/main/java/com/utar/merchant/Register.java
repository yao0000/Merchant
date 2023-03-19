package com.utar.merchant;




import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utar.merchant.data.*;

public class Register extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName;
    Button btnReg;
    String email, password, confirmPassword, name;

    ProgressBar progressBar;
    TextView tvLoginPage;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.reg_email);
        editTextPassword = findViewById(R.id.reg_password);
        editTextConfirmPassword = findViewById(R.id.reg_ConfirmPassword);
        editTextName = findViewById(R.id.reg_Name);

        btnReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.reg_progressBar);
        tvLoginPage = findViewById(R.id.reg_loginNow);

        tvLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());
                name = String.valueOf(editTextName.getText());


                if(email.trim().isEmpty()){
                    editTextEmail.setError("This field cannot be blank");
                    return;
                }

                if(password.isEmpty()){
                    editTextPassword.setError("This field cannot be blank");
                    return;
                }

                if(confirmPassword.isEmpty()){
                    editTextConfirmPassword.setError("This field cannot be blank");
                    return;
                }

                if(name.isEmpty()){
                    editTextName.setError("This field cannot be blank");
                    return;
                }

                if(!password.equals(confirmPassword)){
                    editTextConfirmPassword.setError("Password and Confirm Password not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    toast("createUserWithEmail:success");
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                    //Firebase perform
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String userID = currentUser.getUid();

                                    databaseReference = FirebaseDatabase.getInstance().getReference("user");
                                    Account account = new Account(name, email);
                                    databaseReference.child(userID).setValue(account);

                                    FirebaseAuth.getInstance().signOut();



                                    finish();

                                    //FirebaseUser user = mAuth.getCurrentUser();

                                } else {

                                    // If sign in fails, display a message to the user.
                                    toast("createUserWithEmail:failure");



                                }
                            }
                        });
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(Register.this, msg, Toast.LENGTH_LONG).show();
    }
}