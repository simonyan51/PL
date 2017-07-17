package com.simonyan.pl.ui.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.eventbus.Subscribe;
import com.simonyan.pl.R;
import com.simonyan.pl.db.cursor.CursorReader;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.db.handler.PlAsyncQueryHandler;
import com.simonyan.pl.io.bus.BusProvider;
import com.simonyan.pl.io.bus.event.ApiEvent;
import com.simonyan.pl.io.rest.HttpRequestManager;
import com.simonyan.pl.io.service.PLIntentService;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.NetworkUtil;

public class ProductActivity extends BaseActivity implements
        PlAsyncQueryHandler.AsyncQueryListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private EditText mEtProductTitle;
    private EditText mEtProductPrice;
    private EditText mEtProductDesc;
    private TextView mTvProductTitle;
    private TextView mTvProductPrice;
    private TextView mTvProductDesc;
    private ImageView mIvProductImage;
    private LinearLayout mLlProductView;
    private LinearLayout mLlProductEdit;
    private MenuItem mMenuEdit;
    private MenuItem mMenuDone;
    private MenuItem mMenuUnfav;
    private MenuItem mMenuFav;
    private Product mProduct;
    private PlAsyncQueryHandler mPlAsyncQueryHandler;

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
        init();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product_item, menu);
        mMenuEdit = menu.findItem(R.id.menu_product_edit);
        mMenuDone = menu.findItem(R.id.menu_product_done);
        mMenuUnfav = menu.findItem(R.id.menu_product_unfav);
        mMenuFav = menu.findItem(R.id.menu_product_fav);

        if (!mProduct.isUserProduct()) {
            mMenuEdit.setVisible(false);
            mMenuDone.setVisible(false);
        }

        if (!mProduct.isFavorite()) {
            mMenuFav.setVisible(false);
            mMenuUnfav.setVisible(true);
        } else {
            mMenuFav.setVisible(true);
            mMenuUnfav.setVisible(false);
        }

        return true;
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_product_edit:
                mLlProductView.setVisibility(View.GONE);
                mMenuDone.setVisible(true);
                mMenuEdit.setVisible(false);
                openEditLayout(mProduct);
                return true;

            case R.id.menu_product_fav:
                mMenuFav.setVisible(false);
                mMenuUnfav.setVisible(true);
                mProduct.setFavorite(false);
                mPlAsyncQueryHandler.updateFavoriteProduct(mProduct);
                return true;

            case R.id.menu_product_unfav:
                mMenuFav.setVisible(true);
                mMenuUnfav.setVisible(false);
                mProduct.setFavorite(true);
                mPlAsyncQueryHandler.updateFavoriteProduct(mProduct);
                return true;

            case R.id.menu_product_done:
                updateProduct(
                        mEtProductTitle.getText().toString(),
                        Integer.parseInt(mEtProductPrice.getText().toString()),
                        mEtProductDesc.getText().toString()
                );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ApiEvent product) {

        if (product.isSuccess()) {
            mProduct = (Product) product.getEventData();
        } else {
            Toast.makeText(getBaseContext(), R.string.msg_wrong_error, Toast.LENGTH_LONG).show();
        }

        openViewLayout(mProduct);
    }


    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCT:
                mProduct = CursorReader.parseProduct(cursor);
                customizeActionBar();
                loadProduct();
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.UPDATE_PRODUCT:
                mLlProductEdit.setVisibility(View.GONE);
                mMenuDone.setVisible(false);
                mMenuEdit.setVisible(true);
                openViewLayout(mProduct);
                break;

            case PlAsyncQueryHandler.QueryToken.UPDATE_FAVORITE_PRODUCT:

                if (!cookie.equals(ApiEvent.EventType.PRODUCT_ITEM_LOADED)) {
                    Toast.makeText(this, R.string.msg_wrong_error, Toast.LENGTH_LONG).show();

                    if (mMenuFav.isVisible() && mProduct.isFavorite()) {

                        mMenuFav.setVisible(false);
                        mMenuUnfav.setVisible(true);
                        mProduct.setFavorite(false);

                    } else {

                        mMenuUnfav.setVisible(false);
                        mMenuFav.setVisible(true);
                        mProduct.setFavorite(true);
                    }
                }

                break;
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void getData() {
        long productId = getIntent().getExtras().getLong(Constant.Extra.EXTRA_PRODUCT_ID);
        mPlAsyncQueryHandler.getProduct(productId);
    }

    private void findViews() {
        mIvProductImage = (ImageView) findViewById(R.id.iv_product_image);
        mLlProductView = (LinearLayout) findViewById(R.id.ll_product_view);
        mTvProductTitle = (TextView) findViewById(R.id.tv_product_title);
        mTvProductPrice = (TextView) findViewById(R.id.tv_product_price);
        mTvProductDesc = (TextView) findViewById(R.id.tv_product_desc);
        mLlProductEdit = (LinearLayout) findViewById(R.id.ll_product_edit);
        mEtProductTitle = (EditText) findViewById(R.id.et_product_title);
        mEtProductPrice = (EditText) findViewById(R.id.et_product_price);
        mEtProductDesc = (EditText) findViewById(R.id.et_product_desc);
    }

    private void init() {
        mPlAsyncQueryHandler = new PlAsyncQueryHandler(ProductActivity.this, this);
    }

    private void customizeActionBar() {
        setActionBarTitle(mProduct.getName());
    }

    private void loadProduct() {

        if (NetworkUtil.getInstance().isConnected(this) && !mProduct.isUserProduct()) {
            PLIntentService.start(
                    this,
                    Constant.API.PRODUCT_ITEM + mProduct.getId() + Constant.API.PRODUCT_ITEM_POSTFIX,
                    HttpRequestManager.RequestType.PRODUCT_ITEM
            );

        } else {
            openViewLayout(mProduct);
        }
    }

    private void updateProduct(String name,
                               long price,
                               String description) {
        mProduct.setName(name);
        mProduct.setPrice(price);
        mProduct.setDescription(description);

        mPlAsyncQueryHandler.updateProduct(mProduct);
    }

    private void openViewLayout(Product product) {
        mLlProductView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);
        mTvProductTitle.setText(product.getName());
        mTvProductPrice.setText(String.valueOf(product.getPrice()));
        mTvProductDesc.setText(product.getDescription());
    }

    private void openEditLayout(Product product) {
        mLlProductEdit.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);
        mEtProductTitle.setText(product.getName());
        mEtProductPrice.setText(String.valueOf(product.getPrice()));
        mEtProductDesc.setText(product.getDescription());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}