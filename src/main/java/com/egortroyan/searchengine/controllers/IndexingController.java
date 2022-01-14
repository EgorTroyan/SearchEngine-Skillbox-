package com.egortroyan.searchengine.controllers;

import com.egortroyan.searchengine.service.IndexingService;
import com.egortroyan.searchengine.service.responses.ResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexingController {

    private final IndexingService index;

    public IndexingController(IndexingService index) {
        this.index = index;
    }

    @GetMapping("/api/startIndexing")
    public ResponseEntity<Object> startIndexingAll() {
        //System.out.println("Starting indexing all");
        ResponseService response = index.startIndexingAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/stopIndexing")
    public ResponseEntity<Object> stopIndexingAll() {
        //System.out.println("Stop indexing all");
        ResponseService response = index.stopIndexing();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/indexPage")
    public ResponseEntity<Object> startIndexingOne(
            @RequestParam(name="url", required=false, defaultValue=" ") String url) {
        //System.out.println("Попытка запуска индексации отдельной страницы: " + url);
        ResponseService response = index.startIndexingOne(url);
        return ResponseEntity.ok(response);
    }
}
