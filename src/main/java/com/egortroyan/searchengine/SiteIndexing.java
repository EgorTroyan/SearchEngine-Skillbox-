package com.egortroyan.searchengine;
import com.egortroyan.searchengine.models.*;
import com.egortroyan.searchengine.morphology.MorphologyAnalyzer;
import com.egortroyan.searchengine.repo.*;
import com.egortroyan.searchengine.service.impl.RepositoriesServiceImpl;
import com.egortroyan.searchengine.sitemap.SiteMapBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

//@Service
//@Scope("prototype")
public class SiteIndexing extends Thread{
    private boolean isReady = false;
    private final Site site;
    SearchSettings searchSettings;
    RepositoriesServiceImpl repo;

    public SiteIndexing(Site site,
                        SearchSettings settings,
                        RepositoriesServiceImpl repositoriesService){
        this.site = site;
        this.searchSettings = settings;
        this.repo = repositoriesService;
    }

    @Override
    public void run() {
        runAfterStartup(site.getUrl());
    }

    public void runAfterStartup(String searchUrl) {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        repo.save(site);
        fieldInit();
        SiteMapBuilder builder = new SiteMapBuilder(searchUrl);
        builder.builtSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        List<Field> fieldList = getFieldListFromDB();
        for(String url : allSiteUrls) {
            try {
                Page page = getSearchPage(url, searchUrl, site.getId());
                site.setStatusTime(new Date());
                repo.save(site);
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
                lemmaToDB(map, site.getId());
                map.clear();
                pageToDb(page);
                indexingToDb(indexing, page.getPath());
                indexing.clear();
            } catch (UnsupportedMimeTypeException e) {
                System.out.println("Страница пропущена из за ошибки чтения:\n" + url);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        site.setStatus(Status.INDEXED);
        repo.save(site);
        isReady = true;
    }

    private synchronized void fieldInit() {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        repo.save(fieldTitle);
        repo.save(fieldBody);
    }

    private synchronized void pageToDb(Page page) {
        repo.save(page);
    }

    private Page getSearchPage(String url, String baseUrl, int siteId) throws IOException {
        Page page = new Page();
        Connection.Response response = Jsoup.connect(url)
                .userAgent(searchSettings.getAgent())
                .referrer("http://www.google.com")
                .execute();

        String content = response.body();
        String path = url.replaceAll(baseUrl, "/");
        int code = response.statusCode();
        page.setCode(code);
        page.setPath(path);
        page.setContent(content);
        page.setSiteId(siteId);
        return page;
    }

    private List<Field> getFieldListFromDB() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = repo.getAllField();
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

    private synchronized void lemmaToDB (TreeMap<String, Integer> lemmaMap, int siteId) {
        for (Map.Entry<String, Integer> lemma : lemmaMap.entrySet()) {
            String lemmaName = lemma.getKey();
            Lemma lemma1 = repo.getLemma(lemmaName);
            if (lemma1 == null){
                Lemma newLemma = new Lemma(lemmaName, 1, siteId);
                repo.save(newLemma);
            } else {
                int count = lemma1.getFrequency();
                lemma1.setFrequency(++count);
                repo.save(lemma1);
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

    private synchronized void indexingToDb (TreeMap<String, Float> map, String path){
        for (Map.Entry<String, Float> lemma : map.entrySet()) {
            int pathId = repo.getPage(path).getId();
            String lemmaName = lemma.getKey();
            Lemma lemma1 = repo.getLemma(lemmaName);
            int lemmaId = lemma1.getId();
            //System.out.println(lemmaId);
            Indexing indexing = new Indexing(pathId, lemmaId, lemma.getValue());
            repo.save(indexing);
        }
    }

    public boolean isSiteIndexingReady() {
        return isReady;
    }
}
