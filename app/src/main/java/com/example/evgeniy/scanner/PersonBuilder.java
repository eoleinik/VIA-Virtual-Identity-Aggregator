package com.example.evgeniy.scanner;

import android.os.Parcel;

/**
 * Uses Builder pattern for safe object creation with named and default values.
 */
public class PersonBuilder {
    private String timestamp = null;
    private String firstName = "Jim";
    private String lastName = "Moriarty";
    private String phone = "+123456789";
    private String email = "jim.moriarty@gmail.com";
    private String address = "221A Baker Street, London, UK";
    private String picture = "blank-avatar_bbcu5o.png";
    private String facebook = "";
    private String twitter = "";
    private int id = 123;

    public PersonBuilder timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public PersonBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public PersonBuilder email(String email) {
        this.email = email;
        return this;
    }

    public PersonBuilder address(String address) {
        this.address = address;
        return this;
    }

    public PersonBuilder picture(String picture) {
        this.picture = picture;
        return this;
    }

    public PersonBuilder facebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public PersonBuilder twitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public PersonBuilder id(int id) {
        this.id = id;
        return this;
    }

    public Person buildPerson() {
        return new Person(id, timestamp, firstName, lastName, phone, email, address, picture, facebook, twitter);
    }
}