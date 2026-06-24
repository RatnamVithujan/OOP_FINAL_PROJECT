package com.stockms.model;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactNo;
    private String address;

    public Supplier() {}

    public Supplier(int supplierId, String name, String contactNo, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
    }

    // Getters and Setters
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}