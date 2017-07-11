package com.simonyan.pl.ui.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.db.handler.PlAsyncQueryHandler;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.rest.HttpRequestManager;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.util.Constant;

public class ProductActivity extends BaseActivity implements View.OnClickListener, PlAsyncQueryHandler.AsyncQueryListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================


    private Product mProduct;

    private PlAsyncQueryHandler handler;

    private ImageView mIvProductImage;

    private MenuItem mIEditItem;
    private MenuItem mIDoneItem;

    private LinearLayout mLlProductDetails;
    private TextView mTvProductTitle;
    private TextView mTvProductPrice;
    private TextView mTvProductDesc;

    private LinearLayout mLlProductEdit;
    private EditText mEtProductTitle;
    private EditText mEtProductPrice;
    private EditText mEtProductDesc;


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
        init();
        findViews();
        BusProvider.register(this);

        PLIntentService.start(
                    this,
                    Constant.API.PRODUCT_ITEM + getSendedProductId() + "/detail",
                    HttpRequestManager.RequestType.PRODUCT_ITEM
        );

        setListeners();
        customizeActionBar();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);

        mIEditItem = menu.findItem(R.id.menu_product_edit);
        mIDoneItem = menu.findItem(R.id.menu_product_done);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (mIDoneItem.isVisible()) {
                    openDoneLayout(mProduct);
                    break;
                }

                finish();
                break;
            case R.id.menu_product_edit:
                openEditLayout();
                break;

            case R.id.menu_product_done:
                acceptChanges(mProduct);

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
    public void onEventReceived(Product product) {

        mProduct = product;

        openDoneLayout(product);

    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.UPDATE_PRODUCT:
                openDoneLayout((Product) cookie);
                break;
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void init() {
        handler = new PlAsyncQueryHandler(getBaseContext(), this);
    }

    private void setListeners() {

    }

    private String getSendedProductId() {

        return getIntent().getStringExtra("product_id");

    }

    private void findViews() {

        mLlProductDetails = (LinearLayout) findViewById(R.id.ll_product_details);
        mLlProductEdit = (LinearLayout) findViewById(R.id.ll_product_edit);

        mIvProductImage = (ImageView) findViewById(R.id.iv_product_image);

        mTvProductTitle = (TextView) findViewById(R.id.tv_product_title);
        mTvProductDesc = (TextView) findViewById(R.id.tv_product_desc);
        mTvProductPrice = (TextView) findViewById(R.id.tv_product_price);

        mEtProductTitle = (EditText) findViewById(R.id.et_product_title);
        mEtProductDesc = (EditText) findViewById(R.id.et_product_desc);
        mEtProductPrice = (EditText) findViewById(R.id.et_product_price);
    }

    private void openDoneLayout(Product product) {

        if (!mIEditItem.isVisible()) {
            mIDoneItem.setVisible(false);
            mIEditItem.setVisible(true);
            mLlProductEdit.setVisibility(View.GONE);
            mLlProductDetails.setVisibility(View.VISIBLE);
        }

        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);

        mTvProductTitle.setText(product.getName());
        mTvProductDesc.setText(product.getDescription());
        mTvProductPrice.setText(String.format("%d", product.getPrice()));

    }

    private void openEditLayout() {

        mIEditItem.setVisible(false);
        mIDoneItem.setVisible(true);

        mLlProductEdit.setVisibility(View.VISIBLE);
        mLlProductDetails.setVisibility(View.GONE);

        mEtProductTitle.setText(mProduct.getName());
        mEtProductDesc.setText(mProduct.getDescription());
        mEtProductPrice.setText(String.format("%d", mProduct.getPrice()));

    }

    private void acceptChanges(Product product) {

        product.setName(mEtProductTitle.getText().toString());
        product.setDescription(mEtProductDesc.getText().toString());
        product.setPrice(Integer.parseInt(mEtProductPrice.getText().toString()));

        handler.updateProduct(product);
    }


    private void customizeActionBar() {

    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}