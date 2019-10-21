package classifier.knn;

import java.util.ArrayList;

/**
 *
 * @author Hera
 */
public class Main {
    
    public static void main(String[] args) {
        
        TopicFileParser topicReader = new TopicFileParser("topics.txt");
        ArrayList<String> topics = (ArrayList<String>)topicReader.getDataList();
        
        FileParser irreleventReader = new TopicFileParser("irrelevent.txt");
        ArrayList<String> irreleventWords = (ArrayList<String>)irreleventReader.getDataList();
        
        ArrayList<Document> trainigSet = new ArrayList<>();
        ArrayList<Document> testSet = new ArrayList<>();
        
        for (String topic : topics) {
            System.out.println(topic);
            FileParser trainingFileReader = new XMLParser("./Training/"+topic+".xml", topic, 500);
            FileParser testFileReader = new XMLParser("./Test/"+topic+".xml", topic, 100);
            trainigSet.addAll(trainingFileReader.getDataList());
            testSet.addAll(testFileReader.getDataList());
        }
        
        for (Document d : trainigSet) {
            d.removeWords(irreleventWords);
        }
        
        for (Document d : testSet) {
            d.removeWords(irreleventWords);
        }
        
        Classifier c = new KNNTextClassifier(trainigSet, testSet, 7, KNNTextClassifier.HAMMING);
        c.train();
        c.test();
        
    }
    
}
