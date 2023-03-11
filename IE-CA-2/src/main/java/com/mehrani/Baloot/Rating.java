package com.mehrani.Baloot;

public class Rating {
    private String username;
    private int commodityId;
    private int score;

    public void setData(String username, int commodityId, int score) {
        this.username = username;
        this.commodityId = commodityId;
        this.score = score;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }
    public int getCommodityId() {
        return commodityId;
    }
}
