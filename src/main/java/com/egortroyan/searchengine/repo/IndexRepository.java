package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Indexing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends CrudRepository<Indexing, Integer> {
}

