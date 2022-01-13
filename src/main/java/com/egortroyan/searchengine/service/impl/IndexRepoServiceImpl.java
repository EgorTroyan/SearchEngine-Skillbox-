package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.models.Indexing;
import com.egortroyan.searchengine.repo.IndexRepository;
import com.egortroyan.searchengine.service.IndexRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexRepoServiceImpl implements IndexRepositoryService {

    private final IndexRepository indexRepository;

    public IndexRepoServiceImpl(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public synchronized List<Indexing> getAllIndexingByLemmaId(int lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }

    @Override
    public synchronized List<Indexing> getAllIndexingByPageId(int pageId) {
        return indexRepository.findByPageId(pageId);
    }

    @Override
    public void deleteAllIndexing(List<Indexing> indexingList){
        indexRepository.deleteAll(indexingList);
    }

    @Override
    public synchronized Indexing getIndexing(int lemmaId, int pageId) {
        Indexing indexing = null;
        try{
            indexing = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
        } catch (Exception e) {
            System.out.println("lemmaId: " + lemmaId + " + pageId: " + pageId + " not unique");
            e.printStackTrace();
        }
        return indexing;
    }

    @Override
    public synchronized void save(Indexing indexing) {
        indexRepository.save(indexing);
    }

}
