package com.rollonapp.rollon;
import java.net.URL;


public class Feed {
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
}
