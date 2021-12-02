package com.egortroyan.searchengine.controllers;

import com.egortroyan.searchengine.Index;
import com.egortroyan.searchengine.service.responses.ResponseService;
import com.egortroyan.searchengine.service.responses.FalseResponseService;
import com.egortroyan.searchengine.service.responses.TrueResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexingController {
    @Autowired
    Index index;

    @GetMapping("/api/startIndexing")
    public String startIndexingAll(Model model) {
        System.out.println("Starting indexing all");
        ResponseService response;
        boolean indexing = index.allSiteIndexing();
        if (indexing) {
            response = new TrueResponseService();
        } else {
            response = new FalseResponseService("Индексация уже запущена");
        }
        model.addAttribute(response);
        return "index";
    }

    @GetMapping("/api/stopIndexing")
    public String stopIndexingAll(Model model) {
        System.out.println("Stop indexing all");
        boolean indexing = index.stopSiteIndexing();
        ResponseService response;
        if (indexing) {
            response = new TrueResponseService();
        } else {
            response = new FalseResponseService("Индексация не запущена");
        }
        model.addAttribute("stopIndexing", response);
        return "index";
    }

    @PostMapping("/api/indexPage")
    public String startIndexingOne(@RequestParam(name="url", required=false, defaultValue=" ") String url, Model model) {
        System.out.println("Indexing url: " + url);
        String response = index.checkedSiteIndexing(url);
        ResponseService resp;
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
        model.addAttribute(resp);
        return "index";
    }
}
