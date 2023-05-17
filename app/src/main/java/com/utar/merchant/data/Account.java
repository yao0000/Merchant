package com.utar.merchant.data;

import java.util.List;

public class Account{
    private String name;
    private String email;
    private String balance;


    public Account(){

    }

    public Account(String name, String email, String balance) {
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    public Account(String name, String email) {
        this.name = name;
        this.email = email;
        this.balance = "0.00";

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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
}