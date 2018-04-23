package com.jarq.server;

import com.jarq.server.sessions.ISessionManager;
import com.jarq.server.sessions.SessionException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class MainHandler implements HttpHandler {

    private ISessionManager sessionManager;

    public static HttpHandler create(ISessionManager sessionManager) {
        return new MainHandler(sessionManager);
    }

    private MainHandler(ISessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("method: " + method);


        String uri = httpExchange.getRequestURI().toString();
        System.out.println("uri: " + uri);

        if (method.equals("GET")) {
            switch (uri) {
                case "/":
                    renderMain(httpExchange);
                    break;
                case "/login":
                    renderLogin(httpExchange);
                    break;
            }
        }

        if (method.equals("POST")) {
            switch (uri) {
                case "/login":
                    handleLogin(httpExchange);
                    break;
            }
        }
    }

    private void handleLogin(HttpExchange httpExchange) throws IOException {
        Map<String,String> loginData = getInput(httpExchange);
        String userLogin = loginData.get("login");
        String userPassword = loginData.get("password");
        System.out.println(
                "Login&password: " + userLogin + " | " + userPassword);  // password isn't used
        sessionManager.register(httpExchange, userLogin);  // assume login is successful
        redirectToMainPage(httpExchange);
    }

    private void renderLogin(HttpExchange httpExchange) throws IOException {
        sessionManager.remove(httpExchange);  // removes current session (if session exists)
        JtwigTemplate template = JtwigTemplate.classpathTemplate("/static/login.html");

        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();
        // render a template to a string
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }

    private void renderMain(HttpExchange httpExchange) throws IOException {

        JtwigTemplate template = JtwigTemplate.classpathTemplate("/static/main.html");

        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();

        try {
            String userName = sessionManager.getCurrentUserName(httpExchange);

            model.with("user", userName);
            // render a template to a string
            String response = template.render(model);
            sendResponse(httpExchange, response);

        } catch (SessionException e) {
            System.out.println(e.getMessage());
            redirectToLogin(httpExchange);
        }
    }


    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        // send the results to a the client
        byte[] bytes = response.getBytes();
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

    private Map<String,String> getInput(HttpExchange httpExchange) throws IOException {

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String data = br.readLine();
        Map<String,String> map = new HashMap<>();
        System.out.println("parser: " + data);
        String[] pairs = data.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    private void redirectToLogin(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/login");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private void redirectToMainPage(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }
}
