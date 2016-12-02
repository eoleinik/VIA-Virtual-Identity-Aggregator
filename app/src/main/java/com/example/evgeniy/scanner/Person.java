package com.example.evgeniy.scanner;

class Person {
    private int id = -1;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;

    Person(String firstName, String lastName, String phone, String email, String address) {
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
