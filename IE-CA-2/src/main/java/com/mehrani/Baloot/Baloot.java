package com.mehrani.Baloot;

import com.google.gson.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Baloot {
    private Map<String, User> balootUsers = new HashMap<>();
    private Map<Integer, Commodity> balootCommodities = new HashMap<>();
    private Map<Integer, Provider> balootProviders = new HashMap<>();
    private Map<String, Rating> balootRatings = new HashMap<>();
    private Map<String, Category> balootCategorySections = new HashMap<>();
    private Error error = new Error();

    public boolean commodityExists(int commodityId) {
        return balootCommodities.containsKey(commodityId);
    }
    public boolean userExists(String username) {
        return balootUsers.containsKey(username);
    }
    public boolean categoryExists(String category) {
        return balootCategorySections.containsKey(category);
    }
    public boolean providerExists(int providerId) {
        return balootProviders.containsKey(providerId);
    }
    public void updateCategorySection(String categoryName, int commodityId) {
        if(balootCategorySections.containsKey(categoryName)) {
            balootCategorySections.get(categoryName).addCommodityToCategory(commodityId);
        }
        else {
            Category category = new Category();
            category.setCategoryName(categoryName);
            category.addCommodityToCategory(commodityId);
            balootCategorySections.put(categoryName, category);
        }
    }
    public String addUser(User user) throws Exception {
        Response response = new Response();
        Gson gsonProvider = new GsonBuilder().create();
        if(balootUsers.containsKey(user.getUsername())) {
            user.setBuyList(balootUsers.get(user.getUsername()).getBuyList());
            balootUsers.put(user.getUsername(), user);
            response.setSuccess(true);
            response.setData("");
            return gsonProvider.toJson(response);
        }
        else {
            if((user.getUsername().contains("!")) || (user.getUsername().contains("#")) || (user.getUsername().contains("@"))) {
                response.setSuccess(false);
                response.setData(error.getUsernameWrongChar());
                throw new Exception(gsonProvider.toJson(response));
            }
            else {
                balootUsers.put(user.getUsername(), user);
                response.setSuccess(true);
                response.setData("");
                return gsonProvider.toJson(response);
            }
        }
    }
    public String addCommodity(Commodity commodity) throws Exception {
        Gson gsonCommodity = new GsonBuilder().create();
        Response response = new Response();
        if(!balootProviders.containsKey(commodity.getProviderId())) {
            response.setSuccess(false);
            response.setData(error.getProviderNotExists());
            throw new Exception(gsonCommodity.toJson(response));
        }
        else {
            if(!balootCommodities.containsKey(commodity.getId())) {
                balootProviders.get(commodity.getProviderId()).addProvidedCommodity(commodity.getId());
                for (String ctgr : commodity.getCategories()) {
                    updateCategorySection(ctgr, commodity.getId());
                }
                balootCommodities.put(commodity.getId(), commodity);
                response.setSuccess(true);
                response.setData("");
                balootProviders.get(commodity.getProviderId()).updateCommoditiesData(commodity.getRating());
                return gsonCommodity.toJson(response);
            }
            else {
                response.setSuccess(false);
                response.setData(error.getCommodityIdExists());
                throw new Exception(gsonCommodity.toJson(response));
            }
        }
    }
    public String addProvider(Provider provider) throws Exception {
        Response response = new Response();
        if(providerExists(provider.getId())) {
            balootProviders.get(provider.getId()).setName(provider.getName());
            balootProviders.get(provider.getId()).setRegistryDate(provider.getRegistryDate().toString());
        }
        else
            balootProviders.put(provider.getId(), provider);
        response.setSuccess(true);
        response.setData("");
        Gson gsonProvider = new GsonBuilder().create();
        return gsonProvider.toJson(response);
    }
    public String addRemoveBuyList(String username, int commodityId, boolean isAdding) throws Exception {
        Response response = new Response();
        Gson gsonaddRemove = new GsonBuilder().create();
        if(!userExists(username)) {
            response.setSuccess(false);
            response.setData(error.getUserNotExists());
            throw new Exception(gsonaddRemove.toJson(response));
        }
        if(!commodityExists(commodityId)) {
            response.setSuccess(false);
            response.setData(error.getCommodityNotExists());
            throw new Exception(gsonaddRemove.toJson(response));
        }
        else if(balootCommodities.get(commodityId).getInStock()==0 && isAdding) {
            response.setSuccess(false);
            response.setData(error.getProductNotInStorage());
            throw new Exception(gsonaddRemove.toJson(response));
        }
        if(balootUsers.get(username).itemExistsInBuyList(commodityId)) {
            if(isAdding) {
                response.setSuccess(false);
                response.setData(error.getProductAlreadyExistsInBuyList());
                throw new Exception(gsonaddRemove.toJson(response));
            }
            else {
                balootUsers.get(username).removeFromBuyList(commodityId);
                balootCommodities.get(commodityId).reduceInStock(-1);
                response.setSuccess(true);
                response.setData("");
                return gsonaddRemove.toJson(response);
            }
        }
        else {
            if(!isAdding) {
                response.setSuccess(false);
                response.setData(error.getProductNotInBuyList());
                throw new Exception(gsonaddRemove.toJson(response));
            }
        }
        response.setSuccess(true);
        response.setData("");
        balootUsers.get(username).addToBuyList(commodityId);
        balootCommodities.get(commodityId).reduceInStock(1);
        return gsonaddRemove.toJson(response);
    }
    public String getCommoditiesByCategory(String category) {
        Response response = new Response();
        JsonObject responseObject = new JsonObject();
        JsonObject commoditiesListObject = new JsonObject();
        if(!categoryExists(category)) {
            JsonArray emptyCommodityList = new JsonArray();
            commoditiesListObject.add("commoditiesListByCategory", emptyCommodityList);
            responseObject.addProperty("success", true);
            responseObject.add("data", new Gson().toJsonTree(commoditiesListObject));
            return responseObject.toString();
        }

        JsonArray commoditiesList = new JsonArray();
        for (int commId :balootCategorySections.get(category).getCommodities()) {
            JsonObject commObj = new JsonObject();
            Commodity commodity = balootCommodities.get(commId);
            commObj.addProperty("id", commodity.getId());
            commObj.addProperty("name", commodity.getName());
            commObj.addProperty("providerId", commodity.getProviderId());
            commObj.addProperty("price", commodity.getPrice());
            JsonArray categoriesList = new JsonArray();
            for(String itemCategory : commodity.getCategories())
                categoriesList.add(new JsonPrimitive(itemCategory));
            commObj.add("categories", categoriesList);
            commObj.addProperty("rating", commodity.getRating());
            commoditiesList.add(commObj);
        }
        commoditiesListObject.add("commoditiesListByCategory", commoditiesList);
        responseObject.addProperty("success", true);
        responseObject.add("data", new Gson().toJsonTree(commoditiesListObject));
        return responseObject.toString();
    }
    public String getCommodityById(int commodityId) throws Exception {
        Response response = new Response();
        Gson gsonObj = new GsonBuilder().create();
        JsonObject responseObj = new JsonObject();
        if(!commodityExists(commodityId)) {
            response.setSuccess(false);
            response.setData(new Error().getCommodityNotExists());
            throw new Exception(gsonObj.toJson(response));
        }
        Commodity commodity = balootCommodities.get(commodityId);
        JsonArray commoditiesList = new JsonArray();
        for(String category : commodity.getCategories())
            commoditiesList.add(new JsonPrimitive(category));

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", commodity.getId());
        jsonObj.addProperty("name", commodity.getName());
        String providerName = balootProviders.get(commodity.getProviderId()).getName();
        jsonObj.addProperty("provider", providerName);
        jsonObj.addProperty("price", commodity.getPrice());
        jsonObj.add("categories", commoditiesList);
        jsonObj.addProperty("rating", commodity.getRating());

        responseObj.addProperty("success", true);
        responseObj.add("data", new Gson().toJsonTree(jsonObj));
        return responseObj.toString();
    }
    public String getCommoditiesList() {
        Gson gson = new GsonBuilder().create();
        JsonObject responseObject = new JsonObject();
        JsonObject commoditiesListObject = new JsonObject();
        JsonArray commoditiesList = new JsonArray();
        for(Commodity commodity : balootCommodities.values()) {
            JsonObject commObject = new JsonObject();
            commObject.addProperty("id", commodity.getId());
            commObject.addProperty("name", commodity.getName());
            commObject.addProperty("providerId", commodity.getProviderId());
            commObject.addProperty("price", commodity.getPrice());
            JsonArray categoryList = new JsonArray();
            for(String ctgr : commodity.getCategories())
                categoryList.add(new JsonPrimitive(ctgr));
            commObject.add("categories", categoryList);
            commObject.addProperty("rating", commodity.getRating());
            commoditiesList.add(commObject);
        }

        commoditiesListObject.add("commoditiesList", commoditiesList);
        responseObject.addProperty("success", true);
        responseObject.add("data", new Gson().toJsonTree(commoditiesListObject));
        return gson.toJson(responseObject);
    }
    public String getBuyList(String username) throws Exception {
        Gson gson = new GsonBuilder().create();
        JsonObject responseObject = new JsonObject();
        JsonObject commoditiesListObject = new JsonObject();
        JsonArray commoditiesList = new JsonArray();
        if(!userExists(username)) {
            Response response = new Response();
            response.setSuccess(false);
            response.setData(new Error().getUserNotExists());
            throw new Exception(gson.toJson(response));
        }
        ArrayList<Integer> userBuyList = balootUsers.get(username).getBuyList();
        for(int i : userBuyList) {
            Commodity commodity = balootCommodities.get(i);
            JsonObject commodityObj = new JsonObject();
            commodityObj.addProperty("id", commodity.getId());
            commodityObj.addProperty("name", commodity.getName());
            commodityObj.addProperty("providerId", commodity.getProviderId());
            commodityObj.addProperty("price", commodity.getPrice());
            JsonArray categoryList = new JsonArray();
            for(String ctgr : commodity.getCategories())
                categoryList.add(new JsonPrimitive(ctgr));
            commodityObj.addProperty("rating", commodity.getRating());
            commoditiesList.add(commodityObj);
        }
        commoditiesListObject.add("buyList", commoditiesList);
        responseObject.addProperty("success", true);
        responseObject.add("data", new Gson().toJsonTree(commoditiesListObject));
        return gson.toJson(responseObject);
    }
    public String addRating(Rating rating) throws Exception {
        Response response = new Response();
        Gson gsonRating = new GsonBuilder().create();
        if(rating.getScore() > 10 || rating.getScore() < 1) {
            response.setSuccess(false);
            response.setData(error.getRatingOutOfRange(rating.getScore()));
            throw new Exception(gsonRating.toJson(response));
        }
        else if(!userExists(rating.getUsername())) {
            response.setSuccess(false);
            response.setData(error.getUserNotExists());
            throw new Exception(gsonRating.toJson(response));
        }
        else if(!commodityExists(rating.getCommodityId())) {
            response.setSuccess(false);
            response.setData(error.getCommodityNotExists());
            throw new Exception(gsonRating.toJson(response));
        }
        else {
            String ratingKey = rating.getUsername() + "_" + rating.getCommodityId();
            if(!balootRatings.containsKey(ratingKey))
                balootCommodities.get(rating.getCommodityId()).addNewRating(rating.getScore());
            balootRatings.put(ratingKey, rating);
            response.setSuccess(true);
            response.setData("");
            return gsonRating.toJson(response);
        }
    }
    public Map<String, User> getBalootUsers() {
        return balootUsers;
    }
    public Map<Integer, Commodity> getBalootCommodities() {
        return balootCommodities;
    }
    public Map<Integer, Provider> getBalootProviders() {
        return balootProviders;
    }
    public Map<String, Rating> getBalootRatings() {
        return balootRatings;
    }
    public Map<String, Category> getBalootCategorySections() {
        return balootCategorySections;
    }
    public String checkUserCmd(String userInput) {

        String userCmd, userData;
        if(!userInput.contains(" ")) {
            userCmd = userInput;
            userData = "";
        }
        else {
            userCmd = userInput.substring(0, userInput.indexOf(" "));
            userData = userInput.substring(userInput.indexOf(" ") + 1);
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        switch (userCmd) {
            case "addUser" -> {
                try {
                    User user = gson.fromJson(userData, User.class);
                    return addUser(user);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "rateCommodity" -> {
                try {
                    Gson gsonCommodity = new GsonBuilder().create();
                    Rating rating = gsonCommodity.fromJson(userData, Rating.class);
                    return addRating(rating);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "addProvider" -> {
                try {
                    Provider provider = gson.fromJson(userData, Provider.class);
                    return addProvider(provider);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "addToBuyList" -> {
                try {
                    JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                    return addRemoveBuyList(jsonObject.get("username").getAsString(), jsonObject.get("commodityId").getAsInt(), true);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "removeFromBuyList" -> {
                try {
                    JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                    return addRemoveBuyList(jsonObject.get("username").getAsString(), jsonObject.get("commodityId").getAsInt(), false);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "addCommodity" -> {
                try {
                    Gson gson_ = new GsonBuilder().create();
                    Commodity commodity = gson_.fromJson(userData, Commodity.class);
                    return addCommodity(commodity);
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "getCommodityById" -> {
                try {
                    JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                    return getCommodityById(jsonObject.get("id").getAsInt());
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
            case "getCommoditiesByCategory" -> {
                JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                return getCommoditiesByCategory(jsonObject.get("category").getAsString());
            }
            case "getCommoditiesList" -> {
                return getCommoditiesList();
            }
            case "getBuyList" -> {
                try {
                    JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                    return getBuyList(jsonObject.get("username").getAsString());
                }
                catch (Exception e) {
                    return e.getMessage();
                }
            }
        }

        return "Wrong command!";
    }

}
