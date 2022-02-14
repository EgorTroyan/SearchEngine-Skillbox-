package com.egortroyan.searchengine.sitemap;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseUrlTest {

    @Test
    void computeTestInterrupted() {
        ParseUrl parseUrl = new ParseUrl("https://jsoup.org/", true);
        assertEquals("", parseUrl.compute());
    }

    @Test
    void computeTest() {
        ParseUrl parseUrl = new ParseUrl("https://jsoup.org/", false);
        MatcherAssert.assertThat(parseUrl.compute(), Matchers.containsString("https://jsoup.org/\nhttps://jsoup.org/news/"));
    }
}