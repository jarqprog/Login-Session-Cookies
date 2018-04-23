package com.jarq.server.sessions;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.*;

public class SessionManager implements ISessionManager {

    private Map<String,Calendar> sessions;
    private static ISessionManager instance;
    private final long sessionExpirationTime;  // in milliseconds (recommended: 300000)

    // singleton:
    public static ISessionManager create(long sessionExpirationTime) {
        if(instance == null) {
            instance = new SessionManager(sessionExpirationTime);
        }
        return instance;
    }

    private SessionManager(long sessionExpirationTime) {
        sessions = new HashMap<>();
        this.sessionExpirationTime = sessionExpirationTime;
    }

    @Override
    public String getCurrentUserName(HttpExchange httpExchange) throws SessionException {
        deleteExpired();  // remove old sessions
        String invalidSessionInfo = "User hasn't valid session";

        return "Jarek";

//        try {
//            String sessionToken = extractToken(httpExchange);
//
//            if( (! sessionToken.contains("deleted") ) && isLogged(httpExchange)) {
//                sessions.put(sessionToken, Calendar.getInstance());  // time has been updated (session is current)
//
//                String splitRegex = "==";
//                int idIndex = 1;
//
////                return Integer.parseInt(sessionToken.split(splitRegex)[idIndex]);
//                return "Jarek";
//            }
//            else {
//                throw new SessionException(invalidSessionInfo);
//            }
//        } catch (Exception notUsed) {
//            throw new SessionException(invalidSessionInfo);
//        }
    }

    @Override
    public boolean remove(HttpExchange he) {
        try {
            String sessionToken = extractToken(he);
            sessions.remove(sessionToken);
            he.getResponseHeaders().set("Set-Cookie", "sessionToken=deleted");
            return true;
        } catch (SessionException notUsed) {
            return false;
        }
    }

    @Override
    public boolean register(HttpExchange he, String login) {
        if( isLogged(he) ) {  // user is already registered and has active session
            return false;
        }

        String prefix = String.valueOf(getRandomNumber());
        String sessionId = prefix + "==" + login;
        he.getResponseHeaders().set("Set-Cookie", "sessionToken="+sessionId);
        sessions.put(sessionId, Calendar.getInstance());
        return true;
    }

    private int deleteExpired() {
        Map<String,Calendar> copy = new HashMap<>();  // copy to avoid ConcurrentModificationException
        int sessionsDeleted = 0;
        long timeNow = Calendar.getInstance().getTimeInMillis();
        for(Map.Entry<String,Calendar> session : sessions.entrySet()) {
            long sessionTime = session.getValue().getTimeInMillis();
            if((timeNow - sessionTime) < sessionExpirationTime) {
                copy.put(session.getKey(), session.getValue());
            } else {
                sessionsDeleted++;
            }
        }
        sessions = copy;
        return sessionsDeleted;
    }

    private int getRandomNumber() {
        return new Random().nextInt(10000);
    }

    private boolean isLogged(HttpExchange he) {
        try {
            String sessionToken = extractToken(he);
            return sessions.containsKey(sessionToken);

        } catch (SessionException notUsed) {
            return false;
        }
    }

    private String extractToken(HttpExchange he) throws SessionException {
        String cookieMatcher = "sessionToken=";

        try {
            Headers headers = he.getRequestHeaders();
            List<String> cookies = headers.get("Cookie");
            String cookie = cookies.stream()
                    .filter(c -> c.contains(cookieMatcher))
                    .findFirst().orElse("");

            return cookie.replace(cookieMatcher, "");
        } catch (Exception notUsed) {
            throw new SessionException();
        }
    }
}