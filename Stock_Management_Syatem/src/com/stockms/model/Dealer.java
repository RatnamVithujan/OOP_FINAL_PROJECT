package com.stockms.model;

public class Dealer {
    private int dealerId;
    private String name;
    private String contactNo;
    private String address;

    public Dealer() {}

    public Dealer(int dealerId, String name, String contactNo, String address) {
        this.dealerId = dealerId;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
    }

    // Getters and Setters
    public int getDealerId() { return dealerId; }
    public void setDealerId(int dealerId) { this.dealerId = dealerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}