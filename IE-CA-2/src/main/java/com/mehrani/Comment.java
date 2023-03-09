package com.mehrani;

import java.time.LocalDate;

public class Comment {
    private int commentId;
    private String userEmail;
    private int commodityId;
    private String text;
    private LocalDate date;
    private int likesNo;
    private int dislikesNo;
    private int neutralVotesNo;

    Comment(int commentId, String userEmail, int commodityId, String text, String date) {
        this.commentId = commentId;
        this.userEmail = userEmail;
        this.commodityId = commodityId;
        this.text = text;
        this.date = LocalDate.parse(date);
        this.likesNo = 0;
        this.dislikesNo = 0;
        this.neutralVotesNo = 0;
    }
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }
    public void setLikesNo(int likesNo) {
        this.likesNo = likesNo;
    }
    public void setDislikesNo(int dislikesNo) {
        this.dislikesNo = dislikesNo;
    }
    public void setNeutralVotesNo(int neutralVotesNo) {
        this.neutralVotesNo = neutralVotesNo;
    }
    public void addLike() {
        likesNo++;
    }
    public void removeLike() {
        likesNo--;
    }
    public void addDislike() {
        dislikesNo++;
    }
    public void removeDislike() {
        dislikesNo--;
    }
    public void addNeutralVote() {
        neutralVotesNo++;
    }
    public void removeNeutralVote() {
        neutralVotesNo--;
    }
    public int getCommentId() {
        return commentId;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public int getCommodityId() {
        return commodityId;
    }
    public String getText() {
        return text;
    }
    public LocalDate getDate() {
        return date;
    }
    public int getLikesNo() {
        return likesNo;
    }
    public int getDislikesNo() {
        return dislikesNo;
    }
    public int getNeutralVotesNo() {
        return neutralVotesNo;
    }
}
