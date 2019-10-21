package classifier.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Hera
 */
public class KNNTextClassifier extends Classifier {

    int k;
    ArrayList<String> topics = new ArrayList<String>();

    public static int EUCLEDIAN = 0;
    public static int HAMMING = 1;
    public static int COSINE = 2;

    int choiceOfDistanceHeuristic;

    public KNNTextClassifier(ArrayList<Document> trainingList, ArrayList<Document> testList, int k, int distanceChoice) {
        super(trainingList, testList);
        this.k = k;

        HashSet<String> set = new HashSet<String>();
        for (Document d : trainingList) {
            set.add(d.topic);
        }

        for (String s : set) {
            topics.add(s);
        }

        this.choiceOfDistanceHeuristic = distanceChoice;

    }

    @Override
    public void train() {
        // nothing to do for k-NN
    }

    @Override
    public double test() {

        int success = 0, failure = 0;
        ArrayList<Document> allDocs = new ArrayList<Document>();
        allDocs.addAll(this.trainingList);

        for (Object obj : testList) {

            Document d = (Document) obj;
            Document.addDocToMap(d);
            allDocs.add(d);

            ArrayList<DocDistanceTuple> listToSort = new ArrayList<DocDistanceTuple>();
            for (Object obj1 : trainingList) {
                Document d1 = (Document) obj1;
                if (this.choiceOfDistanceHeuristic == KNNTextClassifier.COSINE) {
                    listToSort.add(new DocDistanceTuple(d1, (int) (10000.0 * d1.cosineDistance(d, allDocs))));
                } else if (this.choiceOfDistanceHeuristic == KNNTextClassifier.EUCLEDIAN) {
                    listToSort.add(new DocDistanceTuple(d1, (int) (d1.euclideanDistance(d))));
                } else if (this.choiceOfDistanceHeuristic == KNNTextClassifier.HAMMING) {
                    listToSort.add(new DocDistanceTuple(d1, (int) (d1.hammingDistance(d))));
                }
            }

            Collections.sort(listToSort);

            int[] count = new int[this.topics.size()];
            for (int i = 0; i < k; i++) {
                Document nn = listToSort.get(i).d;
                for (int j = 0; j < this.topics.size(); j++) {
                    if (this.topics.get(j).equals(nn.topic)) {
                        count[j]++;
                        break;
                    }
                }
            }

            int maxCount = -1, index = -1;
            for (int j = 0; j < count.length; j++) {
                if (count[j] > maxCount) {
                    maxCount = count[j];
                    index = j;
                }
            }

            String classifiedTopic = this.topics.get(index);

            //System.out.println("Actual: " + d.topic + " Classified: " + classifiedTopic);
            if (d.topic.equals(classifiedTopic)) {
                //System.out.println("Corrrectly classified");
                success++;
            } else {
                //System.out.println("Inorrrectly classified");
                failure++;
            }

            Document.removeDocFromMap(d);
            allDocs.remove(d);

        }

        //System.out.println("Success: " + success + " Failure: " + failure);
        return 100.0 * success / (success + failure);
    }

}

class DocDistanceTuple implements Comparable {

    Document d;
    int distance;

    DocDistanceTuple(Document d, int distance) {
        this.d = d;
        this.distance = distance;
    }

    @Override
    public int compareTo(Object o) {
        DocDistanceTuple ddt = (DocDistanceTuple) o;
        if (this.distance == ddt.distance) {
            return 0;
        } else {
            return (int) ((this.distance - ddt.distance));
        }
    }

}
//54,55,101,102
