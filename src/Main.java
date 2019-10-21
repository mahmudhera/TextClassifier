import classifier.nb.Document;
import classifier.nb.NaiveBayes;
import classifier.knn.KNNTextClassifier;
import JSci.maths.statistics.TDistribution;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class Main {

    /* Test Values */
    static int NUM_TRAINING_ROW = 5000;
    static int ROW_EACH_ITER = 100;
    static int NUM_ITERATIONS = 50;
    static int NUM_TEST_DOCS = 50;
    static double D = 4;

    /* Best Values */
    static double BEST_SF = 0.31600000000000017;
    static int BEST_K = 47;

    /* File Paths */
    static String blacklistFile = "blacklist.txt";
    static String topicFileName = "./Data/topics.txt";
    static String trainFilePath = "./Data/Training/";
    static String testFilePath = "./Data/Test/";

    public static ArrayList<classifier.knn.Document> docConverter(ArrayList<Document> docsNaiveBayes) {
        ArrayList<classifier.knn.Document> docsKNN = new ArrayList<>();
        for (Document d : docsNaiveBayes) {
            classifier.knn.Document knnDoc = new classifier.knn.Document(d.topicName);
            for (Entry e : d.wordMap.entrySet()) {
                knnDoc.addWord((String) e.getKey(), (Integer) e.getValue());
            }
            docsKNN.add(knnDoc);
        }
        return docsKNN;
    }
    ///*
    public static void main(String[] args) {

        ArrayList<String> topics = new ArrayList<>();
        ArrayList<Document> trainDocs = new ArrayList<>();
        ArrayList<Document> testDocs = new ArrayList<>();
        Set<String> blacklist = new HashSet<>();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.print(dateFormat.format(new Date()) + " ");
        System.out.println("File parsing in progress ... please wait.");

        new BlacklistRead(blacklistFile, blacklist).processFile();
        new TopicRead(topicFileName, topics).processFile();

        for (String topic : topics) {
            System.out.println(topic);
            new XMLRead(trainFilePath.concat(topic).concat(".xml"), trainDocs, blacklist, topic).processFile(NUM_TRAINING_ROW + 2);
            new XMLRead(testFilePath.concat(topic).concat(".xml"), testDocs, blacklist, topic).processFile(NUM_TEST_DOCS);
        }

        /**** NB Output ****
        double bestSmoothingFactor = NaiveBayes.findBestSmoothingFactor(trainDocs, testDocs, topics);
        ********************/

        /**** KNN Output ****
        int[] ks = {1, 3, 5};
        for (int k : ks) {
            KNNTextClassifier KNN1 = new KNNTextClassifier(docConverter(trainDocs), docConverter(testDocs), k, KNNTextClassifier.EUCLEDIAN);
            KNNTextClassifier KNN2 = new KNNTextClassifier(docConverter(trainDocs), docConverter(testDocs), k, KNNTextClassifier.HAMMING);
            KNNTextClassifier KNN3 = new KNNTextClassifier(docConverter(trainDocs), docConverter(testDocs), k, KNNTextClassifier.COSINE);
            KNN1.train();
            KNN2.train();
            KNN3.train();

            System.out.printf("k=%d,\t Euclidean:\t %.4f\n", k, KNN1.test());
            System.out.printf("k=%d,\t Hamming:\t %.4f\n", k, KNN2.test());
            System.out.printf("k=%d,\t Cosine:\t %.4f\n", k, KNN3.test());
        }
        *********************/

        ///*
        Sampst sampstNB = new Sampst();
        Sampst sampstKNN = new Sampst();

        for (int i = 0; i < NUM_ITERATIONS; i++) {

            ArrayList<Document> trainDocsSubset = new ArrayList<>();
            for (int j = i * ROW_EACH_ITER; j < i * ROW_EACH_ITER + ROW_EACH_ITER; j++) {
                for (int k = 0; k < topics.size(); k++) {
                    trainDocsSubset.add(trainDocs.get(k * NUM_TRAINING_ROW + j));
                }
            }

            NaiveBayes NB = new NaiveBayes(BEST_SF, topics, trainDocsSubset);
            KNNTextClassifier KNN = new KNNTextClassifier(docConverter(trainDocsSubset), docConverter(testDocs), BEST_K, KNNTextClassifier.COSINE);
            KNN.train();

            double accKNN = KNN.test();
            double accNB = NB.test(testDocs);
            sampstKNN.add(accKNN);
            sampstNB.add(accNB);
            System.out.printf("KNN: %.4f%s \t NB: %.4f%s\n", accKNN, "%", accNB, "%");
        }

        System.out.println("");
        System.out.println("KNN Avg: "+ sampstKNN.avg() + " \t NB Avg: " + sampstNB.avg());
        System.out.println("KNN Var: "+ sampstKNN.sampleVariance() + " \t NB Var: " + sampstNB.sampleVariance());

        double tScore = Sampst.tScore(sampstNB, sampstKNN, D);
        System.out.println("\nT-score " + Math.abs(tScore));

        TDistribution tdist = new TDistribution((int) Math.round(Sampst.dof(sampstNB, sampstKNN)));
        System.out.println("DOF: " + tdist.getDegreesOfFreedom());

        double levelSignificance[] = {0.005, 0.01, 0.05};
        for (double alpha : levelSignificance) {
            double tCritical  = tdist.inverse(1-alpha);
            System.out.println("LEVEL OF SIGNIFICANCE (alpha): " + alpha + ", tCritical="+tCritical);

            if(Sampst.rejectNullHypothesis(tScore, tCritical)){
                System.out.printf("Diﬀerence between the overall mean of NB and KNN is less than %.4f%s\n", D, "%");
            }else{
                System.out.printf("We can't reject that diﬀerence between the overall mean of NB and KNN is more than %.4f%s\n", D, "%");
            }
            System.out.println("");
        }
        //*/
    }
    //*/
}
