package com.egortroyan.searchengine.service.impl;

import com.egortroyan.searchengine.models.Site;
import com.egortroyan.searchengine.repo.SiteRepository;
import com.egortroyan.searchengine.service.SiteRepositoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SiteRepoServiceImpl implements SiteRepositoryService {

    private final SiteRepository siteRepository;

    public SiteRepoServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public Site getSite(String url) {
        return siteRepository.findByUrl(url);
    }
    public Site getSite(int siteId) {
        Optional<Site> optional = siteRepository.findById(siteId);
        Site site = null;
        if(optional.isPresent()){
            site = optional.get();
        }
        return site;
    }

    @Override
    public synchronized void save(Site site) {
        siteRepository.save(site);
    }

    @Override
    public long siteCount(){
        return siteRepository.count();
    }

    @Override
    public List<Site> getAllSites() {
        List<Site> siteList = new ArrayList<>();
        Iterable<Site> it = siteRepository.findAll();
        it.forEach(siteList::add);
        return siteList;
    }
}
