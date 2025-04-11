package com.example.szonyegwebshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.szonyegwebshop.ShoppingItem;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShoppingItem> mShoppingItemData = new ArrayList<>();
    private ArrayList<ShoppingItem> mShoppingItemDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData) {
        this.mShoppingItemData = itemsData;
        this.mShoppingItemDataAll = itemsData;
        this.mContext = context;
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
        return shopingFilter;
    }

    private Filter shopingFilter = new Filter() {
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

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ProductListActivity) mContext).updateAlertIcon();
                }
            });
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