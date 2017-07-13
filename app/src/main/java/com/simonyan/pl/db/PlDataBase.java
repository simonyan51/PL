package com.simonyan.pl.db;

import android.content.ContentValues;

import com.simonyan.pl.db.entity.Product;

import java.util.ArrayList;

/**
 * Created by simonyan51 on 7/5/17.
 */

public class PlDataBase {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = PlDataBase.class.getName();

    public static class ContentValuesType {
        public static final String PRODUCTS = "PRODUCTS";
        public static final String DESCRIPTION = "DESCRIPTION";
    }

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * TABLES
     ***************************************************************/

    public static final String PRODUCT_TABLE = "PRODUCT_TABLE";

    public static final String PRODUCT_PK = "_id";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String PRODUCT_NAME = "PRODUCT_NAME";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String PRODUCT_IMAGE = "PRODUCT_IMAGE";
    public static final String PRODUCT_FAVORITE = "PRODUCT_FAVORITE";
    public static final String PRODUCT_USER = "PRODUCT_USER";

    public static final String PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";

    public static final String CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE
            + " ("
            + PRODUCT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCT_ID + " INTEGER UNIQUE, "
            + PRODUCT_NAME + " TEXT, "
            + PRODUCT_PRICE + " INTEGER, "
            + PRODUCT_IMAGE + " TEXT, "
            + PRODUCT_FAVORITE + " INTEGER, "
            + PRODUCT_USER + " INTEGER, "
            + PRODUCT_DESCRIPTION + " TEXT "
            + ");";

    /**
     * PROJECTIONS
     ***************************************************************/

    public static class Projection {
        public static String[] PRODUCT = {
                PlDataBase.PRODUCT_PK,
                PlDataBase.PRODUCT_ID,
                PlDataBase.PRODUCT_NAME,
                PlDataBase.PRODUCT_PRICE,
                PlDataBase.PRODUCT_IMAGE,
                PlDataBase.PRODUCT_FAVORITE,
                PlDataBase.PRODUCT_USER,
                PlDataBase.PRODUCT_DESCRIPTION
        };
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * VALUES
     ***************************************************************/

    public static ContentValues composeValues(Object object, String type) {
        ContentValues values = new ContentValues();
        Product product = (Product) object;

        switch (type) {
            case ContentValuesType.PRODUCTS:
                values.put(PlDataBase.PRODUCT_ID, product.getId());
                values.put(PlDataBase.PRODUCT_NAME, product.getName());
                values.put(PlDataBase.PRODUCT_PRICE, product.getPrice());
                values.put(PlDataBase.PRODUCT_IMAGE, product.getImage());
                values.put(PlDataBase.PRODUCT_FAVORITE, product.isFavorite());
                values.put(PlDataBase.PRODUCT_USER, product.isUserProduct());
                values.put(PlDataBase.PRODUCT_DESCRIPTION, product.getDescription());
                break;

            case ContentValuesType.DESCRIPTION:
                values.put(PlDataBase.PRODUCT_DESCRIPTION, product.getDescription());
                break;
        }
        return values;
    }

    public static ContentValues[] composeValuesArray(ArrayList<?> objects, String table) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ArrayList<Product> users = (ArrayList<Product>) objects;

        switch (table) {
            case ContentValuesType.PRODUCTS:
                for (Product user : users) {
                    ContentValues values = new ContentValues();
                    values.put(PlDataBase.PRODUCT_ID, user.getId());
                    values.put(PlDataBase.PRODUCT_NAME, user.getName());
                    values.put(PlDataBase.PRODUCT_PRICE, user.getPrice());
                    values.put(PlDataBase.PRODUCT_IMAGE, user.getImage());
                    values.put(PlDataBase.PRODUCT_FAVORITE, user.isFavorite());
                    values.put(PlDataBase.PRODUCT_USER, user.isUserProduct());
                    values.put(PlDataBase.PRODUCT_DESCRIPTION, user.getDescription());
                    valuesList.add(values);
                }
                break;
        }
        return valuesList.toArray(new ContentValues[valuesList.size()]);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}