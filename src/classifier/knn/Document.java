package classifier.knn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Hera
 */
public class Document {
    
    HashMap wordList = null;
    String topic;
    static HashMap wordToDocCount = new HashMap();
    
    public Document(String topic) {
        wordList = new HashMap();
        this.topic = topic;
    }
    
    public  void addWord(String word, int frequency) {
        wordList.put(word, frequency);
    }
    
    int getFrequency(String word) {
        return (int)wordList.get(word);
    }
    
    static void removeDocFromMap(Document d) {
        for (Object o : d.wordList.keySet()) {
            String word = (String)o;
            
            Integer count = (Integer)Document.wordToDocCount.get(word);
            if (count == null)
                continue;
            Document.wordToDocCount.remove(word);
            if (count > 1)
                Document.wordToDocCount.put(word, (int)count - 1);
        }
    }
    
    static void addDocToMap(Document d) {
        for (Object o : d.wordList.keySet()) {
            String word = (String)o;
            Integer count = (Integer)Document.wordToDocCount.get(word);
            if (count == null)
                continue;
            Document.wordToDocCount.remove(word);
            Document.wordToDocCount.put(word, (int)count + 1);
        }
    }
    
    void increaseFrequency(String word) {
        word = word.trim();
        if (word.length() == 0)
            return;
        Integer count = (Integer)wordList.get(word);
        if (count == null)
            count = 0;
        count++;
        
        wordList.remove(word);
        wordList.put(word, count);
    }
    
    void removeWords(ArrayList<String> words) {
        for (String word : words) {
            this.wordList.remove(word);
        }
    }
    
    void print() {
        System.out.println("Topic: " + this.topic);
        for (Object o : wordList.keySet()) {
            String word = (String)o;
            System.out.println("Word: " + word + " Freq: " + wordList.get(word));
        }
    }
    
    int hammingDistance(Document d) {
        int distance = 0;
        for (Object o : d.wordList.keySet()) {
            String str = (String)o;
            if (!this.wordList.containsKey(str)) {
                distance++;
            }
        }
        for (Object o : this.wordList.keySet()) {
            String str = (String)o;
            if (!d.wordList.containsKey(str)) {
                distance++;
            }
        }
        return distance;
    }
    
    int euclideanDistance(Document d) {
        int distance = 0;
        HashSet<String> set = new HashSet<String>();
        for (Object obj : d.wordList.keySet())
            set.add((String)obj);
        for (Object obj : this.wordList.keySet())
            set.add((String)obj);
        for (Object obj : set) {
            int value1, value2;
            Object o1 = d.wordList.get((String)obj);
            if (o1 != null) {
                value1 = (Integer)o1;
            } else {
                value1 = 0;
            }
            Object o2 = this.wordList.get((String)obj);
            if (o2 != null) {
                value2 = (Integer)o2;
            } else {
                value2 = 0;
            }
            distance += (value1 - value2) * (value1 - value2);
        }
        return distance;
    }
    
    double cosineDistance(Document d, ArrayList<Document> allDocuments) {
        return 1 - (double)this.dotProduct(d, allDocuments) / (double)(this.vectorLength(allDocuments) * d.vectorLength(allDocuments));
    }
    
    private double dotProduct(Document d, ArrayList<Document> allDocuments) {
        double dp = 0;
        HashSet<String> set = new HashSet<String>();
        for (Object obj : d.wordList.keySet())
            set.add((String)obj);
        for (Object obj : this.wordList.keySet())
            set.add((String)obj);
        for (Object obj : set) {
            String str = (String)obj;
            Object o1 = d.wordList.get(str);
            Object o2 = this.wordList.get(str);
            if (o1 != null && o2 != null) {
                double idf = Math.log(1.0*allDocuments.size()/countDocsContainingWord(allDocuments, str));
                dp += idf * (Integer)(o1) * (Integer)(o2) * idf;
            }
        }
        return dp;
    }
    
    public static int countDocsContainingWord(ArrayList<Document> allDocs, String word) {
        Integer retValue = (Integer)Document.wordToDocCount.get(word);
        if (retValue != null)
            return retValue;
        int count = 0;
        for (Document d : allDocs) {
            if (d.wordList.containsKey(word)) {
                count ++;
            }
        }
        Document.wordToDocCount.put(word, count);
        return count;
    }
    
    private double vectorLength(ArrayList<Document> allDocuments) {
        double total = 0;
        for (Object o : this.wordList.keySet()) {
            String str = (String)o;
            double idf = Math.log(1.0*allDocuments.size()/countDocsContainingWord(allDocuments, str));
            int val = (Integer)this.wordList.get(str);
            total += val * val * idf * idf ;
        }
        return Math.sqrt(total);
    }
    
    public static void main(String[] args) {
        Document d = new Document("Test");
        d.increaseFrequency("i");
        d.increaseFrequency("i");
        d.increaseFrequency("i");
        d.increaseFrequency("am");
        d.increaseFrequency("a");
        d.increaseFrequency("boy");
        d.print();
    }
    
}
