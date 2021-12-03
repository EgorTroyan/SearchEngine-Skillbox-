package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.models.*;
import com.egortroyan.searchengine.repo.*;
import com.egortroyan.searchengine.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoriesServiceImpl implements FieldRepositoryService,
                                                IndexRepositoryService,
                                                LemmaRepositoryService,
                                                PageRepositoryService,
                                                SiteRepositoryService {
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Override
    public synchronized Field getFieldByName(String fieldName) {
        return fieldRepository.findByName(fieldName);
    }
    @Override
    public synchronized void save(Field field) {
        fieldRepository.save(field);
    }

    @Override
    public synchronized List<Field> getAllField() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = fieldRepository.findAll();
        iterable.forEach(list::add);
        return list;
    }


    @Override
    public synchronized List<Indexing> getAllIndexing(int lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }

    @Override
    public synchronized Indexing getIndexing(int lemmaId, int pageId) {
        return indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
    }

    @Override
    public synchronized void save(Indexing indexing) {
        indexRepository.save(indexing);
    }



    @Override
    public synchronized Lemma getLemma(String lemmaName) {
        return lemmaRepository.findByLemma(lemmaName);
    }

    @Override
    public synchronized void save(Lemma lemma) {
        lemmaRepository.save(lemma);
    }

    @Override
    public long lemmaCount(){
        return lemmaRepository.count();
    }

    public long lemmaCount(long siteId){
        return lemmaRepository.count(siteId);
    }



    @Override
    public synchronized Page getPage(String pagePath) {
        return pageRepository.findByPath(pagePath);
    }

    @Override
    public synchronized void save(Page page) {
        pageRepository.save(page);
    }

    @Override
    public synchronized Optional<Page> findPageById(int id) {
        return pageRepository.findById(id);
    }

    @Override
    public long pageCount(){
        return pageRepository.count();
    }

    public long pageCount(long siteId){
        return pageRepository.count(siteId);
    }


    @Override
    public synchronized Site getSite(String url) {
        return siteRepository.findByUrl(url);
    }

    @Override
    public synchronized void save(Site site) {
        siteRepository.save(site);
    }

    @Override
    public long siteCount(){
        return siteRepository.count();
    }

    public List<Site> getAllSites() {
        List<Site> siteList = new ArrayList<>();
        Iterable<Site> it = siteRepository.findAll();
        it.forEach(siteList::add);
        return siteList;
    }


}
