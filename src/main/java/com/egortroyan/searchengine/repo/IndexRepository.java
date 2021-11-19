package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Indexing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Indexing, Integer> {
    List<Indexing> findByLemmaId (int lemmaId);
}

