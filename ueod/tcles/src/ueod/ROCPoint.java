
package ueod;

public class ROCPoint
{
    public double threshold;
    public double tpr;
    public double fpr;
    public double accuracy;

    public ROCPoint(double threshold, double tpr, double fpr, double accuracy)
    {
        this.threshold = threshold;
        this.tpr = tpr;
        this.fpr = fpr;
        this.accuracy = accuracy;
    }
}