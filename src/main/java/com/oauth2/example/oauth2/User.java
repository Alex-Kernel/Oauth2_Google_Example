package com.oauth2.example.oauth2;

public class User {
    public User(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
    private final String id;
    private String name;
    private String surname;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
