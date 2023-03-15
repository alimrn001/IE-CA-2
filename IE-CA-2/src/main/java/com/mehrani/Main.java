package com.mehrani;

import com.mehrani.Baloot.Baloot;
import com.mehrani.HTTPReqHandler.HTTPReqHandler;
import com.mehrani.InterfaceServer.InterfaceServer;

public class Main {
    public static void main(String[] args) {
        InterfaceServer interfaceServer = new InterfaceServer();
        interfaceServer.start("http://5.253.25.110:5000/api/users", "http://5.253.25.110:5000/api/providers",
                "http://5.253.25.110:5000/api/commodities", "http://5.253.25.110:5000/api/comments", 8080);

    }
}