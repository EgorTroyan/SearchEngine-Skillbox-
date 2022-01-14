package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.models.Indexing;
import com.egortroyan.searchengine.models.Lemma;
import com.egortroyan.searchengine.repo.LemmaRepository;
import com.egortroyan.searchengine.service.LemmaRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LemmaRepoServiceImpl implements LemmaRepositoryService {

    private final LemmaRepository lemmaRepository;

    public LemmaRepoServiceImpl(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public Lemma getLemma(String lemmaName) {
        Lemma lemma = null;
        try{
            lemma = lemmaRepository.findByLemma(lemmaName);
        } catch (Exception e) {
            System.out.println(lemmaName);
            e.printStackTrace();
        }
        return lemma;
    }

    @Override
    public synchronized void save(Lemma lemma) {
        lemmaRepository.save(lemma);
    }

    @Override
    public long lemmaCount(){
        return lemmaRepository.count();
    }

    @Override
    public long lemmaCount(long siteId){
        return lemmaRepository.count(siteId);
    }

    @Override
    public synchronized void deleteAllLemmas(List<Lemma> lemmaList){
        lemmaRepository.deleteAll(lemmaList);
    }

    @Override
    public List<Lemma> findLemmasByIndexing(List<Indexing> indexingList){
        int[] lemmaIdList = new int[indexingList.size()];
        for (int i = 0; i < indexingList.size(); i++) {
            lemmaIdList[i] = indexingList.get(i).getLemmaId();
        }
        return lemmaRepository.findById(lemmaIdList);
    }
}
