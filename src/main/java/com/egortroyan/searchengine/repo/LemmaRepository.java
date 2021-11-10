package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Lemma;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    Lemma findByLemma (String lemma);
}