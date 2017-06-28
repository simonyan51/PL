package com.simonyan.pl.db.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by simonyan51 on 6/28/17.
 */

public class ProductResponse {

    @SerializedName("products")
    private ArrayList<Product> products;

    public ProductResponse() {

    }

    public ProductResponse(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
