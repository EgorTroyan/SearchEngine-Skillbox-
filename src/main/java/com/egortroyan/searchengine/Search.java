package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.*;
import com.egortroyan.searchengine.repo.FieldRepository;
import com.egortroyan.searchengine.repo.IndexRepository;
import com.egortroyan.searchengine.repo.LemmaRepository;
import com.egortroyan.searchengine.repo.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Search {
//    @Autowired
//    SiteIndexing siteIndexing;
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

//    public Search(Request request) throws InterruptedException {
//        System.out.println("Indexing");
//        while (!siteIndexing.isSiteIndexingReady()){
//            System.out.println(".");
//            Thread.sleep(100);
//        }
//        System.out.println("Site is indexing!");
//        this.request = request;
//        Lemma lemma = lemmaRepository.findByLemma("почта");
//        System.out.println(lemma.getLemma());
//    }

    public String searching(Request request){
        List<Lemma> reqLemmas = sortedReqLemmas(request);
        System.out.println(reqLemmas);
        List<Integer> pageIndexes = new ArrayList<>();
        List<Indexing> indexingList = indexRepository.findByLemmaId(reqLemmas.get(0).getId());
        System.out.println(indexingList);
        indexingList.forEach(indexing ->
            pageIndexes.add(indexing.getPageId())
        );
        StringBuilder builder = new StringBuilder();
         for(Lemma lemma : reqLemmas) {
            if (!pageIndexes.isEmpty()) {
                List<Indexing> indexingList2 = indexRepository.findByLemmaId(lemma.getId());
                List<Integer> tempList = new ArrayList<>();
                indexingList2.forEach(indexing -> tempList.add(indexing.getPageId()));
                pageIndexes.retainAll(tempList);
            } else {
                builder.append("нет совпадений");
            }
        }
         for (Integer page : pageIndexes) {
             Optional<Page> page1 = pageRepository.findById(page);
             if (page1.isPresent()) {
                 Page page2 = page1.get();
                 System.out.println(page2);
                 builder.append(page2.getId()).append(" ").append(page2.getPath()).append("\n");
             }
         }

//        Lemma lemma = lemmaRepository.findByLemma("почта");
        return builder.toString();
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
