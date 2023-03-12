package com.mehrani.InterfaceServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mehrani.Baloot.Baloot;
import com.mehrani.Baloot.User;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import io.javalin.Javalin;
import com.mehrani.Baloot.LocalDateAdapter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InterfaceServer {
    private Javalin app;
    private HTTPReqHandler httpReqHandler = new HTTPReqHandler();
    private Baloot baloot;

    public void retrieveUsersDataFromAPI(String url) throws Exception {
        String userDataJsonStr = httpReqHandler.httpGetRequest(url);
        //System.out.println(userDataJsonStr);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = gson.fromJson(userDataJsonStr, userListType);
        for (User user : userList) {
            System.out.println("username : " + user.getUsername());
            System.out.println("password : " + user.getPassword());
            System.out.println("birthDate : " + user.getBirthDate().toString());
            System.out.println("email : " + user.getEmail());
            System.out.println("address : " + user.getAddress());
            System.out.println("credit : " + user.getCredit());
            System.out.println("------------------------");
        }
    }
}
