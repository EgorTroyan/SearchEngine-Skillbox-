package com.egortroyan.searchengine.service.responses;

import com.egortroyan.searchengine.models.Status;

public class Detailed {
    String url;
    String name;
    Status status;
    long statusTime;

    public Detailed(String url, String name, Status status, long statusTime) {
        this.url = url;
        this.name = name;
        this.status = status;
        this.statusTime = statusTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(long statusTime) {
        this.statusTime = statusTime;
    }
}
