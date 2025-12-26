package com.pisco.deydemv3;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CourseModel implements Parcelable {

    public int id;
    public String pickup;
    public String dropoff;
    public String status;
    public String phone;
    public int driverId;
    public String created;
    public int price;

    public double pickupLat;
    public double pickupLng;
    public double dropLat;
    public double dropLng;

    // ✅ Constructeur normal (dashboard)
    public CourseModel(int id, String pickup, String dropoff,
                       int price, String status, String phone, String created) {
        this.id = id;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.price = price;
        this.status = status;
        this.phone = phone;
        this.created = created;
    }

    // ✅ Constructeur avec coordonnées (map)
    public CourseModel(int id, String pickup, String dropoff,
                       int price, String status, String phone,
                       double pickupLat, double pickupLng,
                       double dropLat, double dropLng, int driverId) {

        this.id = id;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.price = price;
        this.status = status;
        this.phone = phone;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.dropLat = dropLat;
        this.dropLng = dropLng;
        this.driverId = driverId;
    }

    // ✅ Constructeur Parcelable (LECTURE)
    protected CourseModel(Parcel in) {
        id = in.readInt();
        pickup = in.readString();
        dropoff = in.readString();
        status = in.readString();
        phone = in.readString();
        created = in.readString();
        price = in.readInt();

        pickupLat = in.readDouble();
        pickupLng = in.readDouble();
        dropLat = in.readDouble();
        dropLng = in.readDouble();
        driverId = in.readInt();
    }

    // ✅ OBLIGATOIRE
    public static final Creator<CourseModel> CREATOR = new Creator<CourseModel>() {
        @Override
        public CourseModel createFromParcel(Parcel in) {
            return new CourseModel(in);
        }

        @Override
        public CourseModel[] newArray(int size) {
            return new CourseModel[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    // ✅ ÉCRITURE complète
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(pickup);
        dest.writeString(dropoff);
        dest.writeString(status);
        dest.writeString(phone);
        dest.writeString(created);
        dest.writeInt(price);
        dest.writeDouble(pickupLat);
        dest.writeDouble(pickupLng);
        dest.writeDouble(dropLat);
        dest.writeDouble(dropLng);
        dest.writeInt(driverId);
    }
}
