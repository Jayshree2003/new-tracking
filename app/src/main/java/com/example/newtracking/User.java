package com.example.newtracking;

public class User {

    public String email;
    public String role;
    public String name, crn,phNumber;

    public User() {}

    public User(String name, String crn, String email, String role,String phNumber) {
        this.name = name;
        this.crn = crn;
        this.email = email;
        this.role = role;
        this.phNumber=phNumber;
    }
}
