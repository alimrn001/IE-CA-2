package com.mehrani.Baloot.Data;

import java.time.LocalDate;
import java.util.ArrayList;

public class User {

    private String username;

    private String password;

    private LocalDate birthDate;

    private String email;

    private String address;

    private double credit;

    private ArrayList<Integer> buyList;

    private ArrayList<Integer> commentsList;

    private ArrayList<Integer> purchasedList;

    private ArrayList<Integer> likedComments;

    private ArrayList<Integer> dislikedComments;


    public User(String username, String password, String birthDate, String email, String address, double credit) {
        this.username = username;
        this.password = password;
        this.birthDate = LocalDate.parse(birthDate);
        this.email = email;
        this.address = address;
        this.credit = credit;
        buyList = new ArrayList<>();
        commentsList = new ArrayList<>();
        purchasedList = new ArrayList<>();
        likedComments = new ArrayList<>();
        dislikedComments = new ArrayList<>();
    }

    public void initializeGsonNullValues() {
        buyList = new ArrayList<>();
        commentsList = new ArrayList<>();
        purchasedList = new ArrayList<>();
        likedComments = new ArrayList<>();
        dislikedComments = new ArrayList<>();
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

    public void addCredit(double creditAmount) {
        this.credit += creditAmount;
    }

    public void reduceCredit(double creditAmount) {
        this.credit -= creditAmount;
    }

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

    public boolean userHasLikedComment(int commentId) {
        return likedComments.contains(commentId);
    }

    public boolean userHasDislikedComment(int commentId) {
        return dislikedComments.contains(commentId);
    }

    public void purchaseBuyList(double purchasePrice) {
        for(Integer buyListItemId : this.buyList)
            purchasedList.add(buyListItemId);
        this.buyList.clear();
        this.credit -= purchasePrice;
    }

    public void setCommentsList(ArrayList<Integer> commentsList) {
        this.commentsList = commentsList;
    }

    public void setLikedComments(ArrayList<Integer> likedComments) {
        this.likedComments = likedComments;
    }

    public void setDislikedComments(ArrayList<Integer> dislikedComments) {
        this.dislikedComments = dislikedComments;
    }

    public void addCommentReference(int commentId) {
        this.commentsList.add(commentId);
    }

    public void deleteCommentReference(int commentId) {
       this.commentsList.remove(Integer.valueOf(commentId));
    }

    public void removeLikeFromComment(int commentId) {
        this.likedComments.remove(Integer.valueOf(commentId));
    }

    public void removeDislikeFromComment(int commentId) {
        this.dislikedComments.remove(Integer.valueOf(commentId));
    }

    public void addCommentToLikedList(int commentId) {
        if(!userHasLikedComment(commentId)) {
            if(userHasDislikedComment(commentId))
                removeDislikeFromComment(commentId);
            likedComments.add(commentId);
        }
    }

    public void addCommentToDislikedList(int commentId) {
        if(!userHasDislikedComment(commentId)) {
            if(userHasLikedComment(commentId))
                removeLikeFromComment(commentId);
            dislikedComments.add(commentId);
        }
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

    public ArrayList<Integer> getLikedComments() {
        return likedComments;
    }

    public ArrayList<Integer> getDislikedComments() {
        return dislikedComments;
    }

}
