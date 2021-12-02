package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Indexing;

import java.util.List;

public interface IndexRepositoryService {
    List<Indexing> getAllIndexing (int lemmaId);
    Indexing getIndexing (int lemmaId, int pageId);
    void save(Indexing indexing);

}
