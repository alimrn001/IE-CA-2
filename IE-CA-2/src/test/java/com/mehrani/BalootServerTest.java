package com.mehrani;

import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Data.User;
import com.mehrani.Baloot.Exceptions.ItemAlreadyExistsInBuyListException;
import com.mehrani.Baloot.Exceptions.ItemNotInBuyListForRemovingException;
import com.mehrani.Baloot.Exceptions.NegativeCreditAddingException;
import com.mehrani.Baloot.Exceptions.RatingOutOfRangeException;
import com.mehrani.InterfaceServer.InterfaceServer;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
//import org.apache.http.HttpResponse;
import org.junit.*;

import static org.junit.Assert.*;

public class BalootServerTest {

    private static Baloot baloot;

    private static InterfaceServer interfaceServer;


    @BeforeClass
    public static void setup() {
        String userURL = "http://5.253.25.110:5000/api/users";
        String providersURL = "http://5.253.25.110:5000/api/providers";
        String commoditiesURL = "http://5.253.25.110:5000/api/commodities";
        String commentsURL = "http://5.253.25.110:5000/api/comments";
        int port = 8080;
        interfaceServer = new InterfaceServer();
        interfaceServer.start(userURL, providersURL, commoditiesURL, commentsURL, port);
        baloot = interfaceServer.getBaloot();
        try {
            addSampleData();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        interfaceServer.stop();
    }

    @Test
    public void addRatingTestCorrect() {
        try {
            double previousAvgRating = baloot.getBalootCommodity(20).getRating();
            baloot.addRating("saied", 20, 8);
            Assert.assertEquals((previousAvgRating+8)/2, baloot.getBalootCommodity(20).getRating(), 0.001);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addRatingTestWrong() {
        try {
            Assert.assertThrows(RatingOutOfRangeException.class, ()->baloot.addRating("saied", 20, 11));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addRatingCorrectStatus() {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/rateCommodity/amir/1/6").asString();
//        System.out.println(response.getBody().toString());
        Assert.assertEquals(202, response.getStatus());
    }

    @Test
    public void addRatingWrongStatus() {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/rateCommodity/invalidusername/1/6").asString();
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void addToBuyListTestCorrect() {
        try {
            baloot.getBalootUser("saied").getBuyList().clear();
            baloot.addRemoveBuyList("saied", 20, true);
            Assert.assertEquals(1, baloot.getBalootUser("saied").getBuyList().size());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addToBuyListTestWrong() {
        try {
            baloot.getBalootUser("saied").getBuyList().clear();
            baloot.addRemoveBuyList("saied", 20, true);
            Assert.assertThrows(ItemAlreadyExistsInBuyListException.class,
                    ()->baloot.addRemoveBuyList("saied", 20, true));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void removeFromBuyListTestCorrect() {
        try {
            baloot.getBalootUser("saied").getBuyList().clear();
            baloot.addRemoveBuyList("saied", 20, true);
            baloot.addRemoveBuyList("saied", 20, false);
            assertEquals(0, baloot.getBalootUser("saied").getBuyList().size());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void removeFromBuyListTestWrong() {
        try {
            baloot.getBalootUser("saied").getBuyList().clear();
            Assert.assertThrows(ItemNotInBuyListForRemovingException.class,
                    ()->baloot.addRemoveBuyList("saied", 20, false));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addCreditToUserCorrect() {
        try {
            baloot.addCreditToUser("saied", 10000);
            Assert.assertEquals(baloot.getBalootUser("saied").getCredit(), 11000, 0.001);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addCreditToUserWrong() {
        try {
            assertThrows(NegativeCreditAddingException.class, () -> baloot.addCreditToUser("saied", -10000));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void getCommoditiesByPriceStatus() {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/commodities/search/10000/30000").asString();
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getCommoditiesByPriceStatusWrong() {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/commodities/search/a/80000").asString();
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void getCommoditiesByPriceRangeTest() {
        try {
            Assert.assertEquals(5, baloot.getCommoditiesByPriceRange(10000, 20000).size());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addSampleData() throws Exception {
        User user = new User("saied", "1234", "2001-05-25", "saied@gmail", "s-home", 1000.0);
        baloot.addUser(user);
    }

}