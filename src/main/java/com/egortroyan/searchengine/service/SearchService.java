package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.service.responses.ResponseService;
import com.egortroyan.searchengine.service.responses.SearchResponseService;

import java.io.IOException;

public interface SearchService {
    ResponseService getResponse (Request request, String url, int offset, int limit) throws IOException;
}
