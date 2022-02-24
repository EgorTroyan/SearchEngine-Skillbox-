package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.Search;
import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.service.SearchService;
import com.egortroyan.searchengine.service.responses.FalseResponseService;
import com.egortroyan.searchengine.service.responses.ResponseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LogManager.getLogger(SearchServiceImpl.class);

    private final Search search;

    public SearchServiceImpl(Search search) {
        this.search = search;
    }

    ResponseService response;

    @Override
    public ResponseService getResponse(Request request, String url, int offset, int limit) throws IOException {
        log.info("Запрос на поиск строки- \"" + request.getReq() + "\"");
        if (request.getReq().equals("")){
            response = new FalseResponseService("Задан пустой поисковый запрос");
            log.warn("Задан пустой поисковый запрос");
            return response;
            }
        if(url.equals("")) {
            response = search.searchService(request, null, offset, limit);
        } else {
            response = search.searchService(request, url, offset, limit);
        }
        if (response.getResult()) {
            log.info("Запрос на поиск строки обработан, результат получен.");
            return response;
        } else {
            log.warn("Запрос на поиск строки обработан, указанная страница не найдена.");
            return new FalseResponseService("Указанная страница не найдена");
        }
    }
}
