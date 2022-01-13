package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Site;
import com.egortroyan.searchengine.models.Status;
import com.egortroyan.searchengine.service.impl.RepositoriesServiceImpl;
import com.egortroyan.searchengine.service.indexResponseEntity.Detailed;
import com.egortroyan.searchengine.service.responses.StatisticResponseService;
import com.egortroyan.searchengine.service.indexResponseEntity.Statistics;
import com.egortroyan.searchengine.service.indexResponseEntity.Total;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Statistic {
    @Autowired
    RepositoriesServiceImpl repo;

    public StatisticResponseService getStatistic(){
        Total total = getTotal();
        List<Site> siteList = repo.getAllSites();
        Detailed[] detaileds = new Detailed[siteList.size()];
        for (int i = 0; i < siteList.size(); i++) {
            detaileds[i] = getDetailed(siteList.get(i));
        }
        return new StatisticResponseService(true, new Statistics(total, detaileds));
    }

    public Total getTotal(){
        long sites = repo.siteCount();
        long lemmas = repo.lemmaCount();
        long pages = repo.pageCount();
        boolean isIndexing = isSitesIndexing();
        return new Total(sites, pages, lemmas, isIndexing);

    }

    public Detailed getDetailed(Site site){
        String url = site.getUrl();
        String name = site.getName();
        Status status = site.getStatus();
        long statusTime = site.getStatusTime().getTime();
        String error = site.getLastError();
        long pages = repo.pageCount(site.getId());
        long lemmas = repo.lemmaCount(site.getId());
        return new Detailed(url, name, status, statusTime, error, pages, lemmas);
    }

    public boolean isSitesIndexing(){
        boolean is = true;
        for(Site s : repo.getAllSites()){
            if(!s.getStatus().equals(Status.INDEXED)){
                is = false;
                break;
            }
        }
    return is;
    }
}
