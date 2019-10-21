package classifier.nb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class NaiveBayes {
    ArrayList<String> topics;
    double smoothingFactor;    
    /** populated and calculated while training **/
    HashSet<String> words;
    double[] logPriorProbs;
    int numDocs;
    int numUniqueWords; 
    HashMap<String, Document> classDocs;
    
    public NaiveBayes(double smoothingFactor, ArrayList<String> topics, ArrayList<Document> docs) {
        this.smoothingFactor = smoothingFactor;
        this.topics = topics;
        this.numDocs = docs.size();
        this.logPriorProbs = new double[topics.size()];
        this.words = new HashSet<>();
                
        /* build 1 document for each topic        */
        this.classDocs = new HashMap<>();
        for(String topic: topics){
            classDocs.put(topic, new Document(new HashMap<String, Integer>(), topic));
        }
            
        /* merge documents of same class, also count & store P(C1), P(C2)...   */
        for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
            Document doc = iterator.next();            
            //count
            logPriorProbs[topics.indexOf((String)doc.topicName)] += 1;
            
            Document classDoc = classDocs.get((String)doc.topicName);
            classDoc.merge(doc);
        }
        
        /* calculate and store total word count */
        for(String topic : classDocs.keySet()){
            //System.out.println(topic);
            Document topicDoc = classDocs.get((String)topic);
            
            
            for (Entry<String, Integer> entry : topicDoc.wordMap.entrySet()){
                String word = entry.getKey();
                words.add(word);    //add unique words of this class (topic)
                
                Integer wordCount = entry.getValue();
                topicDoc.totalWordCount += wordCount; // count all words of this class (topic) [multiple times result in multiple counts]
            }            
            
            //System.out.println(topicDoc.wordMap);
            //System.out.println("Word Count: "+ topicDoc.totalWordCount);
            //System.out.println();
        }
        numUniqueWords = words.size();
        
        /* calculate and store log P(C1), log P(C2)...   */
        //double tot = 0;
        for (int i = 0; i < logPriorProbs.length; i++) {            
            double logPriorProb = logPriorProbs[i];
            if(logPriorProb != 0){                
                logPriorProb = Math.log((logPriorProb*1.0)/(numDocs*1.0));
                logPriorProbs[i] = logPriorProb;
                //tot+= logPriorProb;
            }
            //System.out.println(logPriorProb + " " + tot);
        }
        /*
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.print(dateFormat.format(new Date()) + " ");
        System.out.println("Training completed.");        
        System.out.println("Number of docs: "+ numDocs);
        */
    }
    
    
    /***
     * 
     * @param testDocs List of documents to test.
     * @return Accuracy of the test set.
     */
    public double test(ArrayList<Document> testDocs){
        //start testing
        int correct = 0;
        int wrong = 0;

        for (Document testDoc : testDocs) {
            if (test(testDoc) == testDoc.topicName) {
                correct++;
            } else {
                wrong++;
            }
        }
        double accuracy = (correct * 1.0) / (correct + wrong) * 100;
        return accuracy;
    }
    
    /***
     * 
     * @param testDoc Single document to classify.
     * @return Name (String) of the class.
     */
    public String test(Document testDoc) {
        String chosenTopic = null;
        double maxProb = Double.NEGATIVE_INFINITY;
        
        for(String topic: topics){
            //get logCondProbs
            double logCondProb = 0.0;
            for(String w : testDoc.wordMap.keySet()){
                //n(w1)(c1), the count of this word w, in class(topic) topic.
                Document topicDoc = this.classDocs.get((String)topic);
                int numWordCount = 0;
                try {                    
                    numWordCount = topicDoc.wordMap.get((String)w);
                } catch (Exception e) {
                   // e.printStackTrace();
                }
                
                //calculate logCondProb
                double logP_W_given_C = Math.log((numWordCount + 1.0*smoothingFactor)/(topicDoc.totalWordCount + numUniqueWords*smoothingFactor*1.0));
                logCondProb += logP_W_given_C;

            }
            logCondProb += this.logPriorProbs[topics.indexOf((String)topic)];
            //System.out.println("logCondProb: "+ logCondProb);
            if(logCondProb > maxProb){
                chosenTopic = topic;
                maxProb = logCondProb;
            }
        }
        //System.out.println("Chosen: "+ chosenTopic + ", Actual: " + testDoc.topicName);
        return chosenTopic;
    }
    
    
    
    
     public static double findBestSmoothingFactor(ArrayList<Document> train, ArrayList<Document> test, ArrayList<String> topics ){
        int N = 50;
        double[] alphas = new double[N];
        alphas[0] = 1;
        double sf = 1;
        for (int i = 1; i < N; i++) {
            if (i >= 1 && i < 10) {
                sf = sf - 0.05;
            }
            if (i >= 10 && i < 20) {
                sf = sf - 0.01;

            }
            if (i >= 20 && i < 30) {
                sf = sf - 0.005;

            }
            if (i >= 30 && i < 50) {
                sf = sf - 0.001;
            }
            alphas[i] = sf;
        }
        
        double maxAcc = 0.0;
        double maxSF = -1;
        for (int i = 1; i < N; i++) {
          NaiveBayes NB = new NaiveBayes(alphas[i], topics, train);
          double currAccuracy = NB.test(test);
          if(currAccuracy > maxAcc){
              maxAcc = currAccuracy;
              maxSF = alphas[i];
          }
           System.out.println("SF: "  + alphas[i] + "\t Acc: " + currAccuracy);
        }
         System.out.println("Best SF: "  + maxSF + "\t Best Acc: " + maxAcc);
        
        return maxSF;

    }
}
