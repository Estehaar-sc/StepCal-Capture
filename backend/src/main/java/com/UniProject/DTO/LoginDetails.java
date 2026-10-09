package com.UniProject.DTO;

public class LoginDetails {
    private String email;
    private String password;

    public LoginDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginDetails() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDetails{" +
                "email='" + email + '\'' +
                ", password='[REDACTED]'" +
                '}';
    }
}