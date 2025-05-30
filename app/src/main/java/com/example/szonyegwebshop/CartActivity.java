package com.example.szonyegwebshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingItemAdapter adapter;
    private ArrayList<ShoppingItem> cartItems = new ArrayList<>();
    private TextView totalPriceTextView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingItemAdapter(this, cartItems, true, true);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadCartItems();

        Button backToProductListButton = findViewById(R.id.backToProductListButton);
        backToProductListButton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, ProductListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(view -> {
            String uid = auth.getCurrentUser().getUid();

            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete();
                        }

                        cartItems.clear();
                        adapter.notifyDataSetChanged();
                        updateTotalPrice();
                        Toast Toast = null;
                        Toast.makeText(this, "Rendelés leadva. Kosár kiürítve.", Toast.LENGTH_SHORT).show();
                        Log.d("CartActivity", "Fizetés sikeresen megtörtént");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CartActivity", "Hiba fizetés közben " + e.getMessage());
                    });
        });

    }

    private void loadCartItems() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    double total = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ShoppingItem item = doc.toObject(ShoppingItem.class);
                        item.setDocumentId(doc.getId());
                        cartItems.add(item);

                        try {
                            total += Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    totalPriceTextView.setText("Összesen: " + total + " Ft");

                    adapter.setOnItemRemovedListener(this::updateTotalPrice);
                    adapter.notifyDataSetChanged();
                });
    }


    public void updateTotalPrice() {
        double total = 0;
        for (ShoppingItem item : cartItems) {
            try {
                total += Double.parseDouble(item.getPrice().replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        totalPriceTextView.setText("Összesen: " + total + " Ft");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (this != null) {
                loadCartItems();
                updateTotalPrice();
            }
        } catch (Exception e) {
            Log.e("Lifecycle", "onStart hiba: " + e.getMessage());
        }
    }
}
