package com.example.evgeniy.scanner;

import android.os.Parcel;
import android.os.Parcelable;

class Person implements Parcelable {
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Person createFromParcel(Parcel in) {
                    return new Person(in);
                }

                public Person[] newArray(int size) {
                    return new Person[size];
                }
            };
    private final String timestamp;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String email;
    private final String address;
    private final String picture;
    private int id = -1;

    Person(String timestamp, String firstName, String lastName, String phone, String email, String address, String picture) {
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.picture = picture;
    }

    Person(int id, String timestamp, String firstName, String lastName, String phone, String email, String address, String picture) {
        this.id = id;
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.picture = picture;
    }

    private Person(Parcel in) {
        this.id = in.readInt();
        this.timestamp = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.address = in.readString();
        this.picture = in.readString();
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getTimestamp() {
        return timestamp;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getPhone() {
        return phone;
    }

    String getEmail() {
        return email;
    }

    String getAddress() {
        return address;
    }

    // Parcel stuff

    String getPicture() {
        return picture;
    }

    String getFullName() {
        return getFirstName()+" "+getLastName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // FIFO order
        dest.writeInt(this.id);
        dest.writeString(this.timestamp);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.address);
        dest.writeString(this.picture);
    }
}
