package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Lemma;
import com.egortroyan.searchengine.models.Request;
import com.egortroyan.searchengine.models.Response;
import com.egortroyan.searchengine.repo.FieldRepository;
import com.egortroyan.searchengine.repo.IndexRepository;
import com.egortroyan.searchengine.repo.LemmaRepository;
import com.egortroyan.searchengine.repo.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Search {
    @Autowired
    SiteIndexing siteIndexing;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;

    private Request request;
    private Response response;
    private boolean isReady = false;



    public Search() {
    }

    public Search(Request request) throws InterruptedException {
        System.out.println("Indexing");
        while (!siteIndexing.isSiteIndexingReady()){
            System.out.println(".");
            Thread.sleep(100);
        }
        System.out.println("Site is indexing!");
        this.request = request;
        Lemma lemma = lemmaRepository.findByLemma("почта");
        System.out.println(lemma.getLemma());
    }

    public Response getResponse() {
        return response;
    }

    private List<Lemma> sortedReqLemmas(Request request){
        List<Lemma> lemmaList = new ArrayList<>();
        List<String> list = request.getReqLemmas();
        for(String s : list) {
            Lemma lemma = lemmaRepository.findByLemma(s);
            if (lemma == null){
                return null;
            } else {
                lemmaList.add(lemma);
            }
        }
        lemmaList.sort((o1, o2) -> o1.getFrequency() - o2.getFrequency());
        return lemmaList;
    }


}
