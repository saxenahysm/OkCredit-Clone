package com.android.aaroo.activity.transactionsupplier;

public class SupplierTransactionModel {

    public String txnId,addNote,customerID,paymentTime,paymentDate,billImage,createdBy,ipaddress,created_at,updated_at;

    public double amount;
    public int paymentMethod;

    public SupplierTransactionModel() {
    }

    public SupplierTransactionModel(String txnId, String addNote, String customerID, String paymentTime, String paymentDate, String billImage, String createdBy, String ipaddress, String created_at, String updated_at, double amount, int paymentMethod) {
        this.txnId = txnId;
        this.addNote = addNote;
        this.customerID = customerID;
        this.paymentTime = paymentTime;
        this.paymentDate = paymentDate;
        this.billImage = billImage;
        this.createdBy = createdBy;
        this.ipaddress = ipaddress;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAddNote() {
        return addNote;
    }

    public void setAddNote(String addNote) {
        this.addNote = addNote;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getBillImage() {
        return billImage;
    }

    public void setBillImage(String billImage) {
        this.billImage = billImage;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }
}
