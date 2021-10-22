package com.android.aaroo.fragment.customer;

public class CustomerModel {
    public String cid;
    public String name;
    public String lastMsg;
    public double amount;
    public String mobile;
    public String profile;
    public String status;
    public int customerType;


    public CustomerModel() {
    }

    public CustomerModel(String cid, String name, String lastMsg, double amount, String profile, String status) {
        this.cid = cid;
        this.name = name;
        this.lastMsg = lastMsg;
        this.amount = amount;
        this.profile = profile;
        this.status = status;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
