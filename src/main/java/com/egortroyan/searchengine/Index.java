package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Field;
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


    public boolean allSiteIndexing() throws InterruptedException {
        fieldInit();
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

    public String checkedSiteIndexing(String url) throws InterruptedException {
        List<Site> siteList = repositoriesService.getAllSites();
        String baseUrl = "";
        for(Site site : siteList) {
            if(site.getStatus() != Status.INDEXED) {
                return "false";
            }
            if(url.contains(site.getUrl())){
                baseUrl = site.getUrl();
            }
        }
        if(baseUrl.isEmpty()){
            return "not found";
        } else {
            Site site = repositoriesService.getSite(baseUrl);
            site.setUrl(url);
            SiteIndexing indexing = new SiteIndexing(
                    site,
                    searchSettings,
                    repositoriesService, false);
            indexing.start();
            threads.add(indexing);
            indexing.join();
            site.setUrl(baseUrl);
            repositoriesService.save(site);
            return "true";
        }
    }

    private void fieldInit() {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        if (repositoriesService.getFieldByName("title") == null) {
            repositoriesService.save(fieldTitle);
            repositoriesService.save(fieldBody);
        }
    }

    private boolean startSiteIndexing(Site site) throws InterruptedException {
        Site site1 = repositoriesService.getSite(site.getUrl());
        if (site1 == null) {
            repositoriesService.save(site);
            SiteIndexing indexing = new SiteIndexing(
                    repositoriesService.getSite(site.getUrl()),
                    searchSettings,
                    repositoriesService, true);
            indexing.start();
            threads.add(indexing);
            //indexing.join();
            return true;
        } else {
            if (!site1.getStatus().equals(Status.INDEXING)){
                SiteIndexing indexing = new SiteIndexing(
                        repositoriesService.getSite(site.getUrl()),
                        searchSettings,
                        repositoriesService, true);
                indexing.start();
                threads.add(indexing);
                //indexing.join();
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
                System.out.println("Останавливаем поток" + thread);
                isThreadAlive = true;
                thread.interrupt();
            }
        }
        if (isThreadAlive){
            List<Site> siteList = repositoriesService.getAllSites();
            for(Site site : siteList) {
                site.setStatus(Status.FAILED);
                repositoriesService.save(site);
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
