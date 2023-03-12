package com.mehrani;

import com.mehrani.Baloot.*;
import com.mehrani.Baloot.Error;
import org.junit.*;

import java.util.Map;

import static org.junit.Assert.*;

public class BalootTest {
    private static Baloot baloot = new Baloot();

    @BeforeClass
    public static void setup() {
        String result1=baloot.checkUserCmd("addUser {\"username\": \"user1\", \"password\": \"1234\", \"email\": \"user@gmail.com\", \"birthDate\": \"1977-09-15\", \"address\": \"address1\", \"credit\": 1500}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result1);
        String result2=baloot.checkUserCmd("addProvider {\"id\": 3, \"name\": \"provider1\", \"registryDate\": \"2023-09-15\"}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result2);
        String result3=baloot.checkUserCmd("addProvider {\"id\": 6, \"name\": \"provider2\", \"registryDate\": \"2013-09-15\"}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result3);
        String result4=baloot.checkUserCmd("addCommodity {\"id\": 1, \"name\": \"Headphone\", \"providerId\": 3, \"price\": 35000, \"categories\": [\"Technology\", \"Phone\"], \"rating\": 8, \"inStock\": 50}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result4);
        String result5=baloot.checkUserCmd("addCommodity {\"id\": 2, \"name\": \"Headphone\", \"providerId\": 3, \"price\": 35000, \"categories\": [\"Technology\", \"Phone2\"], \"rating\": 5, \"inStock\": 50}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result5);
        String result6=baloot.checkUserCmd("addCommodity {\"id\": 3, \"name\": \"Headphone\", \"providerId\": 3, \"price\": 35000, \"categories\": [\"Technology\", \"Phone2\"], \"rating\": 6.5, \"inStock\": 50}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result6);
        String result7=baloot.checkUserCmd("addCommodity {\"id\": 4, \"name\": \"Headphone\", \"providerId\": 3, \"price\": 35000, \"categories\": [\"Technology\", \"Phone\"], \"rating\": 7, \"inStock\": 50}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result7);
        String result8=baloot.checkUserCmd("addUser {\"username\": \"user2\", \"password\": \"12345\", \"email\": \"user2@gmail.com\", \"birthDate\": \"1990-09-15\", \"address\": \"address2\", \"credit\": 100000}");
        assertEquals("{\"success\":true,\"data\":\"\"}", result8);

        assertEquals(6.625, baloot.getBalootProviders().get(3).getAvgCommoditiesRate(), 0.000001);

        System.out.println("Users : ");
        for(Map.Entry<String, User> user : baloot.getBalootUsers().entrySet()) {
            System.out.println("username : " + user.getValue().getUsername());
            System.out.println("password : " + user.getValue().getPassword());
            System.out.println("email : " + user.getValue().getEmail());
            System.out.println("birthDate : " + user.getValue().getBirthDate());
            System.out.println("address : " + user.getValue().getAddress());
            System.out.println("credit : " + user.getValue().getCredit());
            System.out.println("---------------------");
        }
        System.out.println("Providers : ");
        for(Map.Entry<Integer, Provider> provider : baloot.getBalootProviders().entrySet()) {
            System.out.println("provider id : " + provider.getValue().getId());
            System.out.println("provider name : " + provider.getValue().getName());
            System.out.println("provider registry date : " + provider.getValue().getRegistryDate().toString());
            System.out.println("---------------------");
        }
        System.out.println("Commodities : ");
        for(Map.Entry<Integer, Commodity> commodity : baloot.getBalootCommodities().entrySet()) {
            System.out.println("id : " + commodity.getValue().getId());
            System.out.println("name : " + commodity.getValue().getName());
            System.out.println("provider id : " + commodity.getValue().getProviderId());
            System.out.println("price : " + commodity.getValue().getPrice());;
            System.out.println("rating : " + commodity.getValue().getRating());
            System.out.println("categories : " + commodity.getValue().getCategories());
            System.out.println("in stock : " + commodity.getValue().getInStock());
            System.out.println("---------------------");
        }

    }

    @AfterClass
    public static void tearDown() {

    }

    @Test
    public void addRatingTest() {
        Error error = new Error();
        String res1=baloot.checkUserCmd("rateCommodity {\"username\": \"user1\", \"commodityId\": 3, \"score\": 7}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res1);

        String res2=baloot.checkUserCmd("rateCommodity {\"username\": \"user3\", \"commodityId\": 3, \"score\": 7}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getUserNotExists() + "\"}", res2);

        String res3=baloot.checkUserCmd("rateCommodity {\"username\": \"user1\", \"commodityId\": 5, \"score\": 7}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getCommodityNotExists() + "\"}", res3);

        String res4=baloot.checkUserCmd("rateCommodity {\"username\": \"user1\", \"commodityId\": 3, \"score\": 12}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getRatingOutOfRange(12) + "\"}", res4);

        String res5=baloot.checkUserCmd("rateCommodity {\"username\": \"user2\", \"commodityId\": 3, \"score\": 5}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res5);
        assertEquals(6, baloot.getBalootCommodities().get(3).getRating(), 0.01);

    }

    @Test
    public void addRemoveToBuyListTest() {
        Error error = new Error();
        String res1 = baloot.checkUserCmd("addToBuyList {\"username\": \"user1\", \"commodityId\": 4}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res1);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res2 = baloot.checkUserCmd("addToBuyList {\"username\": \"user5\", \"commodityId\": 4}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getUserNotExists() + "\"}", res2);

        String res3 = baloot.checkUserCmd("addToBuyList {\"username\": \"user1\", \"commodityId\": 6}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getCommodityNotExists() + "\"}", res3);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res4 = baloot.checkUserCmd("addToBuyList {\"username\": \"user1\", \"commodityId\": 4}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getProductAlreadyExistsInBuyList() + "\"}", res4);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res6 = baloot.checkUserCmd("addToBuyList {\"username\": \"user1\", \"commodityId\": 2}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res6);
        assertEquals(2, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res5 = baloot.checkUserCmd("removeFromBuyList {\"username\": \"user1\", \"commodityId\": 4}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res5);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res7 = baloot.checkUserCmd("removeFromBuyList {\"username\": \"user1\", \"commodityId\": 4}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getProductNotInBuyList() + "\"}", res7);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res8 = baloot.checkUserCmd("removeFromBuyList {\"username\": \"user6\", \"commodityId\": 4}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getUserNotExists() + "\"}", res8);

        String res9 = baloot.checkUserCmd("removeFromBuyList {\"username\": \"user1\", \"commodityId\": 10}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getCommodityNotExists() + "\"}", res9);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res10 = baloot.checkUserCmd("removeFromBuyList {\"username\": \"user1\", \"commodityId\": 2}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res10);
        assertEquals(0, baloot.getBalootUsers().get("user1").getBuyList().size());

        String res11 = baloot.checkUserCmd("addToBuyList {\"username\": \"user1\", \"commodityId\": 2}");
        assertEquals("{\"success\":true,\"data\":\"\"}", res11);
        assertEquals(1, baloot.getBalootUsers().get("user1").getBuyList().size());

    }

    @Test
    public void findCommodityByIdTest() {
        Error error = new Error();
        String res1 = baloot.checkUserCmd("getCommodityById {\"id\": 2}");
        assertEquals("{\"success\":true,\"data\":{\"id\":2,\"name\":\"Headphone\",\"provider\":\"provider1\",\"price\":35000,\"categories\":[\"Technology\",\"Phone2\"],\"rating\":5.0}}", res1);

        String res2 = baloot.checkUserCmd("getCommodityById {\"id\": 6}");
        assertEquals("{\"success\":false,\"data\":\"" + error.getCommodityNotExists() + "\"}", res2);

    }

    @Test
    public void getCommoditiesByCategoryTest() {
        String res1 = baloot.checkUserCmd("getCommoditiesByCategory {\"category\": \"Technology\"}\n");
        assertEquals(res1, "{\"success\":true,\"data\":{\"commoditiesListByCategory\":[{\"id\":1,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone\"],\"rating\":8.0},{\"id\":2,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone2\"],\"rating\":5.0},{\"id\":3,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone2\"],\"rating\":6.5},{\"id\":4,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone\"],\"rating\":7.0}]}}");
    }

    @Test
    public void getCommodityList() {
        String res1 = baloot.checkUserCmd("getCommoditiesList");
        assertEquals(res1, "{\"success\":true,\"data\":{\"commoditiesList\":[{\"id\":1,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone\"],\"rating\":8.0},{\"id\":2,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone2\"],\"rating\":5.0},{\"id\":3,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone2\"],\"rating\":6.5},{\"id\":4,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"categories\":[\"Technology\",\"Phone\"],\"rating\":7.0}]}}");
    }

    @Test
    public void getBuyListTest() {
        String result = baloot.checkUserCmd("getBuyList {\"username\": \"user1\"}");
        assertEquals(result, "{\"success\":true,\"data\":{\"buyList\":[{\"id\":2,\"name\":\"Headphone\",\"providerId\":3,\"price\":35000,\"rating\":5.0}]}}");
    }
}