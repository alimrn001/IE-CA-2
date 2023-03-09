package com.mehrani;

import java.time.LocalDate;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private LocalDate birthday;
    private String email;
    private String address;
    private double credit;
    private ArrayList<Integer> buyList = new ArrayList<>();

    void setUserData(String username, String password, String birthday, String email, String address, double credit) {
        this.username = username;
        this.password = password;
        this.birthday = LocalDate.parse(birthday);
        this.email = email;
        this.address = address;
        this.credit = credit;
    }
    void setUsername(String username) {
        this.username = username;
    }
    void setPassword(String password) {
        this.password = password;
    }
    public void setBirthday(String birthday) {
        this.birthday = LocalDate.parse(birthday);
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCredit(double credit) {
        this.credit = credit;
    }
    public void setBuyList(ArrayList<Integer> buyList) {
        this.buyList = buyList;
    }
    public void addToBuyList(int commodityId) {
        this.buyList.add(commodityId);
    }
    public void removeFromBuyList(int commodityId) {
        buyList.remove(Integer.valueOf(commodityId));
    }
    public boolean itemExistsInBuyList(int commodityId) {
        return buyList.contains(commodityId);
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public double getCredit() {
        return credit;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public ArrayList<Integer> getBuyList() {
        return buyList;
    }
}
