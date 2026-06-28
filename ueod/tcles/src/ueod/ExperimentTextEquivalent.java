
package ueod;

import java.io.*;

import java.text.*;

public class ExperimentTextEquivalent
{
    public static void main(String[] args) throws IOException
    {
        testGroup1();
    }

    private static void testGroup1() throws IOException
    {
        System.out.println("TextA\tTextB\tStdED\tCLEED\tTCLES");

        testOne("compare", "which");
        testOne("compare", "export");
        testOne("compare", "computer");
        testOne("compare", "coᵐputer");
        testOne("compare", "ComparE");
        testOne("compare", "coᵐpÀrᴱ਺");
        testOne("compare", "਺਺c਺਺oᵐ਺਺਺pÀrᴱ");
    }

    private static void testOne(String a, String b) throws IOException
    {
        TextEncodingEquivalentMetric metric = new TextEncodingEquivalentMetric(TextEncodingEquivalentMetric.LEUCM_DATABASE);

        char[] chA = a.toCharArray();
        char[] chB = b.toCharArray();

        int STDED = metric.standardEditDistance(chA, chB);
        double CLEED = metric.crossLocaleEquivalentEditDistance(chA, chB, false);
        double TCLES = metric.textCrossLocaleEquivalenceScore(chA, chB);

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.print(a + " \t " + b + " \t");
        System.out.print(STDED + "\t" + CLEED + "\t" + df.format(TCLES));
        System.out.println();
    }
}