package com.egortroyan.searchengine.controllers;


import com.egortroyan.searchengine.Search;
import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.models.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/search")
    public Response greeting(@RequestParam(name="text", required=false, defaultValue="") String text, Model model) throws InterruptedException {
        Request request = new Request(text);
        Search search = new Search(request);

        return null;
    }
}
