package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Indexing;
import com.egortroyan.searchengine.models.Lemma;

import java.util.List;

public interface LemmaRepositoryService {
    Lemma getLemma (String lemmaName);
    void save(Lemma lemma);
    long lemmaCount();
    long lemmaCount(long siteId);
    void deleteAllLemmas(List<Lemma> lemmaList);
    List<Lemma> findLemmasByIndexing(List<Indexing> indexingList);
}
