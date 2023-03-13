package com.mehrani.Baloot;

import java.time.LocalDate;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private LocalDate birthDate;
    private String email;
    private String address;
    private double credit;
    private ArrayList<Integer> buyList = new ArrayList<>();
    private ArrayList<Integer> commentsList = new ArrayList<>();
    private ArrayList<Integer> purchasedList = new ArrayList<>();

    public void setUserData(String username, String password, String birthday, String email, String address, double credit) {
        this.username = username;
        this.password = password;
        this.birthDate = LocalDate.parse(birthday);
        this.email = email;
        this.address = address;
        this.credit = credit;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setBirthday(String birthday) {
        this.birthDate = LocalDate.parse(birthday);
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
    public void setPurchasedList(ArrayList<Integer> purchasedList) {
        this.purchasedList = purchasedList;
    }
    public void addCredit(double creditAmount) {this.credit += creditAmount;}
    public void reduceCredit(double creditAmount) {this.credit -= creditAmount;}
    public void setBuyList(ArrayList<Integer> buyList) {
        this.buyList = buyList;
    }
    public void addToBuyList(int commodityId) {
        this.buyList.add(commodityId);
    }
    public void removeFromBuyList(int commodityId) {
        this.buyList.remove(Integer.valueOf(commodityId));
    }
    public boolean itemExistsInBuyList(int commodityId) {
        return buyList.contains(commodityId);
    }
    public void purchaseBuyList(double purchasePrice) {
        //you can also consider using a normal loop instead of addAll method
        //this.purchasedList.addAll(this.buyList); //might want to add a 0 argument in order to append latest purchased at the beginning of the list
        for(Integer buyListItemId : this.buyList)
            purchasedList.add(buyListItemId);
        this.buyList.clear();
        this.credit -= purchasePrice;
    }
    public void setCommentsList(ArrayList<Integer> commentsList) {
        this.commentsList = commentsList;
    }
    public void addCommentReference(int commentId) {
        this.commentsList.add(commentId);
    }
    public void deleteCommentReference(int commentId) {
       this.commentsList.remove(Integer.valueOf(commentId));
    }
    public LocalDate getBirthDate() {
        return birthDate;
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
    public ArrayList<Integer> getCommentsList() {
        return commentsList;
    }
    public ArrayList<Integer> getPurchasedList() {
        return purchasedList;
    }
}
