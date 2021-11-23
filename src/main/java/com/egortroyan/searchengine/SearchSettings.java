package com.egortroyan.searchengine;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "config")
public class SearchSettings {
    private String prefix;
    private String agent;
    private String webinterface;
    private List<HashMap<String, String>> site;

    public String getPrefix() {
        return prefix;
    }

    public String getAgent() {
        return agent;
    }

    public String getWebinterface() {
        return webinterface;
    }

    public List<HashMap<String, String>> getSite() {
        return site;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setWebinterface(String webinterface) {
        this.webinterface = webinterface;
    }

    public void setSite(List<HashMap<String, String>> site) {
        this.site = site;
    }
}
