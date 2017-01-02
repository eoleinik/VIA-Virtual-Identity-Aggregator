package com.example.evgeniy.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    private final String facebook;
    private Bitmap bitmap = null;
    private int id = -1;

    Person(String timestamp, String firstName, String lastName, String phone, String email, String address, String picture, String facebook) {
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.picture = picture;
        this.facebook = facebook;
    }

    Person(int id, String timestamp, String firstName, String lastName, String phone, String email, String address, String picture, String facebook) {
        this.id = id;
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.picture = picture;
        this.facebook = facebook;
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
        this.facebook = in.readString();
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

    String getFacebook() {
        return facebook;
    }

    // Parcel stuff

    String getPicture() {
        return picture;
    }

    Bitmap getBitmap(Context context) {
        if (bitmap == null) {
            FileInputStream in;

            if (picture.equals(""))
                return null;

            try {
                File sd = context.getFilesDir();
                File file = new File(sd, picture);
                in = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(in);
            } catch (FileNotFoundException e) {
                System.out.println("Bitmap not found...");
            }
        }

        return bitmap;
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
        dest.writeString(this.facebook);
    }
}
