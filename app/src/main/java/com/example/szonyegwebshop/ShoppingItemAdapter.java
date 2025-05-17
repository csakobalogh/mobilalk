package com.example.szonyegwebshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShoppingItem> mShoppingItemData = new ArrayList<>();
    private ArrayList<ShoppingItem> mShoppingItemDataAll = new ArrayList<>();
    private Context mContext;
    private boolean isCartView = false;
    private int lastPosition = -1;
    private OnItemRemovedListener onItemRemovedListener;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData, boolean isCartView) {
        this.mShoppingItemData = itemsData;
        this.mShoppingItemDataAll = itemsData;
        this.mContext = context;
        this.isCartView = isCartView;
    }

    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    @Override
    public ShoppingItemAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemData.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mShoppingItemDataAll.size();
                results.values = mShoppingItemDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ShoppingItem item : mShoppingItemDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingItemData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
            mPriceText = itemView.findViewById(R.id.price);
            Button addToCartButton = itemView.findViewById(R.id.add_to_cart);
            Button removeFromCartButton = itemView.findViewById(R.id.remove_from_cart);
            if (addToCartButton != null && removeFromCartButton != null) {
                if (isCartView) {
                    addToCartButton.setVisibility(View.GONE);
                    removeFromCartButton.setVisibility(View.VISIBLE);

                    removeFromCartButton.setOnClickListener(view -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        String uid = auth.getCurrentUser().getUid();
                        ShoppingItem item = mShoppingItemData.get(getBindingAdapterPosition());

                        String documentId = item.getDocumentId();
                        db.collection("users")
                                .document(uid)
                                .collection("cart")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(mContext, "Eltávolítva a kosárból", Toast.LENGTH_SHORT).show();

                                    mShoppingItemData.remove(getBindingAdapterPosition());
                                    notifyItemRemoved(getBindingAdapterPosition());

                                    if (onItemRemovedListener != null) {
                                        onItemRemovedListener.onItemRemoved();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(mContext, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    });

                } else {
                    removeFromCartButton.setVisibility(View.GONE);

                    addToCartButton.setOnClickListener(view -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        String uid = auth.getCurrentUser().getUid();
                        ShoppingItem item = mShoppingItemData.get(getAdapterPosition());

                        db.collection("users")
                                .document(uid)
                                .collection("cart")
                                .add(item)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(mContext, "Kosárba téve!", Toast.LENGTH_SHORT).show();
                                    ((ProductListActivity) mContext).updateAlertIcon();
                                })
                                .addOnFailureListener(e -> Toast.makeText(mContext, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    });
                }
            }
        }

        void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRatedInfo());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }
    }
}