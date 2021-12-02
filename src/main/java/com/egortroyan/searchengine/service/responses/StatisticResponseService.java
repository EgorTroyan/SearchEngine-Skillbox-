package com.egortroyan.searchengine.service.responses;

public class StatisticResponseService {
    boolean result;
    Statistics statistics;

    public StatisticResponseService(boolean result, Statistics statistics) {
        this.result = result;
        this.statistics = statistics;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
