package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Site;

import java.util.List;

public interface SiteRepositoryService {
    Site getSite (String url);
    Site getSite (int siteId);
    void save(Site site);
    long siteCount();
    List<Site> getAllSites();
}
