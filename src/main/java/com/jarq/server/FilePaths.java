package com.jarq.server;

public enum FilePaths {

    INFO404("static/img/404.jpg");

    private String path;

    FilePaths(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
