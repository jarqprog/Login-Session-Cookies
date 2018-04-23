package com.jarq.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class MyServer implements IServer {

    private final int port;
    private final HttpHandler staticHandler;
    private final HttpHandler mainHandler;

    public static IServer create(int port, HttpHandler staticHandler, HttpHandler mainHandler) {
        return new MyServer(port, staticHandler, mainHandler);
    }

    private MyServer(int port, HttpHandler staticHandler, HttpHandler mainHandler) {
        this.port = port;
        this.staticHandler = staticHandler;
        this.mainHandler = mainHandler;
    }


    @Override
    public void run() throws IOException {

        System.out.println("Starting server: http://localhost:" + port);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // set routes
        server.createContext("/static", staticHandler);
        server.createContext("/", mainHandler);

        server.setExecutor(null);

        // start listening
        server.start();
    }
}
