package com.egortroyan.searchengine.controllers;


import com.egortroyan.searchengine.Search;
import com.egortroyan.searchengine.SiteIndexing;
import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    Search search;
//    @Autowired
//    SiteIndexing siteIndexing;

//    public MainController() throws InterruptedException {
//        siteIndexing.runAfterStartup();
//        System.out.print("Pls wait, Indexing");
//        while (!siteIndexing.isSiteIndexingReady()){
//            System.out.print(".");
//            Thread.sleep(100);
//        }
//        System.out.println("Site is indexing. Ready to search!");
//    }
    @GetMapping("/search")
    public String greeting(@RequestParam(name="text", required=false, defaultValue=" ") String text, Model model) throws IOException {
        List<Response> s = search.searching(new Request(text));
        model.addAttribute("responses", s);
        return "search";
    }


}
