package com.example.evgeniy.scanner;

class Person {
    private int id = -1;
    private String timestamp;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;

    Person(String timestamp, String firstName, String lastName, String phone, String email, String address) {
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    Person(int id, String timestamp, String firstName, String lastName, String phone, String email, String address) {
        this.id = id;
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
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
}
