package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Site;
import com.egortroyan.searchengine.models.Status;
import com.egortroyan.searchengine.service.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class Index {

    private final SearchSettings searchSettings;

    private final FieldRepositoryService fieldRepositoryService;
    private final SiteRepositoryService siteRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;

    public Index(SearchSettings searchSettings,
                 FieldRepositoryService fieldRepositoryService,
                 SiteRepositoryService siteRepositoryService,
                 IndexRepositoryService indexRepositoryService,
                 PageRepositoryService pageRepositoryService,
                 LemmaRepositoryService lemmaRepositoryService) {
        this.searchSettings = searchSettings;
        this.fieldRepositoryService = fieldRepositoryService;
        this.siteRepositoryService = siteRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
    }

    //private final List<Thread> threads = new ArrayList<>();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    public boolean allSiteIndexing() throws InterruptedException {
        fieldInit();
        boolean isIndexing;
        List<Site> siteList = getSiteListFromConfig();
        for (Site site : siteList) {
            isIndexing = startSiteIndexing(site);
            //executor.shutdown();
            if (!isIndexing){
                stopSiteIndexing();
                return false;
            }
        }
        return true;
    }

    public String checkedSiteIndexing(String url) throws InterruptedException {
        List<Site> siteList = siteRepositoryService.getAllSites();
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
            Site site = siteRepositoryService.getSite(baseUrl);
            site.setUrl(url);
            SiteIndexing indexing = new SiteIndexing(
                    site,
                    searchSettings,
                    fieldRepositoryService,
                    siteRepositoryService,
                    indexRepositoryService,
                    pageRepositoryService,
                    lemmaRepositoryService,
                    false);
            //indexing.start();
            //threads.add(indexing);
            //indexing.join();
            //cleanTreadList();
            executor.execute(indexing);
           // executor.shutdown();
            site.setUrl(baseUrl);
            siteRepositoryService.save(site);
            return "true";
        }
    }


    private void fieldInit() {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        if (fieldRepositoryService.getFieldByName("title") == null) {
            fieldRepositoryService.save(fieldTitle);
            fieldRepositoryService.save(fieldBody);
        }
    }

    private boolean startSiteIndexing(Site site) throws InterruptedException {
        Site site1 = siteRepositoryService.getSite(site.getUrl());
        if (site1 == null) {
            siteRepositoryService.save(site);
            SiteIndexing indexing = new SiteIndexing(
                    siteRepositoryService.getSite(site.getUrl()),
                    searchSettings,
                    fieldRepositoryService,
                    siteRepositoryService,
                    indexRepositoryService,
                    pageRepositoryService,
                    lemmaRepositoryService,
                    true);
            //indexing.start();
            //threads.add(indexing);
            executor.execute(indexing);
            //executor.shutdown();
            return true;
        } else {
            if (!site1.getStatus().equals(Status.INDEXING)){
                SiteIndexing indexing = new SiteIndexing(
                        siteRepositoryService.getSite(site.getUrl()),
                        searchSettings,
                        fieldRepositoryService,
                        siteRepositoryService,
                        indexRepositoryService,
                        pageRepositoryService,
                        lemmaRepositoryService,
                        true);
                //indexing.start();
                //threads.add(indexing);
                executor.execute(indexing);
               // executor.shutdown();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean stopSiteIndexing(){
        boolean isThreadAlive = false;
        if(executor.getActiveCount() == 0){
            return false;
        }

        executor.shutdownNow();
        try {
            isThreadAlive = executor.awaitTermination(5,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for(Thread thread : threads) {
//            if(thread.isAlive()) {
//                System.out.println("Останавливаем поток" + thread);
//
//                thread.interrupt();
//                while (thread.isInterrupted()){
//                    try {
//                        thread.join(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                isThreadAlive = true;
//            }
//        }
        if (isThreadAlive){
            List<Site> siteList = siteRepositoryService.getAllSites();
            for(Site site : siteList) {
                site.setStatus(Status.FAILED);
                siteRepositoryService.save(site);
            }
        }
        //cleanTreadList();
        return isThreadAlive;
    }

//    private void cleanTreadList () {
//        threads.removeIf(t -> !t.isAlive());
//    }isAlive

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
