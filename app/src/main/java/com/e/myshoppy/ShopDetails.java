package com.e.myshoppy;

public class ShopDetails {

    String sName;
    String sKName;
    String sRegNumber;
    String sCity;
    String email;
    String Code;
    String contact;
    String charge;

    public ShopDetails(String sName,String sKName,String sRegNumber,String sCity,String contact,
                       String email,String charge){

        this.sName = sName;
        this.sKName = sKName;
        this.sRegNumber = sRegNumber;
        this.sCity = sCity;
        this.contact = contact;
        this.email = email;
        this.charge = charge;

    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getCharge() {
        return charge;
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
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getsCity() {
        return sCity;
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

    public void setsCity(String sCity) {
        this.sCity = sCity;
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
