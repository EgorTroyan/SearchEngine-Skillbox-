package com.egortroyan.searchengine.controllers;

import com.egortroyan.searchengine.Statistic;
import com.egortroyan.searchengine.service.responses.StatisticResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

    @Autowired
    Statistic statistic;

    @GetMapping("/api/statistics")
    public ResponseEntity<Object> getStatistics(){
        StatisticResponseService stat = statistic.getStatistic();
        return new ResponseEntity<Object> (stat, HttpStatus.OK);
    }
}
