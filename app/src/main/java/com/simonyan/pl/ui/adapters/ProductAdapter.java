package com.simonyan.pl.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;

import java.util.ArrayList;

/**
 * Created by simonyan51 on 6/30/17.
 */


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<Product> mProductArrayList;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProductAdapter(ArrayList<Product> productArrayList, OnItemClickListener onItemClickListener) {
        mProductArrayList = productArrayList;
        mOnItemClickListener = onItemClickListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item_product, viewGroup, false);
        return new ViewHolder(view, mProductArrayList, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mProductArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductTitle;
        TextView tvProductPrice;
        ImageView ivProductImage;
        LinearLayout llItemContainer;
        OnItemClickListener onItemClickListener;
        ArrayList<Product> productArrayList;

        ViewHolder(View itemView, ArrayList<Product> productArrayList, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.productArrayList = productArrayList;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
        }

        void findViews(View view) {
            llItemContainer = (LinearLayout) view.findViewById(R.id.ll_product_item_container);
            tvProductTitle = (TextView) view.findViewById(R.id.tv_product_item_title);
            tvProductPrice = (TextView) view.findViewById(R.id.tv_product_item_price);
            ivProductImage = (ImageView) view.findViewById(R.id.iv_product_item_logo);
        }

        void bindData() {


            Glide.with(itemView.getContext()).load(productArrayList.get(getAdapterPosition()).getImage()).into(ivProductImage);

            tvProductTitle.setText(productArrayList.get(getAdapterPosition()).getName());

            tvProductPrice.setText(String.valueOf(productArrayList.get(getAdapterPosition()).getPrice()));

            llItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(productArrayList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Product product);

    }
}