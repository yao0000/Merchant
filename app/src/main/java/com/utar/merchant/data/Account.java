package com.utar.merchant.data;

import java.util.List;

public class Account{
    private String name;
    private String email;
    private List<Transaction> transactionList;

    public Account(){

    }

    public Account(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}