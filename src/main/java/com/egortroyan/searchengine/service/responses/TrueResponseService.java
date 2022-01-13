package com.egortroyan.searchengine.service.responses;

public class TrueResponseService extends ResponseService {
    boolean result = true;

    @Override
    public boolean isResult() {
        return result;
    }
}
