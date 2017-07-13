package com.simonyan.pl.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.simonyan.pl.R;
import com.simonyan.pl.db.entity.Product;
import com.simonyan.pl.db.handler.PlAsyncQueryHandler;

public class AddProductActivity extends BaseActivity implements 
        OnClickListener, 
        PlAsyncQueryHandler.AsyncQueryListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AddProductActivity.class.getSimpleName();
    public static final String NEW_PRODUCT = "New Product";
    private static final String PRODUCT_IMAGE = "http://3.bp.blogspot.com/_RTP7jI1D1mQ/S2BAxTEvMdI/AAAAAAAAAIw/5oN5pgLTADE/s320/D%C3%BCnyan%C4%B1n+en+mutsuz+bal%C4%B1%C4%9F%C4%B1,+EN+UMUTSUZ+BALI%C4%9EI+da+oldu+.jpg";


    // ===========================================================
    // Fields
    // ===========================================================


    private ImageView mIvProductImage;

    private EditText mEtProductName;

    private TextInputLayout mIlProductName;

    private EditText mEtProductPrice;

    private TextInputLayout mIlProductPrice;

    private EditText mEtProductDesc;

    private TextInputLayout mIlProductDesc;

    private Button mBtnProductAdd;

    private PlAsyncQueryHandler mTlAsyncQueryHandler;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        findViews();
        setListeners();
        customizeActionBar();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_product;
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.iv_activity_add_product_image:
                break;

            case R.id.btn_activity_add_product_add:
                addProduct();
                break;
        }

    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

        switch (token) {
            case PlAsyncQueryHandler.QueryToken.ADD_PRODUCT:
                Intent result = new Intent();
                result.putExtra(NEW_PRODUCT, (Product) cookie);
                setResult(RESULT_OK, result);
                finish();
                break;
        }

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }


    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mIvProductImage = (ImageView) findViewById(R.id.iv_activity_add_product_image);

        mEtProductName = (EditText) findViewById(R.id.et_activity_add_product_name);

        mIlProductName = (TextInputLayout) findViewById(R.id.il_activity_add_product_name);

        mEtProductPrice = (EditText) findViewById(R.id.et_activity_add_product_price);

        mIlProductPrice = (TextInputLayout) findViewById(R.id.il_activity_add_product_price);

        mEtProductDesc = (EditText) findViewById(R.id.et_activity_add_product_desc);

        mIlProductDesc = (TextInputLayout) findViewById(R.id.il_activity_add_product_desc);

        mBtnProductAdd = (Button) findViewById(R.id.btn_activity_add_product_add);
        mBtnProductAdd.setOnClickListener(this);
    }

    private void init() {
        mTlAsyncQueryHandler = new PlAsyncQueryHandler(getApplicationContext(), this);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.action_add_product));
    }

    private void addProduct() {

        if (!validate()) {
            return;
        }

        Product product = new Product();

        product.setId(System.currentTimeMillis());
        product.setImage(PRODUCT_IMAGE);
        product.setName(mEtProductName.getText().toString());
        product.setPrice(Integer.parseInt(mEtProductPrice.getText().toString()));
        product.setDescription(mEtProductDesc.getText().toString());




        mTlAsyncQueryHandler.addProduct(product);
    }

    private void setListeners() {
        mIvProductImage.setOnClickListener(this);
    }

    private boolean validate() {

        if (mEtProductName.getText().toString() == null || mEtProductName.getText().toString().equals("")) {
            mIlProductName.setHint(getString(R.string.name_error));
            return false;
        }

        if (mEtProductPrice.getText().toString() == null || mEtProductPrice.getText().toString().equals("")) {
            mIlProductPrice.setHint(getString(R.string.price_error));
            return false;
        }

        if (mEtProductDesc.getText().toString() == null || mEtProductDesc.getText().toString().equals("")) {
            mIlProductDesc.setHint(getString(R.string.desc_error));
            return false;
        }

        return true;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
