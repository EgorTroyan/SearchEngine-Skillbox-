package com.egortroyan.searchengine.service.responses;

public class FalseResponseService extends ResponseService{
    boolean result = false;
    String error;

    public FalseResponseService(String error) {
        this.error = error;
    }

    @Override
    public boolean isResult() {
        return result;
    }

    public String getError() {
        return error;
    }
}
