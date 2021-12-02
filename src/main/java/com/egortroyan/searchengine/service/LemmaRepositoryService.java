package com.egortroyan.searchengine.service;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Lemma;

import java.util.List;

public interface LemmaRepositoryService {
    Lemma getLemma (String lemmaName);
    void save(Lemma lemma);

}
