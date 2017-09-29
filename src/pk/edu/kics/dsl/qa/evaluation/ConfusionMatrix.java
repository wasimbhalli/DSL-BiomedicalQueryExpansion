package pk.edu.kics.dsl.qa.evaluation;


/**
 * This class implements a confusion matrix between
 * two objects containing list of items 
 * (for example classes in a classification problem for documents).
 * 
 *
 */
public class ConfusionMatrix {
    int tp; // count for true positives
    int tn; // count for true negatives
    int fp; // count for false positives
    int fn; // count for false negatives
    
    public ConfusionMatrix()
    {
        tp=0;fp=0;tn=0;fn=0;
    }
    
    public void increaseTP()
    {
        tp++;
    }
    
    public void increaseTN()
    {
        tn++;
    }
    
    public void increaseFP()
    {
        fp++;
    }
     
    public void increaseFN()
    {
        fn++;
    }

    public int getFn() {
        return fn;
    }

    public int getFp() {
        return fp;
    }

    public int getTn() {
        return tn;
    }

    public int getTp() {
        return tp;
    }
}
