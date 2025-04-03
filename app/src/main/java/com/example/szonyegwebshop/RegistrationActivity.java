package com.example.szonyegwebshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final int SECRET_KEY = 99;

    private FirebaseAuth mAuth;

    EditText nameET;
    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }

        nameET = findViewById(R.id.nameET);
        usernameET = findViewById(R.id.usernameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordAgainET = findViewById(R.id.passwordAgainET);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG,  "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,  "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG,  "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG,  "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG,  "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG,  "onResume");
    }

    public void regisztracio(View view) {
        // TODO: Regisztracio funkcio megvalositasa
        String name = nameET.getText().toString();
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();

        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Nem egyezik a ket megadott jelszo!");
            return;
        }

        Log.i(LOG_TAG, "Regisztracio: " + name + " " + username + " " +  email + " " +  password + " " + passwordAgain);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i(LOG_TAG, "Felhasznalo sikeresen letrehozva.");
                    startProductList();
                } else {
                    Log.d(LOG_TAG, "Felhasznalo letrehozasa nem sikerult");
                    Toast.makeText(RegistrationActivity.this, "Felhasznalo letrehozasa nem sikerult: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void megse(View view) {
        finish();
    }

    private void startProductList() {
        Intent intent = new Intent(this, ProductListActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}