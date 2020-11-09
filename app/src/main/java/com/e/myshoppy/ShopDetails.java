package com.e.myshoppy;

public class ShopDetails {

    String sName;
    String sKName;
    String sRegNumber;
    String sAddress;
    String Email;
    String Code;
    String contact;

    public ShopDetails(String sName,String sKName,String sRegNumber,String sAddress,String contact){

        this.sName = sName;
        this.sKName = sKName;
        this.sRegNumber = sRegNumber;
        this.sAddress = sAddress;
        this.contact = contact;

    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCode() {
        return Code;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getsAddress() {
        return sAddress;
    }

    public String getsKName() {
        return sKName;
    }

    public String getsName() {
        return sName;
    }

    public String getsRegNumber() {
        return sRegNumber;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public void setsKName(String sKName) {
        this.sKName = sKName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setsRegNumber(String sRegNumber) {
        this.sRegNumber = sRegNumber;
    }

}
