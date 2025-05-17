package com.example.szonyegwebshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductManagerAdapter extends RecyclerView.Adapter<ProductManagerAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onUpdate(ShoppingItem item, String docId);
        void onDelete(String docId);
    }

    private ArrayList<ShoppingItem> itemList;
    private OnItemActionListener actionListener;
    private ArrayList<String> documentIds;

    public ProductManagerAdapter(ArrayList<ShoppingItem> itemList, OnItemActionListener listener) {
        this.itemList = itemList;
        this.actionListener = listener;
        this.documentIds = new ArrayList<>();
        fetchDocumentIds();
    }

    private void fetchDocumentIds() {
        FirebaseFirestore.getInstance().collection("Items")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    documentIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        documentIds.add(doc.getId());
                    }
                    notifyDataSetChanged();
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_manager_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem currentItem = itemList.get(position);
        String docId = (position < documentIds.size()) ? documentIds.get(position) : null;

        holder.nameText.setText(currentItem.getName());
        holder.priceText.setText("Ár: " + currentItem.getPrice());

        holder.updateButton.setOnClickListener(v -> {
            if (docId != null) actionListener.onUpdate(currentItem, docId);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (docId != null) actionListener.onDelete(docId);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText;
        Button updateButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textItemName);
            priceText = itemView.findViewById(R.id.textItemPrice);
            updateButton = itemView.findViewById(R.id.buttonUpdate);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
