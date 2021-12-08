package com.egortroyan.searchengine;
import com.egortroyan.searchengine.models.*;
import com.egortroyan.searchengine.morphology.MorphologyAnalyzer;
import com.egortroyan.searchengine.service.impl.RepositoriesServiceImpl;
import com.egortroyan.searchengine.sitemap.SiteMapBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

//@Service
//@Scope("prototype")
public class SiteIndexing extends Thread{
    private final Site site;
    SearchSettings searchSettings;
    RepositoriesServiceImpl repo;
    private boolean allSite;

    public SiteIndexing(Site site,
                        SearchSettings settings,
                        RepositoriesServiceImpl repositoriesService, boolean allSite){
        this.site = site;
        this.searchSettings = settings;
        this.repo = repositoriesService;
        this.allSite = allSite;
    }

    @Override
    public void run() {
        if (allSite) {
            runAllIndexing();
        } else {
            runOneSiteIndexing(site.getUrl());
        }

    }

    public void runAllIndexing() {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        repo.save(site);
        SiteMapBuilder builder = new SiteMapBuilder(site.getUrl());
        builder.builtSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        for(String url : allSiteUrls) {
            runOneSiteIndexing(url);
        }
    }

    public void runOneSiteIndexing(String searchUrl) {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        repo.save(site);
        List<Field> fieldList = getFieldListFromDB();
        try {
            Page page = getSearchPage(searchUrl, site.getUrl(), site.getId());
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
        }
        catch (UnsupportedMimeTypeException e) {
            site.setLastError("Формат страницы не поддерживается: " + searchUrl);
            site.setStatus(Status.FAILED);
        }
        catch (IOException e) {
            site.setLastError("Ошибка чтения страницы: " + searchUrl + "\n" + e.getMessage());
            site.setStatus(Status.FAILED);
        }
        finally {
            repo.save(site);
        }
        site.setStatus(Status.INDEXED);
        repo.save(site);
    }


    private synchronized void pageToDb(Page page) {
        if(repo.getPage(page.getPath()) == null) {
            repo.save(page);
        }
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
            Indexing indexing = new Indexing(pathId, lemmaId, lemma.getValue());
            repo.save(indexing);
        }
    }
}
