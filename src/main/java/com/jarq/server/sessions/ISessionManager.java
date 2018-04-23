package com.jarq.server.sessions;

import com.sun.net.httpserver.HttpExchange;

public interface ISessionManager {

    String getCurrentUserName(HttpExchange he) throws SessionException;
    boolean register(HttpExchange he, String login);
    boolean remove(HttpExchange he);  // use it in case of logout

}
