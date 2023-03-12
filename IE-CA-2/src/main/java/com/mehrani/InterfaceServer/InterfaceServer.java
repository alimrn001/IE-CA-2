package com.mehrani.InterfaceServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mehrani.Baloot.*;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import io.javalin.Javalin;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterfaceServer {
    private Javalin app;
    private HTTPReqHandler httpReqHandler = new HTTPReqHandler();
    private Baloot baloot = new Baloot();

    public void retrieveUsersDataFromAPI(String url) throws Exception {
        String userDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = gson.fromJson(userDataJsonStr, userListType);
        for (User user : userList)
            baloot.addUser(user);
        System.out.println("Total users in baloot : " + baloot.getBalootUsers().size());
    }
    public void retrieveProvidersDataFromAPI(String url) throws Exception {
        String providerDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type providerListType = new TypeToken<ArrayList<Provider>>(){}.getType();
        List<Provider> providerList = gson.fromJson(providerDataJsonStr, providerListType);
        for(Provider provider : providerList)
            baloot.addProvider(provider);
        System.out.println("Total providers in baloot : " + baloot.getBalootProviders().size());
    }
    public void retrieveCommoditiesDataFromAPI(String url) throws Exception {
        String commodityDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().create();
        Type commodityListType = new TypeToken<ArrayList<Commodity>>(){}.getType();
        List<Commodity> commodityList = gson.fromJson(commodityDataJsonStr, commodityListType);
        for(Commodity commodity : commodityList)
            baloot.addCommodity(commodity);
        System.out.println("Total commodities in baloot : " + baloot.getBalootCommodities().size());
        //System.out.println("Commodities : ");
//        for(Map.Entry<Integer, Commodity> commodityEntry : baloot.getBalootCommodities().entrySet()) {
//            System.out.println("id : " + commodityEntry.getValue().getId());
//            System.out.println("name : " + commodityEntry.getValue().getName());
//            System.out.println("provider id : " + commodityEntry.getValue().getProviderId());
//            System.out.println("price : " + commodityEntry.getValue().getPrice());
//            System.out.println("categories : " + commodityEntry.getValue().getCategories());
//            System.out.println("rating : " + commodityEntry.getValue().getRating());
//            System.out.println("in stock : " + commodityEntry.getValue().getInStock());
//            System.out.println("-------------");
//        }

    }
    public void retrieveCommentsDataFromAPI(String url) throws Exception {
        String commentDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type commentListType = new TypeToken<ArrayList<Comment>>(){}.getType();
        List<Comment> commentList = gson.fromJson(commentDataJsonStr, commentListType);
        for(Comment comment : commentList)
            baloot.addComment(comment);

        System.out.println("Total comments in baloot : " + baloot.getBalootComments().size());
        System.out.println("Comments");
        for(Map.Entry<Integer, Comment> commentEntry : baloot.getBalootComments().entrySet()) {
            System.out.println("-------------");
            System.out.println("id : " + commentEntry.getValue().getCommentId());
            System.out.println("user email : " + commentEntry.getValue().getUserEmail());
            System.out.println("commodity id : " + commentEntry.getValue().getCommodityId());
            System.out.println("text : " + commentEntry.getValue().getText());
            System.out.println("date : " + commentEntry.getValue().getDate().toString());
            System.out.println("likes : " + commentEntry.getValue().getLikesNo());
            System.out.println("dislikes : " + commentEntry.getValue().getDislikesNo());
            System.out.println("neutral votes : " + commentEntry.getValue().getNeutralVotesNo());
        }
    }

}
