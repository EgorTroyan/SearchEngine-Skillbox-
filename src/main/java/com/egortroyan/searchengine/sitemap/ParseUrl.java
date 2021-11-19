package com.egortroyan.searchengine.sitemap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;

public class ParseUrl extends RecursiveTask<String> {
    public final static List<String> urlList = new Vector<>();

    private final String url;

    public ParseUrl(String url) {
        this.url = url;
    }

    @Override
    protected String compute() {
        StringBuilder result = new StringBuilder();
        result.append(url);
        try {
            Thread.sleep(200);
            Document doc = Jsoup.connect(url)
                    .maxBodySize(0)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            Elements rootElements = doc.select("a");

            List<ParseUrl> linkGrabers = new ArrayList<>();
            rootElements.forEach(element -> {
                String link = element.attr("abs:href");
                if (link.startsWith(element.baseUri())
                        && !link.equals(element.baseUri())
                        && !link.contains("#")
                        && !link.contains(".pdf")
                        && !urlList.contains(link)
                ) {
                    urlList.add(link);
                    ParseUrl linkGraber = new ParseUrl(link);
                    linkGraber.fork();
                    linkGrabers.add(linkGraber);
                }
            });

            for (ParseUrl lg : linkGrabers) {
                String text = lg.join();
                if (!text.equals("")) {
                    result.append("\n");
                    result.append(text);
                }
            }
        } catch (IOException | InterruptedException e) {
            /*Пропускаем сайт не удовлетворяющий условиям*/;
        }
        return result.toString();
    }
}
