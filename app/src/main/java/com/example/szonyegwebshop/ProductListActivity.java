package com.example.szonyegwebshop;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProductListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProductListActivity.class.getName();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i(LOG_TAG, "Authentikalt felhasznalo!");
        } else {
            Log.i(LOG_TAG, "Nem authentikalt felhasznalo!");
            finish();
        }
    }
}