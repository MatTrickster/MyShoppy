package com.e.myshoppy;

import java.util.List;

public class OrderParentItem {

    private String number;
    private List<ShoppingItem> ChildItemList;
    private String price;

    public OrderParentItem(String number, List<ShoppingItem> ChildItemList, String price) {

        this.price = price;
        this.number = number;
        this.ChildItemList = ChildItemList;
    }

    public List<ShoppingItem> getChildItemList() {
        return ChildItemList;
    }

    public void setChildItemList(List<ShoppingItem> childItemList) {
        ChildItemList = childItemList;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }
}
