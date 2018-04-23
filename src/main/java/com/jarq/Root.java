package com.jarq;

import com.jarq.server.IServer;
import com.jarq.server.MainHandler;
import com.jarq.server.MyServer;
import com.jarq.server.StaticHandler;
import com.jarq.server.sessions.ISessionManager;
import com.jarq.server.sessions.SessionManager;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;


public class Root implements Client {


    static Client create() {
        return new Root();
    }

    private Root() {}


    @Override
    public void run() {

        int port = 8010;

        // initialize objects

        long sessionExpirationTime = 300000;
        ISessionManager sessionManager = SessionManager.create(sessionExpirationTime);
        HttpHandler staticHandler = StaticHandler.create();
        HttpHandler mainHandler = MainHandler.create(sessionManager);

        IServer server = MyServer.create(port, staticHandler, mainHandler);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
