package com.example.szonyegwebshop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductManagerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    private EditText nameInput, infoInput, priceInput, ratingInput;
    private Button addButton;
    private Button updateButton;
    private String updatingDocId = null;

    private RecyclerView recyclerView;
    private ArrayList<ShoppingItem> itemList;
    private ProductManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);

        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("Items");

        nameInput = findViewById(R.id.editName);
        infoInput = findViewById(R.id.editInfo);
        priceInput = findViewById(R.id.editPrice);
        ratingInput = findViewById(R.id.editRating);
        addButton = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.itemRecyclerView);
        updateButton = findViewById(R.id.buttonUpdate);
        updateButton.setEnabled(false);

        itemList = new ArrayList<>();
        adapter = new ProductManagerAdapter(itemList, new ProductManagerAdapter.OnItemActionListener() {
            @Override
            public void onUpdate(ShoppingItem item, String docId) {
                nameInput.setText(item.getName());
                infoInput.setText(item.getInfo());
                priceInput.setText(item.getPrice());
                ratingInput.setText(String.valueOf(item.getRatedInfo()));

                updatingDocId = docId;
                updateButton.setEnabled(true);
            }

            @Override
            public void onDelete(String docId) {
                onDeleteClick(docId);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> addItem());

        updateButton.setOnClickListener(v -> {
            if (updatingDocId != null) {
                onUpdateClick(updatingDocId);
            }
        });

        loadItems();
    }

    private void addItem() {
        String name = nameInput.getText().toString();
        String info = infoInput.getText().toString();
        String price = priceInput.getText().toString();
        float rating = Float.parseFloat(ratingInput.getText().toString());

        ShoppingItem newItem = new ShoppingItem(name, info, price, rating, R.drawable.carpet_1);

        itemsRef.add(newItem).addOnSuccessListener(docRef -> {
            Toast.makeText(this, "Hozzáadva", Toast.LENGTH_SHORT).show();
            loadItems();
        });
    }

    private void loadItems() {
        itemsRef.get().addOnSuccessListener(snapshots -> {
            itemList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                ShoppingItem item = doc.toObject(ShoppingItem.class);
                itemList.add(item);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void clearInputs() {
        nameInput.setText("");
        infoInput.setText("");
        priceInput.setText("");
        ratingInput.setText("");
        updatingDocId = null;
        updateButton.setEnabled(false);
    }

    private void onUpdateClick(String docId) {
        String name = nameInput.getText().toString();
        String info = infoInput.getText().toString();
        String price = priceInput.getText().toString();
        float rating = Float.parseFloat(ratingInput.getText().toString());

        ShoppingItem updatedItem = new ShoppingItem(name, info, price, rating, R.drawable.carpet_1);

        itemsRef.document(docId).set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Frissítve", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadItems();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Frissítés sikertelen", Toast.LENGTH_SHORT).show();
                });
    }

    private void onDeleteClick(String docId) {
        itemsRef.document(docId).delete()
                .addOnSuccessListener(aVoid -> loadItems());
    }
}

