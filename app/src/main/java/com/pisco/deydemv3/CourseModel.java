package com.pisco.deydemv3;

import java.io.Serializable;

public class CourseModel implements Serializable {
    public int id;
    public String pickup, dropoff, status, phone, created;
    public int price;
    public double pickupLat;
    public double pickupLng;
    public double dropLat;
    public double dropLng;

    public CourseModel(int id, String pickup, String dropoff,int price, String status, String phone, String created) {
        this.id = id;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.status = status;
        this.price = price;
        this.phone = phone;
        this.created = created;
    }


    public CourseModel(int id, String pickup, String dropoff, int price, String status, String phone,double pickupLat,
                       double pickupLng,
                       double dropLat,
                       double dropLng) {
        this.id = id;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.status = status;
        this.price = price;
        this.phone = phone;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.dropLat = dropLat;
        this.dropLng = dropLng;
    }
}

