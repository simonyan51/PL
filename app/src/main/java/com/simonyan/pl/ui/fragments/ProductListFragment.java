package com.simonyan.pl.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.cursor.CursorReader;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.db.handler.PlAsyncQueryHandler;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.rest.HttpRequestManager;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.ui.activities.AddProductActivity;
import com.simonyan.pl.ui.adapters.ProductAdapter;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.NetworkUtil;

import org.parceler.Parcels;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.simonyan.pl.ui.activities.AddProductActivity.NEW_PRODUCT;

public class ProductListFragment extends BaseFragment implements
            View.OnClickListener,
            PlAsyncQueryHandler.AsyncQueryListener,
            ProductAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1;


    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;

    private ArrayList<Product> mProducts;

    private RecyclerView mRvProductListRecycler;

    private ProductAdapter mProductAdapter;

    private PlAsyncQueryHandler mTlAsyncQueryHandler;

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
        init();
        fetchData();
        setListeners();
        getData();
        customizeActionBar();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_product_add:
                startAddProductActivity();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(Product product) {

    }

    @Override
    public void onLongItemClick(Product product) {
        showDialog(product);
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCTS:
                ArrayList<Product> newProducts = CursorReader.parseProducts(cursor);
                mProducts.clear();
                mProducts.addAll(newProducts);
                mProductAdapter.notifyDataSetChanged();
                break;
        }

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

        switch (token) {
            case PlAsyncQueryHandler.QueryToken.DELETE_PRODUCT:
                mProducts.remove(mProducts.indexOf((Product) cookie));
                mProductAdapter.notifyDataSetChanged();
                break;
        }

    }

    @Subscribe
    public void onEventReceived(ArrayList<Product> productArrayList) {
        mProducts.clear();
        mProducts.addAll(productArrayList);
        mProductAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_action_bar, menu);
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Product newProduct = Parcels.unwrap(data.getParcelableExtra(NEW_PRODUCT));
                mProducts.add(newProduct);
                mProductAdapter.notifyDataSetChanged();
            }
        }

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

    private void fetchData() {


        if (NetworkUtil.getInstance().isConnected(getContext())) {
            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_LIST,
                    HttpRequestManager.RequestType.PRODUCT_LIST
            );

//            PLIntentService.start(
//                    getActivity(),
//                    Constant.API.PRODUCT_ITEM,
//                    HttpRequestManager.RequestType.PRODUCT_ITEM
//            );

        } else {

            mTlAsyncQueryHandler.getProducts();

            Snackbar.make(getActivity().findViewById(R.id.dl_main), R.string.msg_network_connection_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void findViews(View view) {
        mRvProductListRecycler = (RecyclerView) view.findViewById(R.id.rv_fragment_proudct_list_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sr_fragment_proudct_list_swipe);
    }

    private void init() {

        mTlAsyncQueryHandler = new PlAsyncQueryHandler(getActivity().getApplicationContext(), this);

        mRvProductListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvProductListRecycler.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        mRvProductListRecycler.setItemAnimator(new DefaultItemAnimator());

        mProducts = new ArrayList<>();

        mProductAdapter = new ProductAdapter(mProducts, this);

        mRvProductListRecycler.setAdapter(mProductAdapter);
    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    private void startAddProductActivity() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE);
    }


    private void showDialog(final Product product) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.product_delete_title)
                .setMessage(getString(R.string.delete_question) + " " + product.getName() + "?")
                .setPositiveButton(R.string.yes_value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct(product);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteProduct(Product product) {
        mTlAsyncQueryHandler.deleteProduct(product);
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}