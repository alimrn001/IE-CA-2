package com.mehrani.Baloot;

import java.time.LocalDate;
import java.util.ArrayList;

public class Provider {
    private int id;
    private String name;
    private LocalDate registryDate;
    private int commoditiesNum;
    private double avgCommoditiesRate;
    private ArrayList<Integer> commoditiesProvided = new ArrayList<>();

    public void setData(int id, String name, String registryDate, boolean update) {
        if(!update) {
            this.commoditiesNum = 0;
            this.avgCommoditiesRate = 0;
        }
        this.id = id;
        this.name = name;
        this.registryDate = LocalDate.parse(registryDate);
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRegistryDate(String registryDate) {
        this.registryDate = LocalDate.parse(registryDate);
    }
    public void setCommoditiesNum(int commoditiesNum) {
        this.commoditiesNum = commoditiesNum;
    }
    public void setAvgCommoditiesRate(double avgCommoditiesRate) {
        this.avgCommoditiesRate = avgCommoditiesRate;
    }
    public void addProvidedCommodity(int commId) {
        commoditiesProvided.add(commId);
    }
    public void setCommoditiesProvided(ArrayList<Integer> commoditiesProvided) {
        this.commoditiesProvided = commoditiesProvided;
    }
    public void updateCommoditiesData(double newCommodityRating) {
        this.commoditiesNum ++;
        this.avgCommoditiesRate = ((this.avgCommoditiesRate*(this.commoditiesNum-1)) + newCommodityRating)/(this.commoditiesNum);
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public LocalDate getRegistryDate() {
        return registryDate;
    }
    public int getCommoditiesNum() {
        return commoditiesNum;
    }
    public double getAvgCommoditiesRate() {
        return avgCommoditiesRate;
    }
    public ArrayList<Integer> getCommoditiesProvided() {
        return commoditiesProvided;
    }
}
