package com.simonyan.pl.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.rest.HttpRequestManager;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.ui.adapters.ProductAdapter;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.NetworkUtil;

import java.util.ArrayList;

public class ProductListFragment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;

    private ArrayList<Product> products;

    private RecyclerView mRvProductListRecycler;

    private ProductAdapter mProductAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    public static ProductListFragment newInstance(Bundle args) {
        ProductListFragment fragment = new ProductListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        BusProvider.register(this);
        findViews(view);
        setListeners();
        getData();
        customizeActionBar();
        fetchData();
        return view;
    }

    private void fetchData() {
        if (NetworkUtil.getInstance().isConnected(getContext())) {
            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_LIST,
                    HttpRequestManager.RequestType.PRODUCT_LIST
            );

            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_ITEM,
                    HttpRequestManager.RequestType.PRODUCT_ITEM
            );

        } else {
            Snackbar.make(getActivity().findViewById(R.id.dl_main), "Not connection", Snackbar.LENGTH_LONG).show();
        }
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
    public void onEventReceived(ArrayList<Product> productArrayList) {

        products = productArrayList;
        implementRecyclerView();

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void findViews(View view) {
        mRvProductListRecycler = (RecyclerView) view.findViewById(R.id.rv_fragment_proudct_list_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sr_fragment_proudct_list_swipe);
    }

    private void implementRecyclerView() {
        mRvProductListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mProductAdapter = new ProductAdapter(products, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Toast.makeText(getActivity(), "Product id: " + product.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        mRvProductListRecycler.setAdapter(mProductAdapter);
    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}