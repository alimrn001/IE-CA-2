package com.mehrani.InterfaceServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Exceptions.UserNotExistsException;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import io.javalin.Javalin;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

public class InterfaceServer {
    private Javalin app;
    private HTTPReqHandler httpReqHandler = new HTTPReqHandler();
    private HtmlHandler htmlHandler = new HtmlHandler();
    private Baloot baloot = new Baloot();

    public void start(String usersURL, String ProvidersURL, String CommoditiesURL, String CommentsURL, int port) {
        try {
            retrieveUsersDataFromAPI(usersURL);
            retrieveProvidersDataFromAPI(ProvidersURL);
            retrieveCommoditiesDataFromAPI(CommoditiesURL);
            retrieveCommentsDataFromAPI(CommentsURL);
            runServer(port);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void stop() {
        // stop javalin
    }
    public Baloot getBaloot() {
        return baloot;
    }
    public void runServer(int port) throws Exception {
        app = Javalin.create().start(port);
        app.get("users/{user_id}", ctx -> {
            try {
                ctx.html(createUserHtmlPage(ctx.pathParam("user_id")));
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
                ctx.html(getHtmlContents("404.html"));
            }
        });
        app.get("addToBuyList/{username}/{commodityId}", ctx -> {
            try {
                String username = ctx.pathParam("username");
                String commodityId = ctx.pathParam("commodityId");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private String getHtmlContents(String fileName) throws Exception {
        File file = new File(Resources.getResource("templates/" + fileName).toURI());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
    private String createUserHtmlPage(String username) throws Exception {
        if(!baloot.userExists(username))
            throw new UserNotExistsException();

        String userPageHtmlStr = getHtmlContents("userPages/UserInfo.html");
        User user = baloot.getBalootUsers().get(username);
        HashMap<String, String> userData = new HashMap<>();
        userData.put("Username", user.getUsername());
        userData.put("Email", user.getEmail());
        userData.put("birthdate", user.getBirthDate().toString());
        userData.put("Address", user.getAddress());
        userData.put("Credit", Double.toString(user.getCredit()));

        userPageHtmlStr = htmlHandler.fillTemplatePage(userPageHtmlStr, userData);
        userPageHtmlStr += getHtmlContents("userPages/UserBuyListStart.html");

        String buyListItem = getHtmlContents("userPages/UserBuyListItem.html");
        for(Integer buyListItemId : baloot.getBalootUsers().get(username).getBuyList()) {
            userData = new HashMap<>();
            Commodity commodity = baloot.getBalootCommodities().get(buyListItemId);
            userData.put("Id", Integer.toString(commodity.getId()));
            userData.put("Name", commodity.getName());
            userData.put("ProviderId", Integer.toString(commodity.getProviderId()));
            userData.put("Price", Double.toString(commodity.getPrice()));
            userData.put("Categories", commodity.getCategories().toString());
            userData.put("Rating", Double.toString(commodity.getRating()));
            userData.put("InStock", Integer.toString(commodity.getInStock()));
            userPageHtmlStr += htmlHandler.fillTemplatePage(buyListItem, userData);
        }

        userPageHtmlStr += getHtmlContents("userPages/UserBuyListEnd.html");
        userPageHtmlStr += getHtmlContents("userPages/UserPurchasedListStart.html");

        String purchasedListItem = getHtmlContents("userPages/UserPurchasedListItem.html");
        for(Integer purchasedListItemId : baloot.getBalootUsers().get(username).getPurchasedList()) {
            userData = new HashMap<>();
            Commodity commodity = baloot.getBalootCommodities().get(purchasedListItemId);
            userData.put("Id", Integer.toString(commodity.getId()));
            userData.put("Name", commodity.getName());
            userData.put("ProviderId", Integer.toString(commodity.getProviderId()));
            userData.put("Price", Double.toString(commodity.getPrice()));
            userData.put("Categories", commodity.getCategories().toString());
            userData.put("Rating", Double.toString(commodity.getRating()));
            userData.put("InStock", Integer.toString(commodity.getInStock()));
            userPageHtmlStr += htmlHandler.fillTemplatePage(purchasedListItem, userData);
        }
        userPageHtmlStr += getHtmlContents("userPages/userPurchasedListEnd.html");
        return userPageHtmlStr;
    }
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
