import java.util.ArrayList;
import java.util.Collections;

public class Sampst {
    
    public  ArrayList<Double> numbers;
    
    public Sampst(){
       numbers = new ArrayList<Double>();
    }
    
    public void add(Double val){
        if(!val.isNaN()){
             numbers.add(val);
        }
    }
    
    public double avg(){
        return (Double) (sum()/numbers.size());
    }
    
    public double min(){
        return Collections.min(numbers, null);
    }
    
    public double max(){
        return Collections.max(numbers, null);
    }
    
    public double sum(){
        double sum = 0;
        for(Double i : numbers){
            sum += i;
        }
        return sum;
    }
    
    /***
     * 
     * @return Population Variance (sigma^2)
     */
    public  double variance()
    {
        double mean = avg();
        double temp = 0;
        for(double a : numbers)
            temp += (a-mean)*(a-mean);
        return temp/numbers.size();
    }

    /***
     * 
     * @return Population Standard Deviation (sigma)
     */
    public double stddev()
    {
        return Math.sqrt(variance());
    }
    
    /***
     * 
     * @return Sample Variance (s^2)
     */
    public double sampleVariance()
    {
        double mean = avg();
        double temp = 0;
        for(double a : numbers){
            temp += (a-mean)*(a-mean);           
        }
            
        return temp/(numbers.size()-1);
    }
    
    /***
     * 
     * @return Sample Standard Deviation (s)
     */
    public double sampleStddev()
    {
        return Math.sqrt(sampleVariance());
    }

    
    public static double tScore(Sampst s1, Sampst s2, double d)
    {
        return (   (s1.avg() - s2.avg() - d) /  Math.sqrt(s1.sampleVariance()/s1.numbers.size() + s2.sampleVariance()/s2.numbers.size()) );
    }
    
    //also found that, dof can be approximated to n1 + n2 - 2
    public static double dof(Sampst samp1, Sampst samp2){
        double s1 = samp1.sampleStddev();
        double s2 = samp2.sampleStddev();
        double n1 = samp1.numbers.size();
        double n2 = samp2.numbers.size();
        
        //s1 = 1.391202739599242; s2= 1.4345874404183399; n1=n2=50;
        
        double numerator = (s1*s1/n1 + s2*s2/n2)*(s1*s1/n1 + s2*s2/n2);
        double denominator = (Math.pow(s1, 4)/(Math.pow(n1, 2)*(n1-1))) + (Math.pow(s2, 4)/(Math.pow(n2, 2)*(n2-1)));
        //System.err.println( "dof "+ numerator/denominator);
        return numerator/denominator;
    }
    
    
    // t-value means: CDF of t(0.90), where
    // 0.90 = 1 - alpha/2
    // one tail = 0.10  (alpha/2) [remember, we used 1-alpha/2]
    // two tail = 0.20 (alpha)
    // alpha = 0.20  (level of significance)
    // 1-alpha = 80% (confidence level)
        
    
    //tried and tested
    public static double tScore(Sampst s1, Sampst s2)
    {
        return (   (s1.avg() - s2.avg()) /  Math.sqrt(s1.sampleVariance()/s1.numbers.size() + s2.sampleVariance()/s2.numbers.size()) );
    }
    
    
    public static boolean rejectNullHypothesis(double tScore, double tCritical){
        if(Math.abs(tScore) > tCritical){
            //reject null hypothesis (mu1 - mu2 >= d) and accept alternative hypothesis (mu1 - mu2 < d)
            return true;
        }else{
            //fail to reject null hypothesis (mu1 - mu2 >= d) and can't accept alternative hypothesis (mu1 - mu2 < d)
            return false;
        }
    }
    //level of significance (1-alpha/2) = 0.005, 0.01, 0.05
    //level of confidence, alpha = 0.95
    //t(alpha = 0.95, n-1 = 49) => 1.68
    //0.005 => 2.678 (2.679951974)
    //0.010 => 2.403 (2.40489176)
    //0.050 => 1.676 (1.676550893)
    
    //accept means mu01 - mu02 = 0, reject means mu01 < mu02  
    
    public static boolean accept005(double tscore){
        if(tscore > 2.679951974){
            return false;
        }else{
            return true;
        }
    }
    
    public static boolean accept01(double tscore){
        if(tscore > 2.40489176){
            return false;
        }else{
            return true;
        }
    }
    
    public static  boolean accept05(double tscore){
        if(tscore > 1.676550893){
            return false;
        }else{
            return true;
        }
    }
    
    
    /**
     * 
     * 
     * ************** TESTS **************
     * 
     * VERIFY THAT T-SCORE WORKS
     * public static void main(String[] args) {
       Double [] arr =  {9.0, 10.0, 11.0, 12.0};
       Double [] arr2 =  {2.0, 4.0, 6.0, 8.0};
        
       Sampst s1 = new Sampst();
        Sampst s2 = new Sampst();
        for (Double d : arr) {
               s1.add(d);
        }
        for (Double d : arr2) {
               s2.add(d);
        }
        System.out.println(Sampst.tScore(s1, s2));
    }
     */
}
