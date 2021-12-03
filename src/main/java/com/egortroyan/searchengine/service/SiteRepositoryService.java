package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Site;

import java.util.List;

public interface SiteRepositoryService {
    Site getSite (String url);
    void save(Site site);
    long siteCount();

}
