package com.egortroyan.searchengine.controllers;

import com.egortroyan.searchengine.Search;
import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.service.responses.SearchResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class SearchController {

    @Autowired
    Search search;

    @GetMapping("/api/search")
    public ResponseEntity<Object> search(
            @RequestParam(name="query", required=false, defaultValue="") String query,
            @RequestParam(name="site", required=false, defaultValue="") String site,
            @RequestParam(name="offset", required=false, defaultValue="0") int offset,
            @RequestParam(name="limit", required=false, defaultValue="0") int limit) throws IOException {
        SearchResponseService service;
        if(site.equals("")) {
            service = search.searchService(new Request(query), null, offset, limit);
        } else {
            service = search.searchService(new Request(query), site, offset, limit);
        }

        return new ResponseEntity<Object> (service, HttpStatus.OK);
    }


}
