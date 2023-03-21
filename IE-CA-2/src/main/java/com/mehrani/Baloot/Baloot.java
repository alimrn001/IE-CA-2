package com.mehrani.Baloot;

import com.mehrani.Baloot.Data.*;
import com.mehrani.Baloot.Exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Baloot {

    private final Map<String, User> balootUsers = new HashMap<>();

    private final Map<Integer, Commodity> balootCommodities = new HashMap<>();

    private final Map<Integer, Provider> balootProviders = new HashMap<>();

    private final Map<String, Rating> balootRatings = new HashMap<>();

    private final Map<String, Category> balootCategorySections = new HashMap<>();

    private final Map<Integer, Comment> balootComments = new HashMap<>();

    private int latestCommentID = 0; //comments id start with 1


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

    public boolean commentExists(int commentId) {
        return balootComments.containsKey(commentId);
    }

    public boolean userEmailExists(String userEmail) {
        boolean emailExists = false;
        for(Map.Entry<String, User> userEntry : balootUsers.entrySet()) {
            if (userEntry.getValue().getEmail().equals(userEmail)) {
                emailExists = true;
                break;
            }
        }
        return emailExists; // is email unique for users ?? if not how to identify user account in comment by just email ? how to find its id??
    }

    public void updateCategorySection(String categoryName, int commodityId) {
        if(categoryExists(categoryName)) {
            balootCategorySections.get(categoryName).addCommodityToCategory(commodityId);
        }
        else {
            Category category = new Category(categoryName);
            category.addCommodityToCategory(commodityId);
            balootCategorySections.put(categoryName, category);
        }
    }

    public void addUser(User user) throws Exception {
        if(balootUsers.containsKey(user.getUsername())) {
            user.setBuyList(balootUsers.get(user.getUsername()).getBuyList());
            balootUsers.put(user.getUsername(), user);
        }
        else {
            if((user.getUsername().contains("!")) || (user.getUsername().contains("#")) || (user.getUsername().contains("@"))) {
                throw new UsernameWrongCharacterException();
            }
            else {
                balootUsers.put(user.getUsername(), user);
            }
        }
    }

    public void addCommodity(Commodity commodity) throws Exception {
        if(!providerExists(commodity.getProviderId()))
            throw new ProviderNotExistsException();

        if(commodityExists(commodity.getId()))
            throw new CommodityWithSameIDException();

        balootProviders.get(commodity.getProviderId()).addProvidedCommodity(commodity.getId());
        for (String category : commodity.getCategories()) {
            updateCategorySection(category, commodity.getId());
        }
        balootCommodities.put(commodity.getId(), commodity);
        balootProviders.get(commodity.getProviderId()).updateCommoditiesData(commodity.getRating());
    }

    public void addProvider(Provider provider) throws Exception {
        if(providerExists(provider.getId())) {
            balootProviders.get(provider.getId()).setName(provider.getName());
            balootProviders.get(provider.getId()).setRegistryDate(provider.getRegistryDate().toString());
        }
        else
            balootProviders.put(provider.getId(), provider);
    }

    public void addComment(Comment comment) throws Exception {
        if(!userEmailExists(comment.getUserEmail())) {
            throw new UserNotExistsException();
        }
        if(!commodityExists(comment.getCommodityId())) {
            throw new CommodityNotExistsException();
        }
        comment.setCommentId(latestCommentID+1);
        comment.setLikesNo(0);
        comment.setDislikesNo(0);
        comment.setNeutralVotesNo(0);
        balootComments.put(comment.getCommentId(), comment);
        latestCommentID++;
        balootCommodities.get(comment.getCommodityId()).addComment(comment.getCommentId());
    }

    public void addRating(String username, int commodityId, int rate) throws Exception {
        if(!userExists(username))
            throw new UserNotExistsException();
        if(!commodityExists(commodityId))
            throw new CommodityNotExistsException();
        if(rate > 10 || rate < 1)
            throw new RatingOutOfRangeException();

        String ratingPrimaryKey = username + "_" + commodityId;
        Rating rating = new Rating(username, commodityId, rate);

        if(!balootRatings.containsKey(ratingPrimaryKey)) {
            balootCommodities.get(commodityId).addNewRating(rate);
            balootRatings.put(ratingPrimaryKey, rating);
            return;
        }
        int previousRate = balootRatings.get(ratingPrimaryKey).getScore();
        balootCommodities.get(commodityId).updateUserRating(previousRate, rate);
        balootRatings.put(ratingPrimaryKey, rating);
    }

    public void addRemoveBuyList(String username, int commodityId, boolean isAdding) throws Exception {
        User user = getBalootUser(username);
        Commodity commodity = getBalootCommodity(commodityId);
        if(commodity.getInStock()==0 && isAdding)
            throw new ItemNotAvailableInStockException();
        if(user.itemExistsInBuyList(commodityId)) {
            if(isAdding)
                throw new ItemAlreadyExistsInBuyListException();
            user.removeFromBuyList(commodityId);
            return;
        }
        else {
            if(isAdding) {
                user.addToBuyList(commodityId);
                return;
            }
            throw new ItemNotInBuyListForRemovingException();
        }
    }

    public void purchaseUserBuyList(String username) throws Exception {
        if(!userExists(username))
            throw new UserNotExistsException();

        ArrayList<Integer> userBuyList = balootUsers.get(username).getBuyList();
        double totalPurchasePrice = 0;
        for(Integer buyListItemId : userBuyList)
            totalPurchasePrice += balootCommodities.get(buyListItemId).getPrice();
        if(balootUsers.get(username).getCredit() < totalPurchasePrice)
            throw new NotEnoughCreditException();

        balootUsers.get(username).purchaseBuyList(totalPurchasePrice);
        for(Integer buyListItemId : userBuyList)
            balootCommodities.get(buyListItemId).reduceInStock(1);
    }

    public void addCreditToUser(String username, double credit) throws Exception {
        User user = getBalootUser(username);
        if(credit <= 0)
            throw new NegativeCreditAddingException();
        user.addCredit(credit);
    }

    public void voteComment(String username, int commentId, int vote) throws Exception {
        if(!userExists(username))
            throw new UserNotExistsException();
        if(!commentExists(commentId))
            throw new CommentNotExistsException();

        boolean beenLikedBefore = balootUsers.get(username).userHasLikedComment(commentId);
        boolean beenDislikedBefore = balootUsers.get(username).userHasDislikedComment(commentId);

        if(vote==1) {
            balootUsers.get(username).addCommentToLikedList(commentId);
            if(!beenLikedBefore)
                balootComments.get(commentId).addLike();
            if(beenDislikedBefore)
                balootComments.get(commentId).removeDislike();
        }

        else if(vote==0) { // ????
            balootComments.get(commentId).addNeutralVote();
        }

        else if(vote==-1) {
            balootUsers.get(username).addCommentToDislikedList(commentId);
            if(!beenDislikedBefore)
                balootComments.get(commentId).addDislike();
            if(beenLikedBefore)
                balootComments.get(commentId).removeLike();
        }

        else
            throw new WrongVoteValueException();
    }

    public Map<Integer, Comment> getCommodityComments(int commodityId) throws Exception {
        if(!commodityExists(commodityId))
            throw new CommodityNotExistsException();
        Map<Integer, Comment> result = new HashMap<>();
        for (Map.Entry<Integer, Comment> commentEntry : balootComments.entrySet()) {
            if(commentEntry.getValue().getCommodityId()==commodityId)
                result.put(commentEntry.getKey(), commentEntry.getValue());
        }
        return result;
    }

    public Map<Integer, Commodity> getCommoditiesByCategory(String category) {
        Map<Integer, Commodity> commodities = new HashMap<>();
        if(!categoryExists(category))
            return commodities;

        for(int categoryCommodityID : balootCategorySections.get(category).getCommodities()) {
            Commodity categoryCommodity = balootCommodities.get(categoryCommodityID);
            commodities.put(categoryCommodityID, categoryCommodity);
        }
        return commodities;
    }

    public Map<Integer, Commodity> getCommoditiesByPriceRange(int startPrice, int endPrice) {
        Map<Integer, Commodity> commodities = new HashMap<>();
        for(Map.Entry<Integer, Commodity> commodityEntry : balootCommodities.entrySet()) {
            if(commodityEntry.getValue().getPrice() <= endPrice && commodityEntry.getValue().getPrice() >= startPrice) {
                commodities.put(commodityEntry.getKey(), commodityEntry.getValue());
            }
        }
        return commodities;
    }

    public User getBalootUser(String username) throws Exception {
        if(!userExists(username))
            throw new UserNotExistsException();
        return balootUsers.get(username);
    }

    public Commodity getBalootCommodity(int commodityId) throws Exception {
        if(!commodityExists(commodityId))
            throw new CommodityNotExistsException();
        return balootCommodities.get(commodityId);
    }

    public Provider getBalootProvider(int providerId) throws Exception {
        if(!providerExists(providerId))
            throw new ProviderNotExistsException();
        return balootProviders.get(providerId);
    }

    public Comment getBalootComment(int commentId) throws Exception {
        if(!commentExists(commentId))
            throw new CommentNotExistsException();
        return balootComments.get(commentId);
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

    public Map<Integer, Comment> getBalootComments() {
        return balootComments;
    }

    public Map<String, Rating> getBalootRatings() {
        return balootRatings;
    }

    public Map<String, Category> getBalootCategorySections() {
        return balootCategorySections;
    }

}
