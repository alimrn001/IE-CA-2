package com.mehrani;

import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Error;
import com.mehrani.InterfaceServer.InterfaceServer;
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

    }

    @AfterClass
    public static void tearDown() {
        interfaceServer.stop();
    }

    @Test
    public void addRatingTest() {

    }

    @Test
    public void addRemoveToBuyListTest() {

    }

    @Test
    public void addCreditToUser() {
        try {
           baloot.addCreditToUser("saied", 10000);
            Assert.assertEquals(baloot.getBalootUser("saied").getCredit(), 11000, 0.001);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void getCommoditiesByCategoryTest() {

    }

    @Test
    public void getCommodityList() {

    }

    @Test
    public void getBuyListTest() {
        User user = new User();
        user.setUserData("saied", "1234", "2001-05-25", "saied@gmail", "s-home", 1000.0);
    }

    public void addSampleData() {

    }
}