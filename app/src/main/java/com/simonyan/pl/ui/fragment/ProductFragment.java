package com.simonyan.pl.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.bus.event.ApiEvent;
import com.simonyan.pl.util.Constant;

public class ProductFragment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private TextView mTvProductTitle;
    private TextView mTvProductPrice;
    private TextView mTvProductDesc;
    private ImageView mIvProductImage;
    private Product mProduct;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    public static ProductFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(Constant.Argument.ARGUMENT_PRODUCT, product);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        BusProvider.register(this);
        findViews(view);
        setListeners();
        getData();
        setProduct(mProduct);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ApiEvent<Object> apiEvent) {
        switch (apiEvent.getEventType()){
            case ApiEvent.EventType.PRODUCT_ITEM_LOADED:
                updateDescription((Product) apiEvent.getEventData());
                break;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {

    }

    private void findViews(View view) {
        mIvProductImage = (ImageView) view.findViewById(R.id.iv_product_fr_image);
        mTvProductTitle = (TextView) view.findViewById(R.id.tv_product_fr_title);
        mTvProductPrice = (TextView) view.findViewById(R.id.tv_product_fr_price);
        mTvProductDesc = (TextView) view.findViewById(R.id.tv_product_fr_desc);
    }

    public void getData() {
        if (getArguments() != null) {
            mProduct = getArguments().getParcelable(Constant.Argument.ARGUMENT_PRODUCT);
        }
    }

    private void setProduct(Product product) {
        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);
        mTvProductTitle.setText(product.getName());
        mTvProductPrice.setText(String.valueOf(product.getPrice()));
    }

    private void updateDescription(Product product) {
        mTvProductDesc.setText(product.getDescription());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}