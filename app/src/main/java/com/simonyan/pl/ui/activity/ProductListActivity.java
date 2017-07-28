package com.simonyan.pl.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.bus.event.ApiEvent;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.ui.adapter.TabFragmentAdapter;
import com.simonyan.pl.ui.fragment.ProductFragment;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.NetworkUtil;

import java.util.ArrayList;

public class ProductListActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ViewPager mViewPager;
    private ArrayList<Product> mProductArrayList;
    private TabFragmentAdapter mFragmentAdapter;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.register(this);
        findViews();
        setListeners();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product_list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            case ApiEvent.EventType.PRODUCT_LIST_LOADED:
                if (apiEvent.isSuccess()) {
                    mProductArrayList = (ArrayList<Product>) apiEvent.getEventData();
                    setupTabs(mProductArrayList);
                    customizeActionBar();
                    loadProduct(mFragmentAdapter.getItem(0).getId());

                } else {
                    Toast.makeText(this, "Something went wrong, please try again",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setActionBarTitle(String.valueOf(mFragmentAdapter.getPageTitle(position)));
        loadProduct(mProductArrayList.get(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mViewPager.addOnPageChangeListener(this);
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp_product_list);
    }

    private void customizeActionBar() {
        setActionBarTitle(mProductArrayList.get(0).getName());
    }

    private void setupTabs(ArrayList<Product> productArrayList) {
        if (mViewPager != null && getTabLayout() != null) {
            mFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager());

            for (Product product : productArrayList) {
                mFragmentAdapter.addFragment(ProductFragment.newInstance(product),
                        product.getName());
            }

            mViewPager.setAdapter(mFragmentAdapter);
            getTabLayout().setupWithViewPager(mViewPager);
        }
    }

    private void loadProduct(long productId) {
        PLIntentService.start(
                this,
                Constant.API.PRODUCT_ITEM + String.valueOf(productId)
                        + Constant.API.PRODUCT_ITEM_POSTFIX,
                Constant.RequestType.PRODUCT_ITEM
        );
    }

    private void loadData() {
        if (NetworkUtil.getInstance().isConnected(this)) {
            PLIntentService.start(
                    this,
                    Constant.API.PRODUCT_LIST,
                    Constant.RequestType.PRODUCT_LIST
            );
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}