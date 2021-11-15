package com.egortroyan.searchengine.models;

import com.egortroyan.searchengine.morphology.MorphologyAnalyzer;

import java.io.IOException;
import java.util.*;


public class Request {

    private String req;
    private List<String> reqLemmas;

    public List<String> getReqLemmas() {
        return reqLemmas;
    }

    public Request(String req){
        this.req = req;
        reqLemmas = new ArrayList<>();
        try {
            MorphologyAnalyzer analyzer = new MorphologyAnalyzer();
            TreeMap<String, Integer> map = analyzer.textAnalyzer(req);
            for (Map.Entry<String, Integer> o : map.entrySet()) {
                reqLemmas.add(o.getKey());
            }
        }catch (Exception e) {
            System.out.println("ошибка морфологочиского анализа");
        }


    }

//   private void makeSortedLemmaMap (String request) throws IOException {
//       MorphologyAnalyzer analyzer = new MorphologyAnalyzer();
//       TreeMap<String, Integer> map = analyzer.textAnalyzer(request);
//       ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
//       list.sort((o1, o2) -> o1.getValue() - o2.getValue());
//       for (Map.Entry<String, Integer> o : list) {
//           reqByLemma.put(o.getKey(), o.getValue());
//       }
//   }
}
