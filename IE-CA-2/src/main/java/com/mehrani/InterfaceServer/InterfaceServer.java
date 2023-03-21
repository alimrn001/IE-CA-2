package com.mehrani.InterfaceServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Exceptions.*;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import io.javalin.Javalin;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

public class InterfaceServer {

    private Javalin app;

    private final HTTPReqHandler httpReqHandler = new HTTPReqHandler();

    private final HtmlHandler htmlHandler = new HtmlHandler();

    private final Baloot baloot = new Baloot();


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
            catch (Exception e) {
                System.out.println(e.getMessage());
                ctx.html(getHtmlContents("404.html"));
                //ctx.status(404);
            }
        });

        app.post("users/{user_id}", ctx -> {
            try {
                String commodityIdToBeRemoved = ctx.formParam("commodityId");
                String username = ctx.pathParam("user_id");
                String usernamePurchase = ctx.formParam("userIdPurchase");

                if(commodityIdToBeRemoved != null) {
                    baloot.addRemoveBuyList(username, Integer.parseInt(commodityIdToBeRemoved), false);
                    ctx.redirect(""); //comment this line and uncomment two other parts to show response
                    //ctx.html(getHtmlContents("200.html"));
                    //ctx.status(200);
                }
                if(usernamePurchase != null) {
                    baloot.purchaseUserBuyList(usernamePurchase);
                    ctx.html(getHtmlContents("200.html"));
                    ctx.status(200);
                }
            }
            catch (ItemNotInBuyListForRemovingException e) {
                ctx.html(getHtmlContents("403.html"));
                ctx.status(403);
            }
            catch (NotEnoughCreditException e) {
                ctx.html(getHtmlContents("403.html"));
                ctx.status(403);
                //might design a new "function failed" page for it
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("addToBuyList/{username}/{commodityId}", ctx -> {
            try {
                String username = ctx.pathParam("username");
                String commodityId = ctx.pathParam("commodityId");
                baloot.addRemoveBuyList(username, Integer.parseInt(commodityId), true);
                ctx.html(getHtmlContents("200.html"));
                ctx.status(200);
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                ctx.status(404); //needed ??
                // can add additional features here
            }
            catch (CommodityNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                ctx.status(404); //needed ??
                // add additional features here
            }
            catch (ItemAlreadyExistsInBuyListException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
                //ctx.status(403); //?????
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404); //needed ??
                // can use this to avoid having id as non-int value
            }
        });

        app.get("removeFromBuyList/{username}/{commodityId}", ctx -> {
            try {
                String username = ctx.pathParam("username");
                String commodityId = ctx.pathParam("commodityId");
                baloot.addRemoveBuyList(username, Integer.parseInt(commodityId), false);
                ctx.html(getHtmlContents("200.html"));
                ctx.status(200);
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                ctx.status(404); //needed ??
                // can add additional features here
            }
            catch (CommodityNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                ctx.status(404); //needed ??
                // add additional features here
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404); //needed ??
                // can use this to avoid having id as non-int value
            }
        });

        app.get("addCredit/{user_id}/{credit}", ctx -> {
            try {
                String username = ctx.pathParam("user_id");
                double credit = Double.parseDouble(ctx.pathParam("credit"));
                baloot.addCreditToUser(username, credit);
                ctx.html(getHtmlContents("200.html"));
                ctx.status(200);
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                ctx.status(404);
            }
            catch (NegativeCreditAddingException e) {
                ctx.html(getHtmlContents("403.html"));
                System.out.println(e.getMessage());
                ctx.status(403);
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("403.html"));
                System.out.println(e.getMessage());
                ctx.status(403);
                //cannot add non-double value !!
            }
        });

        app.get("providers/{provider_id}", ctx -> {
            try {
                ctx.html(createProviderHtmlPage(Integer.parseInt(ctx.pathParam("provider_id"))));
            }
            catch (ProviderNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                System.out.println(e.getMessage());
                //ctx.status(404);
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("403.html"));
                ctx.status(403);
            }
        });

        app.get("commodities/", ctx -> {
            try {
                ctx.html(createCommoditiesListHtmlPage(baloot.getBalootCommodities()));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("commodities/{commodity_id}", ctx -> {
            try {
                ctx.html(createCommodityItemHtmlPage(Integer.parseInt(ctx.pathParam("commodity_id"))));
            }
            catch (CommodityNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
                //for wrong input
            }
        });

        app.post("commodities/{commodity_id}", ctx -> {
            try {
                String userId = ctx.formParam("user_id");
                String rateValue = ctx.formParam("rateValue");
                String commodityId = ctx.pathParam("commodity_id");
                String buyListAdd = ctx.formParam("buyListAdd");
                String rateCommodity = ctx.formParam("rateCommodity");
                String likeCommentRequest = ctx.formParam("LikeComment");
                String dislikeCommentRequest = ctx.formParam("DislikeComment");

                boolean validUsernameIsEntered = (userId != null && !userId.equals(""));

                if(buyListAdd != null && userId != null) {
                    baloot.addRemoveBuyList(userId, Integer.parseInt(commodityId), true);
                    ctx.html(getHtmlContents("200.html"));
                }

                if(rateValue != null && rateCommodity != null && userId != null) {
                    if(rateValue.equals(""))
                        ctx.html(getHtmlContents("404.html"));
                    else {
                        baloot.addRating(userId, Integer.parseInt(commodityId), Integer.parseInt(rateValue));
                        ctx.redirect("");
                    }
                }

                if(validUsernameIsEntered) {
                    if (likeCommentRequest != null) {
                        String commentId = ctx.formParam("comment_id");
                        if(commentId != null) {
                            baloot.voteComment(userId, Integer.parseInt(commentId), 1);
                            ctx.redirect("");
                        }
                    }
                    if (dislikeCommentRequest != null) {
                        String commentId = ctx.formParam("comment_id");
                        if(commentId != null) {
                            baloot.voteComment(userId, Integer.parseInt(commentId), -1);
                            ctx.redirect("");
                        }
                    }
                }

                else {
                    ctx.html(getHtmlContents("403.html"));
                }
            }
            catch (ItemAlreadyExistsInBuyListException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
            }
            catch (ItemNotAvailableInStockException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
            }
            catch (RatingOutOfRangeException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("403.html"));
                // for when input box is empty for rating and other errors for string to int conversion
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("commodities/search/{categories}", ctx -> {
            try {
                String category = ctx.pathParam("categories");
                ctx.html(createCommoditiesListHtmlPage(baloot.getCommoditiesByCategory(category)));
                ctx.status(202);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("commodities/search/{start_price}/{end_price}", ctx -> {
            try {
                int startPrice = Integer.parseInt(ctx.pathParam("start_price"));
                int endPrice = Integer.parseInt(ctx.pathParam("end_price"));
                ctx.html(createCommoditiesListHtmlPage(baloot.getCommoditiesByPriceRange(startPrice, endPrice)));
                ctx.status(200);
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("rateCommodity/{username}/{commodityId}/{rate}", ctx -> {
            try {
                String username = ctx.pathParam("username");
                int commodityId = Integer.parseInt(ctx.pathParam("commodityId"));
                int rating = Integer.parseInt(ctx.pathParam("rate"));
                baloot.addRating(username, commodityId, rating);
                ctx.html(getHtmlContents("200.html")); //reloading this page won't change rating
                ctx.status(202);
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
               // to handle username problems
            }
            catch (CommodityNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
               // to handle commodityID problems
            }
            catch (NumberFormatException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
            }
            catch (RatingOutOfRangeException e) {
                ctx.html(getHtmlContents("FunctionFailed.html"));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        app.get("voteComment/{username}/{commentId}/{vote}", ctx -> {
            try {
                String username = ctx.pathParam("username");
                int commentId = Integer.parseInt(ctx.pathParam("commentId"));
                int vote = Integer.parseInt(ctx.pathParam("vote"));
                baloot.voteComment(username, commentId, vote);
                ctx.html(getHtmlContents("200.html"));
                ctx.status(200);
            }
            catch (UserNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
                System.out.println(e.getMessage());
                // for wrong username
            }
            catch (CommentNotExistsException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
                System.out.println(e.getMessage());
                //for wrong comment id
            }
            catch (WrongVoteValueException e) {
                ctx.html(getHtmlContents("404.html"));
                ctx.status(404);
                System.out.println(e.getMessage());
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
        String userPageHtmlStr = getHtmlContents("userPages/UserInfo.html");
        User user = baloot.getBalootUser(username);

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
            //Commodity commodity = baloot.getBalootCommodities().get(buyListItemId);
            Commodity commodity = baloot.getBalootCommodity(buyListItemId);
            userData.put("Username", user.getUsername()); // for removing from buy list we need username to generate url
            userData.put("Id", Integer.toString(commodity.getId()));
            userData.put("Name", commodity.getName());
            userData.put("ProviderId", Integer.toString(commodity.getProviderId()));
            userData.put("Price", Integer.toString(commodity.getPrice()));
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
            //Commodity commodity = baloot.getBalootCommodities().get(purchasedListItemId);
            Commodity commodity = baloot.getBalootCommodity(purchasedListItemId);
            userData.put("Id", Integer.toString(commodity.getId()));
            userData.put("Name", commodity.getName());
            userData.put("ProviderId", Integer.toString(commodity.getProviderId()));
            userData.put("Price", Integer.toString(commodity.getPrice()));
            userData.put("Categories", commodity.getCategories().toString());
            userData.put("Rating", Double.toString(commodity.getRating()));
            userData.put("InStock", Integer.toString(commodity.getInStock()));
            userPageHtmlStr += htmlHandler.fillTemplatePage(purchasedListItem, userData);
        }
        userPageHtmlStr += getHtmlContents("userPages/userPurchasedListEnd.html");
        return userPageHtmlStr;
    }

    private String createProviderHtmlPage(int providerId) throws Exception {
        String providerHtmlPageStr = getHtmlContents("providerPages/ProviderInfo.html");
        Provider provider = baloot.getBalootProvider(providerId);

        HashMap<String, String> providerData = new HashMap<>();
        providerData.put("Id", String.valueOf(provider.getId()));
        providerData.put("Name", provider.getName());
        providerData.put("RegistryDate", provider.getRegistryDate().toString());
        providerHtmlPageStr = htmlHandler.fillTemplatePage(providerHtmlPageStr, providerData);
        String buyListItem = getHtmlContents("providerPages/ProviderCommodityItem.html");

        for(Integer providerCommodityId : provider.getCommoditiesProvided()) {
            providerData = new HashMap<>();
            Commodity commodity = baloot.getBalootCommodity(providerCommodityId);
            providerData.put("Id", Integer.toString(commodity.getId()));
            providerData.put("Name", commodity.getName());
            providerData.put("Price", Integer.toString(commodity.getPrice()));
            providerData.put("Categories", commodity.getCategories().toString());
            providerData.put("Rating", Double.toString(commodity.getRating()));
            providerData.put("InStock", Integer.toString(commodity.getInStock()));
            providerHtmlPageStr += htmlHandler.fillTemplatePage(buyListItem, providerData);
        }
        providerHtmlPageStr += getHtmlContents("providerPages/ProviderEnd.html");
        return providerHtmlPageStr;
    }

    private String createCommoditiesListHtmlPage(Map<Integer, Commodity> commoditiesList) throws Exception {
        String commoditiesListHtmlPageStr = getHtmlContents("commodityPages/CommoditiesListStart.html");
        String commodityItem = getHtmlContents("commodityPages/CommoditiesListItem.html");

        for(Map.Entry<Integer, Commodity> commoditiesSetEntry : commoditiesList.entrySet()) {
            HashMap<String, String> commodityData = new HashMap<>();
            commodityData.put("Id", String.valueOf(commoditiesSetEntry.getValue().getId()));
            commodityData.put("Name", commoditiesSetEntry.getValue().getName());
            commodityData.put("ProviderId", Integer.toString(commoditiesSetEntry.getValue().getProviderId()));
            commodityData.put("Price", Integer.toString(commoditiesSetEntry.getValue().getPrice()));
            commodityData.put("Categories", commoditiesSetEntry.getValue().getCategories().toString());
            commodityData.put("Rating", Double.toString(commoditiesSetEntry.getValue().getRating()));
            commodityData.put("InStock", Integer.toString(commoditiesSetEntry.getValue().getInStock()));
            commoditiesListHtmlPageStr += htmlHandler.fillTemplatePage(commodityItem, commodityData);
        }

        commoditiesListHtmlPageStr += getHtmlContents("commodityPages/CommoditiesListEnd.html");
        return commoditiesListHtmlPageStr;
    }

    private String createCommodityItemHtmlPage(int commodityId) throws Exception {
        String commodityItemHtmlPageStr = getHtmlContents("commodityPages/CommodityInfo.html");
        String commentItem = getHtmlContents("commodityPages/CommodityComment.html");
        Commodity commodity = baloot.getBalootCommodity(commodityId);
        HashMap<String, String> commodityData = new HashMap<>();
        commodityData.put("Id", String.valueOf(commodity.getId()));
        commodityData.put("Name", commodity.getName());
        commodityData.put("ProviderId", String.valueOf(commodity.getProviderId()));
        commodityData.put("Price", Integer.toString(commodity.getPrice()));
        commodityData.put("Categories", commodity.getCategories().toString());
        commodityData.put("Rating", new DecimalFormat("0.00").format(commodity.getRating()));
        commodityData.put("InStock", Integer.toString(commodity.getInStock()));
        commodityItemHtmlPageStr = htmlHandler.fillTemplatePage(commodityItemHtmlPageStr, commodityData);

        for(int commentID : commodity.getComments()) {
            Comment comment = baloot.getBalootComment(commentID);
            HashMap<String, String> commentData = new HashMap<>();
            commentData.put("CommentID", String.valueOf(comment.getCommentId()));
            commentData.put("username", comment.getUserEmail());
            commentData.put("text", comment.getText());
            commentData.put("commentDate", comment.getDate().toString());
            commentData.put("LikesNo", String.valueOf(comment.getLikesNo()));
            commentData.put("DislikesNo", String.valueOf(comment.getDislikesNo()));
            commodityItemHtmlPageStr += htmlHandler.fillTemplatePage(commentItem, commentData);
        }

        commodityItemHtmlPageStr += getHtmlContents("commodityPages/CommodityEnd.html");
        return commodityItemHtmlPageStr;
    }

    public void retrieveUsersDataFromAPI(String url) throws Exception {
        String userDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = gson.fromJson(userDataJsonStr, userListType);
        for (User user : userList) {
            user.initializeGsonNullValues(); // might be removed in upcoming phases
            baloot.addUser(user);
        }
    }

    public void retrieveProvidersDataFromAPI(String url) throws Exception {
        String providerDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type providerListType = new TypeToken<ArrayList<Provider>>(){}.getType();
        List<Provider> providerList = gson.fromJson(providerDataJsonStr, providerListType);
        for(Provider provider : providerList) {
            provider.initializeGsonNullValues();
            baloot.addProvider(provider);
        }
    }

    public void retrieveCommoditiesDataFromAPI(String url) throws Exception {
        String commodityDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().create();
        Type commodityListType = new TypeToken<ArrayList<Commodity>>(){}.getType();
        List<Commodity> commodityList = gson.fromJson(commodityDataJsonStr, commodityListType);
        for(Commodity commodity : commodityList) {
            commodity.initializeGsonNullValues();
            baloot.addCommodity(commodity);
        }
    }

    public void retrieveCommentsDataFromAPI(String url) throws Exception {
        String commentDataJsonStr = httpReqHandler.httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type commentListType = new TypeToken<ArrayList<Comment>>(){}.getType();
        List<Comment> commentList = gson.fromJson(commentDataJsonStr, commentListType);
        for(Comment comment : commentList)
            baloot.addComment(comment);

    }

}
