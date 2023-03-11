package com.mehrani.Baloot;

public class Error {
    public String getUsernameWrongChar() {
        return "ERROR : Username can not contain characters like # @ ! ....";
    }
    public String getUserNotExists() {
        return "ERROR : The selected user does not exist !";
    }
    public String getCommodityNotExists() {
        return "ERROR : The selected commodity does not exist !";
    }
    public String getProductNotInStorage() {
        return "ERROR : The selected product does not exist in the storage !";
    }
    public String getProductAlreadyExistsInBuyList() {
        return "ERROR : The selected product already exists in buy list !";
    }
    public String getProductNotInBuyList() {
        return "ERROR : The selected product is not in buy list to be deleted !";
    }
    public String getProviderNotExists() {
        return "ERROR : The selected provider does not exist !";
    }
    public String getRatingOutOfRange(int rate) {
        return "ERROR : Rating must be a number between 0 and 10 !" + "(you entered : " + rate + ").";
    }
    public String getCommodityIdExists() {
        return "ERROR : There is already a commodity with this id in system !";
    }
}
