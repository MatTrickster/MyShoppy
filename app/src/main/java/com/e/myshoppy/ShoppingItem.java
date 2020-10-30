package com.e.myshoppy;

import android.net.Uri;

import java.io.Serializable;
import java.text.NumberFormat;

public class ShoppingItem implements Serializable {

    private String name, type, description, productID,shopId,price;
    private int quantity;
    private String path;

    public ShoppingItem(String productId, String name, String type, String description, String price, int quantity,
                        String shopId, String path){
        this.productID = productId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.shopId = shopId;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public String getProductID() {
        return productID;
    }

    public String getTitle() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

}
