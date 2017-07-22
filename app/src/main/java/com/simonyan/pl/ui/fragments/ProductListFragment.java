package com.simonyan.pl.ui.fragments;

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
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.cursor.CursorReader;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.db.handler.PlAsyncQueryHandler;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.bus.event.ApiEvent;
import com.simonyan.pl.io.rest.HttpRequestManager;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.ui.activities.AddProductActivity;
import com.simonyan.pl.ui.activities.ProductActivity;
import com.simonyan.pl.ui.adapters.ProductAdapter;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.NetworkUtil;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.simonyan.pl.ui.activities.AddProductActivity.NEW_PRODUCT;

public class ProductListFragment extends BaseFragment implements View.OnClickListener,
        PlAsyncQueryHandler.AsyncQueryListener, ProductAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();
    private static final int REQUEST_CODE_ADD = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;
    private PlAsyncQueryHandler mTlAsyncQueryHandler;
    private RecyclerView mRv;
    private ProductAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<Product> mProductArrayList;
    private SwipeRefreshLayout mSwRefresh;

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
        setListeners();
        getData();
        customizeActionBar();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_ADD:
                if (resultCode == RESULT_OK) {
                    Product newProduct = data.getParcelableExtra(NEW_PRODUCT);
                    mProductArrayList.add(newProduct);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
                break;

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_action_bar, menu);
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
    public void onItemClick(Product product, int position) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra(Constant.Extra.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Product product, int position) {
        mTlAsyncQueryHandler.deleteProduct(product, position);
    }

    @Override
    public void onRefresh() {
        fetchData();
        mSwRefresh.setRefreshing(false);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ApiEvent<Object> apiEvent) {
        if (!apiEvent.isSuccess()) {
            Toast.makeText(getActivity(), R.string.msg_wrong_error,
                    Toast.LENGTH_SHORT).show();
        }
        mTlAsyncQueryHandler.getProducts();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCTS:
                ArrayList<Product> products = CursorReader.parseProducts(cursor);
                mProductArrayList.clear();
                mProductArrayList.addAll(products);
                mRecyclerViewAdapter.notifyDataSetChanged();
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
                int position = (int) cookie;
                mProductArrayList.remove(position);
                mRecyclerViewAdapter.notifyItemRemoved(position);
                break;
        }

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {

    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_fragment_proudct_list_recycler);
        mSwRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sr_fragment_proudct_list_swipe);
        mSwRefresh.setOnRefreshListener(this);
    }

    private void init() {
        mTlAsyncQueryHandler = new PlAsyncQueryHandler(getActivity().getApplicationContext(), this);

        mRv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getActivity());
        mRv.setLayoutManager(mLlm);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mProductArrayList = new ArrayList<>();
        mRecyclerViewAdapter = new ProductAdapter(mProductArrayList, this);
        mRv.setAdapter(mRecyclerViewAdapter);
    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void fetchData() {

        if (NetworkUtil.getInstance().isConnected(getActivity())) {
            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_LIST,
                    HttpRequestManager.RequestType.PRODUCT_LIST
            );
        } else {
            mTlAsyncQueryHandler.getProducts();
            Snackbar.make(getActivity().findViewById(R.id.dl_main), R.string.msg_network_connection_error, Snackbar.LENGTH_LONG).show();
        }

    }


    private void startAddProductActivity() {

        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE_ADD);

    }

    private void customizeActionBar() {

    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}