package com.pisco.deydemv3;

public class CourseModel {
    public int id;
    public String pickup, dropoff, status, phone;
    public int price;

    public CourseModel(int id, String pickup, String dropoff,int price, String status, String phone) {
        this.id = id;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.status = status;
        this.price = price;
        this.phone = phone;
    }


}

