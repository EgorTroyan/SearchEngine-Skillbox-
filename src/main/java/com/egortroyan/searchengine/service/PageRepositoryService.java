package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Page;

import java.util.List;
import java.util.Optional;

public interface PageRepositoryService {
    Page getPage (String pagePath);
    void save(Page page);
    Optional<Page> findPageById(int id);
    long pageCount();
}
