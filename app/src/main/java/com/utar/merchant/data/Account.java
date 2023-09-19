package com.utar.merchant.data;

import java.util.List;

public class Account{
    public static final String FIX_MERCHANT = "merchant";

    private String name;
    private String email;
    private String balance;
    private String role;
    private String password;

    private String deviceId;
    private String pin;


    public Account(){

    }

    public Account(String name, String email, String password, String id) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = "0.00";
        this.role = FIX_MERCHANT;
        this.deviceId = id;
        this.pin = "";
    }

    public String getPassword(){return password;}

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }


    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBalance() {
        return balance;
    }

    public String getRole() {
        return role;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public void setDeviceId(String id){
        this.deviceId = id;
    }

    public String getPin(){
        return this.pin;
    }
}