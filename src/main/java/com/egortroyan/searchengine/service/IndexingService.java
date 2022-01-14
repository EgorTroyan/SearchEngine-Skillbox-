package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.service.responses.ResponseService;

public interface IndexingService {
    ResponseService startIndexingAll();
    ResponseService stopIndexing();
    ResponseService startIndexingOne(String url);
}
