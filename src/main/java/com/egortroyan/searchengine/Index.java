package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Site;
import com.egortroyan.searchengine.models.Status;
import com.egortroyan.searchengine.service.impl.RepositoriesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Index {
    @Autowired
    SearchSettings searchSettings;
    @Autowired
    RepositoriesServiceImpl repositoriesService;
    private final List<Thread> threads = new ArrayList<>();

    public boolean allSiteIndexing(){
        boolean isIndexing;
        List<Site> siteList = getSiteListFromConfig();
        for (Site site : siteList) {
            isIndexing = startSiteIndexing(site);
            if (!isIndexing){
                stopSiteIndexing();
                return false;
            }
        }
        return true;
    }

    public String checkedSiteIndexing(String url){
        boolean isPresent = false;
        int siteIndex = 0;
        List<Site> siteList = getSiteListFromConfig();
        for (Site s : siteList){
            if (s.getUrl().equals(url)) {
                isPresent = true;
                siteIndex = siteList.indexOf(s);
                break;
            }
        }
        if (!isPresent){
            return "not found";
        } else {
            boolean isStarted = startSiteIndexing(siteList.get(siteIndex));
            if (isStarted) {
                return "true";
            } else {
                return "false";
            }
        }
    }

    private boolean startSiteIndexing(Site site){
        Site site1 = repositoriesService.getSite(site.getUrl());
        if (site1 == null) {
            repositoriesService.save(site);
            SiteIndexing indexing = new SiteIndexing(
                    repositoriesService.getSite(site.getUrl()),
                    searchSettings,
                    repositoriesService);
            indexing.start();
            threads.add(indexing);
            return true;
        } else {
            if (!site1.getStatus().equals(Status.INDEXING)){
                SiteIndexing indexing = new SiteIndexing(
                        repositoriesService.getSite(site.getUrl()),
                        searchSettings,
                        repositoriesService);
                indexing.start();
                threads.add(indexing);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean stopSiteIndexing(){
        boolean isThreadAlive = false;
        for(Thread thread : threads) {
            if(thread.isAlive()) {
                isThreadAlive = true;
                thread.interrupt();
            }
        }
        return isThreadAlive;
    }



    private List<Site> getSiteListFromConfig() {
        List<Site> siteList = new ArrayList<>();
        List<HashMap<String, String>> sites = searchSettings.getSite();
        for (HashMap<String, String> map : sites) {
            String url = "";
            String name = "";
            for (Map.Entry<String, String> siteInfo : map.entrySet()) {
                if (siteInfo.getKey().equals("name")) {
                    name = siteInfo.getValue();
                }
                if (siteInfo.getKey().equals("url")) {
                    url = siteInfo.getValue();
                }
            }
            Site site = new Site();
            site.setUrl(url);
            site.setName(name);
            site.setStatus(Status.FAILED);
            siteList.add(site);
        }
        return siteList;
    }
}
