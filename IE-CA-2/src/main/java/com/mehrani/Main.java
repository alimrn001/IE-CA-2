package com.mehrani;

import com.mehrani.HTTPReqHandler.HTTPReqHandler;

public class Main {
    public static void main(String[] args) {
        HTTPReqHandler reqHandler = new HTTPReqHandler();
        String res = "";
        try {
            res = reqHandler.httpGetRequest("http://5.253.25.110:5000/api/users");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println(res);
    }
}