package classifier.nb;

import java.util.HashMap;

public class Document {

    public String topicName;
    public HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
    public int totalWordCount = 0;

    public Document(HashMap<String, Integer> wordMap, String topicName) {
        this.wordMap = wordMap;
        this.topicName = topicName;
    }

    public Document mergeNew(Document d2) {
        HashMap<String, Integer> mapNew = new HashMap<String, Integer> ();
                
        for (String w : d2.wordMap.keySet()) {
            Integer i = mapNew.get(w);
            if (i == null) {
                mapNew.put(w, 1);
            } else {
                mapNew.put(w, i + 1);
            }
        }
        
        for (String w : wordMap.keySet()) {
            Integer i = mapNew.get(w);
            if (i == null) {
                mapNew.put(w, 1);
            } else {
                mapNew.put(w, i + 1);
            }
        }
        return new Document(mapNew, topicName);
    }
    
    public void merge(Document d2) {
        for (String w : d2.wordMap.keySet()) {
            Integer i = wordMap.get(w);
            if (i == null) {
                wordMap.put(w, 1);
            } else {
                wordMap.put(w, i + 1);
            }
        }
    }

}
