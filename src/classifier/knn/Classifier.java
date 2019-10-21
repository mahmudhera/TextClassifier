package classifier.knn;

import java.util.ArrayList;

/**
 *
 * @author Hera
 */
public abstract class Classifier {

    public ArrayList trainingList;
    public ArrayList testList;
    
    public Classifier(ArrayList trainingSet, ArrayList testSet) {
        this.testList = testSet;
        this.trainingList = trainingSet;
    }
    
    public abstract void train();
    
    public abstract double test();
    
}
