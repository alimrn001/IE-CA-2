package com.mehrani;

import com.mehrani.Baloot.Baloot;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import com.mehrani.InterfaceServer.InterfaceServer;

public class Main {
    public static void main(String[] args) {
        HTTPReqHandler reqHandler = new HTTPReqHandler();
        InterfaceServer interfaceServer = new InterfaceServer();
        try {
            interfaceServer.retrieveUsersDataFromAPI("http://5.253.25.110:5000/api/users");
            interfaceServer.retrieveProvidersDataFromAPI("http://5.253.25.110:5000/api/providers");
            interfaceServer.retrieveCommoditiesDataFromAPI("http://5.253.25.110:5000/api/commodities");
            interfaceServer.retrieveCommentsDataFromAPI("http://5.253.25.110:5000/api/comments");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
//        String res = "";
//        try {
//            res = reqHandler.httpGetRequest("http://5.253.25.110:5000/api/users");
//        }
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println(res);
    }
}