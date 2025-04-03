package com.example.szonyegwebshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    EditText usernameET;
    EditText passwordET;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        mAuth = FirebaseAuth.getInstance();
    }

    public void bejelentkezes(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i(LOG_TAG, "Felhasznalo sikeresen belepett.");
                    startProductList();
                } else {
                    Log.d(LOG_TAG, "Felhasznalo belepese nem sikerult");
                    Toast.makeText(LoginActivity.this, "Felhasznalo belepese nem sikerult: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startProductList() {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }
}