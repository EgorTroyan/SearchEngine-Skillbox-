package com.egortroyan.searchengine.controllers;

import com.egortroyan.searchengine.Index;
import com.egortroyan.searchengine.service.responses.FalseResponseService;
import com.egortroyan.searchengine.service.responses.ResponseService;
import com.egortroyan.searchengine.service.responses.TrueResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexingController {

    private final Index index;

    public IndexingController(Index index) {
        this.index = index;
    }

    @GetMapping("/api/startIndexing")
    public ResponseEntity<Object> startIndexingAll() {
        System.out.println("Starting indexing all");
        ResponseService response;
        boolean indexing = false;
        try {
            indexing = index.allSiteIndexing();
        } catch (InterruptedException e) {
            response = new FalseResponseService("Ошибка запуска индексации");
            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }
        if (indexing) {
            response = new TrueResponseService();
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } else {
            response = new FalseResponseService("Индексация уже запущена");
            return new ResponseEntity<Object>(response, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/api/stopIndexing")
    public ResponseEntity<Object> stopIndexingAll() {
        System.out.println("Stop indexing all");
        boolean indexing = index.stopSiteIndexing();
        ResponseService response;
        if (indexing) {
            response = new TrueResponseService();
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } else {
            response = new FalseResponseService("Индексация не запущена");
            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/indexPage")
    public ResponseEntity<Object> startIndexingOne(
            @RequestParam(name="url", required=false, defaultValue=" ")
                    String url) {
        System.out.println("Попытка запуска индексации отдельной страницы: " + url);
        ResponseService resp;
        String response = null;
        try {
            response = index.checkedSiteIndexing(url);
        } catch (InterruptedException e) {
            resp = new FalseResponseService("Ошибка запуска индексации");
            return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
        }

        if (response.equals("not found")) {
            resp = new FalseResponseService("Страница находится за пределами сайтов," +
                    " указанных в конфигурационном файле");
            System.out.println(resp.toString());
            return new ResponseEntity<Object>(resp, HttpStatus.NOT_FOUND);
        }
        else if (response.equals("false")) {
            resp = new FalseResponseService("Индексация страницы уже запущена");
            System.out.println(resp.toString());
            return new ResponseEntity<Object>(resp, HttpStatus.TOO_MANY_REQUESTS);
        }
        else {
            resp = new TrueResponseService();
            System.out.println("ОК!");
            return new ResponseEntity<Object>(resp, HttpStatus.OK);
        }
    }
}
