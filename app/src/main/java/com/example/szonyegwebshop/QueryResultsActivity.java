package com.example.szonyegwebshop;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class QueryResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingItemAdapter adapter;
    private ArrayList<ShoppingItem> resultItems = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);

        recyclerView = findViewById(R.id.queryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingItemAdapter(this, resultItems, false, false);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        Button btnQuery1 = findViewById(R.id.btnQuery1);
        Button btnQuery2 = findViewById(R.id.btnQuery2);
        Button btnQuery3 = findViewById(R.id.btnQuery3);

        btnQuery1.setOnClickListener(v -> loadTopRated());
        btnQuery2.setOnClickListener(v -> searchByName());
        btnQuery3.setOnClickListener(v -> loadRatedSorted());
    }

    private void loadTopRated() {
        db.collection("Items")
                .whereGreaterThan("ratedInfo", 3.0)
                .orderBy("ratedInfo", Query.Direction.DESCENDING)
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    resultItems.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        resultItems.add(doc.toObject(ShoppingItem.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void searchByName() {
        db.collection("Items")
                .whereGreaterThanOrEqualTo("name", "Perzsa")
                .whereLessThan("name", "Perzsa" + "\uf8ff")
                .orderBy("name")
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    resultItems.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        resultItems.add(doc.toObject(ShoppingItem.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadRatedSorted() {
        db.collection("Items")
                .whereGreaterThanOrEqualTo("ratedInfo", 1.0)
                .whereLessThanOrEqualTo("ratedInfo", 5.0)
                .orderBy("ratedInfo", Query.Direction.ASCENDING)
                .orderBy("name")
                .get()
                .addOnSuccessListener(snapshot -> {
                    resultItems.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        resultItems.add(doc.toObject(ShoppingItem.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
