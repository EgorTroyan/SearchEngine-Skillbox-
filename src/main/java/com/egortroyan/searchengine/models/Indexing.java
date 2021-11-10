package com.egortroyan.searchengine.models;

import javax.persistence.*;

@Entity
@Table(name="Search_index")
public class Indexing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int page_id;
    private int lemma_id;
    private float ranking;

    public Indexing() {
    }

    public Indexing(int page_id, int lemma_id, float ranking) {
        this.page_id = page_id;
        this.lemma_id = lemma_id;
        this.ranking = ranking;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getLemma_id() {
        return lemma_id;
    }

    public void setLemma_id(int lemma_id) {
        this.lemma_id = lemma_id;
    }

    public float getRank() {
        return ranking;
    }

    public void setRank(float rank) {
        this.ranking = rank;
    }
}
