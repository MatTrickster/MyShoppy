package com.e.myshoppy;

import com.google.android.gms.common.util.concurrent.NamedThreadFactory;

public class Shop {

    String Name;
    String imgUrl;
    String Address;
    String id;

    public Shop(String Name,String Address, String url,String id){
        this.Name = Name;
        this.Address = Address;
        imgUrl = url;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String sName) {
        this.Name = sName;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
