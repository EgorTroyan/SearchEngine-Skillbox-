package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Lemma;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    Lemma findByLemma (String lemma);

    @Query(value = "SELECT count(*) from Lemma where site_id = :id")
    long count(@Param("id") long id);
}