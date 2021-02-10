package com.example.seriium.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.models.SerieDetails;
import com.example.seriium.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mIme;
    private EditText mPrezime;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private ProgressBar mProgressBar;
    private Switch mSwitch;
    private Button mRegistrer;
    private Button goToLoginBtn;
    private User user = new User();

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mIme = findViewById(R.id.reg_ime);
        mPrezime = findViewById(R.id.reg_prezime);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mConfirmPassword = findViewById(R.id.reg_password_confirm);
        mProgressBar = findViewById(R.id.reg_progressbar);
        mSwitch = findViewById(R.id.reg_switch);
        mRegistrer = findViewById(R.id.reg_button);

        goToLoginBtn = findViewById(R.id.reg_loginbutton);
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mPassword.setTransformationMethod(null);
                    mConfirmPassword.setTransformationMethod(null);
                }
                else {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    mConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        mRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ime =  mIme.getText().toString().trim();
                String prezime =  mPrezime.getText().toString().trim();
                String email =  mEmail.getText().toString().trim();
                String password =  mPassword.getText().toString().trim();
                String confirm_password =  mConfirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(ime)) {
                    mIme.setError("Ime je obvezno!");
                    return;
                }
                if(TextUtils.isEmpty(prezime)) {
                    mPrezime.setError("Prezime je obvezno!");
                    return;
                }
                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email je obvezan!");
                    return;
                }
                if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
                    mEmail.setError("Email je u nepravilnom obliku!");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Lozinka je obvezna!");
                    return;
                }
                if(password.length() < 6) {
                    mPassword.setError("Lozinka mora biti najmanje 6 znakova dugačka!");
                    return;
                }
                if(TextUtils.isEmpty(confirm_password)) {
                    mConfirmPassword.setError("Potvrda lozinke je obvezna!");
                    return;
                }
                if(!password.equals(confirm_password)){
                    Toast.makeText(Register.this, "Lozinke se ne podudaraju!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AddUser(ime, prezime);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(Register.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
            }
        });

    }

    private void AddUser (String ime, String prezime) {
        user.setIme(ime);
        user.setPrezime(prezime);
        user.setSerije(null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("korisnici");
        users.child(mAuth.getCurrentUser().getUid()).setValue(user);
    }
}