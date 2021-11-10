package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {
    Page findByPath (String path);
}