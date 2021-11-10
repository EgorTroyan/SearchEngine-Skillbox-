package com.egortroyan.searchengine;

import com.egortroyan.searchengine.models.Field;
import com.egortroyan.searchengine.models.Indexing;
import com.egortroyan.searchengine.models.Lemma;
import com.egortroyan.searchengine.models.Page;
import com.egortroyan.searchengine.morphology.MorphologyAnalyzer;
import com.egortroyan.searchengine.repo.FieldRepository;
import com.egortroyan.searchengine.repo.IndexRepository;
import com.egortroyan.searchengine.repo.LemmaRepository;
import com.egortroyan.searchengine.repo.PageRepository;
import com.egortroyan.searchengine.sitemap.SiteMapBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class SiteIndexing {
    private boolean isReady = false;
    @Value("${searchUrl}")
    private String searchUrl;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        fieldInit();
        SiteMapBuilder builder = new SiteMapBuilder(searchUrl);
        List<String> allSiteUrls = builder.getSiteMap();
        List<Field> fieldList = getFieldListFromDB();
        for(String url : allSiteUrls) {
            try {
                Page page = getSearchPage(url);
                TreeMap<String, Integer> map = new TreeMap<>();
                TreeMap<String, Float> indexing = new TreeMap<>();
                for (Field field : fieldList){
                    String name = field.getName();
                    float weight = field.getWeight();
                    String stringByTeg = getStringByTeg(name, page.getContent());
                    MorphologyAnalyzer analyzer = new MorphologyAnalyzer();
                    TreeMap<String, Integer> tempMap = analyzer.textAnalyzer(stringByTeg);
                    map.putAll(tempMap);
                    indexing.putAll(indexingLemmas(tempMap, weight));
                }
                lemmaToDB(map);
                map.clear();
                pageRepository.save(page);
                indexingToDb(indexing, page.getPath());
                indexing.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isReady = true;
    }

    private void fieldInit() {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        fieldRepository.save(fieldTitle);
        fieldRepository.save(fieldBody);
    }

    private Page getSearchPage(String url) throws IOException {
        Page page = new Page();
        Connection.Response response = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .execute();

        String content = response.body();
        String path = url.replaceAll(searchUrl, "/");
        int code = response.statusCode();
        page.setCode(code);
        page.setPath(path);
        page.setContent(content);
        return page;
    }

    private List<Field> getFieldListFromDB() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = fieldRepository.findAll();
        iterable.forEach(list::add);
        return list;
    }

    private String getStringByTeg (String teg, String html) {
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select(teg);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (!builder.isEmpty()){
            string = builder.toString();
        }
        return string;
    }

    private void lemmaToDB (TreeMap<String, Integer> lemmaMap) {
        for (Map.Entry<String, Integer> lemma : lemmaMap.entrySet()) {
            String lemmaName = lemma.getKey();
            Lemma lemma1 = lemmaRepository.findByLemma(lemmaName);
            if (lemma1 == null){
                Lemma newLemma = new Lemma(lemmaName, 1);
                lemmaRepository.save(newLemma);
            } else {
                int count = lemma1.getFrequency();
                Lemma newLemma = new Lemma(lemmaName, ++count);
                newLemma.setId(lemma1.getId());
                lemmaRepository.delete(lemma1);
                lemmaRepository.save(newLemma);
            }
        }
    }

    private TreeMap<String, Float> indexingLemmas (TreeMap<String, Integer> lemmas, float weight) {
        TreeMap<String, Float> map = new TreeMap<>();
        for (Map.Entry<String, Integer> lemma : lemmas.entrySet()) {
            String name = lemma.getKey();
            float w;
            if (!map.containsKey(name)) {
                w = (float) lemma.getValue() * weight;
            } else {
                w = map.get(name) + ((float) lemma.getValue() * weight);
            }
            map.put(name, w);
        }
        return map;
    }

    private void indexingToDb (TreeMap<String, Float> map, String path){
        for (Map.Entry<String, Float> lemma : map.entrySet()) {
            int path_id = pageRepository.findByPath(path).getId();
            int lemma_id = lemmaRepository.findByLemma(lemma.getKey()).getId();
            Indexing indexing = new Indexing(path_id, lemma_id, lemma.getValue());
            indexRepository.save(indexing);
        }
    }

    public boolean isIsReady() {
        return isReady;
    }
}
