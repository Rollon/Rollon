package com.rollonapp.rollon.feeds;
import java.io.Serializable;
import java.net.URL;


public class Feed implements Serializable{

    private static final long serialVersionUID = 8212071328640629750L;
    private URL url;
    private String name;
    
    public Feed(String name, URL url) {
        this.url = url;
        this.name = name;
    }
    
    public URL getUrl() {
        return url;
    }
    public void setUrl(URL url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return "Name: " + name + " URL: " + url.toExternalForm();
    }
}
