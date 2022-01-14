package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.Index;
import com.egortroyan.searchengine.service.IndexingService;
import com.egortroyan.searchengine.service.responses.FalseResponseService;
import com.egortroyan.searchengine.service.responses.ResponseService;
import com.egortroyan.searchengine.service.responses.TrueResponseService;
import org.springframework.stereotype.Service;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final Index index;

    public IndexingServiceImpl(Index index) {
        this.index = index;
    }

    @Override
    public ResponseService startIndexingAll() {
        ResponseService response;
        boolean indexing;
        try {
            indexing = index.allSiteIndexing();
        } catch (InterruptedException e) {
            response = new FalseResponseService("Ошибка запуска индексации");
            return response;
        }
        if (indexing) {
            response = new TrueResponseService();
        } else {
            response = new FalseResponseService("Индексация уже запущена");
        }
        return response;
    }

    @Override
    public ResponseService stopIndexing() {
        boolean indexing = index.stopSiteIndexing();
        ResponseService response;
        if (indexing) {
            response = new TrueResponseService();
        } else {
            response = new FalseResponseService("Индексация не запущена");
        }
        return response;
    }

    @Override
    public ResponseService startIndexingOne(String url) {
        ResponseService resp;
        String response;
        try {
            response = index.checkedSiteIndexing(url);
        } catch (InterruptedException e) {
            resp = new FalseResponseService("Ошибка запуска индексации");
            return resp;
        }

        if (response.equals("not found")) {
            resp = new FalseResponseService("Страница находится за пределами сайтов," +
                    " указанных в конфигурационном файле");
        }
        else if (response.equals("false")) {
            resp = new FalseResponseService("Индексация страницы уже запущена");
        }
        else {
            resp = new TrueResponseService();
        }
        return resp;
    }
}
