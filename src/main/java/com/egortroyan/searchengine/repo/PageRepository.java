package com.egortroyan.searchengine.repo;

import com.egortroyan.searchengine.models.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {
    Page findByPath (String path);

    @Query(value = "SELECT count(*) from Page where site_id = :id")
    long count(@Param("id") long id);
}