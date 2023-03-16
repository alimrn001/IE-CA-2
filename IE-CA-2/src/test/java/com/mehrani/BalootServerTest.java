package com.mehrani;

import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Error;
import com.mehrani.Baloot.Exceptions.ItemAlreadyExistsInBuyListException;
import com.mehrani.Baloot.Exceptions.ItemNotInBuyListForRemovingException;
import com.mehrani.Baloot.Exceptions.NegativeCreditAddingException;
import com.mehrani.Baloot.Exceptions.RatingOutOfRangeException;
import com.mehrani.InterfaceServer.InterfaceServer;
import org.checkerframework.checker.units.qual.A;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void getCommoditiesByPriceRangeTest() {
        try {
            Assert.assertEquals(5, baloot.getCommoditiesByPriceRange(10000, 20000).size());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addSampleData() throws Exception {
        User user = new User();
        user.setUserData("saied", "1234", "2001-05-25", "saied@gmail", "s-home", 1000.0);
        baloot.addUser(user);
    }
}