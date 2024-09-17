package com.example.videp_v1.users;

public class VidepUsersClass implements UserAuthentication {
    private String firstName;
    private String lastName;
    private String facultyOfUniversity;
    private String userName;
    private String password;
    private String email;
    private boolean isLogged;
    private String uid;

    public VidepUsersClass() {

    }

    public VidepUsersClass(String firstName, String lastName, String facultyOfUniversity, String userName, String password, String email, Boolean isLogged) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.facultyOfUniversity = facultyOfUniversity;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.isLogged = isLogged;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacultyOfUniversity() {
        return facultyOfUniversity;
    }

    public void setFacultyOfUniversity(String facultyOfUniversity) {
        this.facultyOfUniversity = facultyOfUniversity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    @Override
    public String getAuthenticationUserName() {
        return userName;
    }

    @Override
    public String getAuthenticationEmail() {
        return email;
    }
}