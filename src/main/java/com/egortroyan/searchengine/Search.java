package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.*;
import com.egortroyan.searchengine.morphology.MorphologyAnalyzer;
import com.egortroyan.searchengine.repo.FieldRepository;
import com.egortroyan.searchengine.repo.IndexRepository;
import com.egortroyan.searchengine.repo.LemmaRepository;
import com.egortroyan.searchengine.repo.PageRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class Search {
    @Autowired
    PageRepository pageRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;

    public Search() {
    }

    public List<Response> searching(Request request) throws IOException {
        List<Response> responses = new ArrayList<>();
        List<Lemma> reqLemmas = sortedReqLemmas(request);
        List<Integer> pageIndexes = new ArrayList<>();
        if (!(reqLemmas == null)) {
            List<Indexing> indexingList = indexRepository.findByLemmaId(reqLemmas.get(0).getId());
            indexingList.forEach(indexing ->
                    pageIndexes.add(indexing.getPageId())
            );
            for (Lemma lemma : reqLemmas) {
                if (!pageIndexes.isEmpty()) {
                    List<Indexing> indexingList2 = indexRepository.findByLemmaId(lemma.getId());
                    List<Integer> tempList = new ArrayList<>();
                    indexingList2.forEach(indexing -> tempList.add(indexing.getPageId()));
                    pageIndexes.retainAll(tempList);
                } else {
                    return responses;
                }
            }
            Map<Page, Double> pageAbsRelevance = new HashMap<>();
            HashMap<Page, Double> pageRelevance = new HashMap<>();
            double maxRel = 0.0;
            for (Integer p : pageIndexes) {
                Optional<Page> opPage = pageRepository.findById(p);
                if (opPage.isPresent()) {
                    Page page = opPage.get();
                    double r = getAbsRelevance(page, reqLemmas);
                    pageAbsRelevance.put(page, r);
                    if (r > maxRel)
                        maxRel = r;
                }
            }
            for (Map.Entry<Page, Double> abs : pageAbsRelevance.entrySet()) {
                pageRelevance.put(abs.getKey(), abs.getValue() / maxRel);
            }
            LinkedHashMap<Page, Double> sortedByRankPages = sortedByRelevancePageMap(pageRelevance);
            for (Map.Entry<Page, Double> page : sortedByRankPages.entrySet()) {
                Response response = getResponseByPage(page.getKey(), request, page.getValue());
                responses.add(response);
            }
        }

        return responses;
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

    private double getAbsRelevance(Page page, List<Lemma> lemmas){
        double r = 0.0;
        int pageId = page.getId();
        for (Lemma lemma : lemmas) {
            int lemmaId = lemma.getId();
            Indexing indexing = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
            r = r + indexing.getRank();
        }
        return r;
    }

    private LinkedHashMap<Page, Double> sortedByRelevancePageMap (HashMap<Page, Double> map) {
        SortedSet<Map.Entry<Page, Double>> sortedset = new TreeSet<>(
                new Comparator<Map.Entry<Page, Double>>() {
                    @Override
                    public int compare(Map.Entry<Page, Double> e1,
                                       Map.Entry<Page, Double> e2) {
                        return e1.getValue().compareTo(e2.getValue());
                    }
                });

        sortedset.addAll(map.entrySet());
        LinkedHashMap<Page, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Page, Double> m : sortedset) {
            sortedMap.put(m.getKey(), m.getValue());
        }
        return sortedMap;
    }

    private Response getResponseByPage (Page page, Request request, double relevance) throws IOException {
        Response response = new Response();
        String uri = page.getPath();
        String title = getTitle(page.getContent());
        String snippet = getSnippet(page.getContent(), request);
        response.setRelevance(relevance);
        response.setUri(uri);
        response.setTitle(title);
        response.setSnippet(snippet);
        return response;
    }

    private String getTitle (String html){
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select("title");
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (!builder.isEmpty()){
            string = builder.toString();
        }
        return string;
    }

    private String getSnippet (String html, Request request) throws IOException {
        MorphologyAnalyzer analyzer = new MorphologyAnalyzer();
        String snippet = "";
        String string = "";
        Document document = Jsoup.parse(html);
        Elements titleElements = document.select("title");
        Elements bodyElements = document.select("body");
        StringBuilder builder = new StringBuilder();
        titleElements.forEach(element -> builder.append(element.text()).append(" ").append("\n"));
        bodyElements.forEach(element -> builder.append(element.text()).append(" "));
        if (!builder.isEmpty()){
            string = builder.toString();
        }
        List<String> req = request.getReqLemmas();
        ArrayList<Integer>[] reqIndexes = new ArrayList[req.size()];
        for (int i = 0; i < reqIndexes.length; i++) {
            reqIndexes[i] = analyzer.findLemmaIndexInText(string, req.get(i));
        }
        int endSnippetIndex;
        int snippetLength = request.getReq().length();
        int first;
        int last;
        for (int i = 0; i < reqIndexes.length - 1; i++) {
            for (int j = 0; j < reqIndexes[i + 1].size(); j++) {
                first = reqIndexes[i].get(i);
                last = reqIndexes[i + 1].get(j);
                if (first == last - req.get(i).length() - 1){
                    endSnippetIndex = last + req.get(i).length();
                    snippet = string.substring(
                            string.lastIndexOf(" ", endSnippetIndex - snippetLength - 20),
                            string.indexOf(" ", endSnippetIndex + 10));
                    break;
                }
            }
        }
        StringBuilder builder1 = new StringBuilder();
        builder1.append("...")
                .append("<b>")
                .append(snippet)
                .append("</b>")
                .append("...");
        return builder1.toString();
    }


}
