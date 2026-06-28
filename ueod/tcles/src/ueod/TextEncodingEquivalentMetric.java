
package ueod;

import java.io.*;
import java.util.*;

import ueot.*;

public class TextEncodingEquivalentMetric
{
    public final static int LEUCM_DATABASE = 0;
    public final static int DT_MODEL = 1;

    private final ICharSimScore dbReader;
    private final ICharSimScore extReader;

    private int charCrossLocaleEquivalentScore(char a, char b)
    {
        if ((a == 0 || b == 0) && extReader != null)
            return extReader.isEqual(a, b) ? 0 : 1;

        return dbReader.isEqual(a, b) ? 0 : 1;
    }

    private int countIgnorableCount(char[] a, double sigma)
    {
        int count = 0;

        // initialize: cost of ignorable chars
        for (int i = 0; i < a.length; i++)
            if (charCrossLocaleEquivalentScore(a[i], (char)0) < sigma)
                count++;

        return count;
    }

    public TextEncodingEquivalentMetric(int type) throws IOException
    {
        dbReader = new CLERMReader();
        extReader = null;
        // extReader = type == 0 ? null : new DTCharacterScoreReader();
    }

    public int standardEditDistance(char[] a, char[] b)
    {
        int n = a.length;
        int m = b.length;

        int[][] dp = new int[n + 1][m + 1];

        for (int j = 1; j <= m; j++)
            dp[0][j] = j;

        for (int i = 1; i <= n; i++)
            dp[i][0] = i;

        for (int i = 1; i <= n; i++)
        {
            for (int j = 1; j <= m; j++)
            {
                if (a[i - 1] == b[j - 1])
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Util.min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]);
            }
        }

        return dp[n][m];
    }

    public double crossLocaleEquivalentEditDistance(char[] a, char[] b, boolean outdp)
    {
        int n = a.length;
        int m = b.length;

        double[][] dp = new double[n + 1][m + 1];

        // initialize: cost of insert chars
        for (int j = 1; j <= m; j++)
            dp[0][j] = dp[0][j - 1] + charCrossLocaleEquivalentScore(b[j - 1], (char)0);

        // initialize: cost of delete chars
        for (int i = 1; i <= n; i++)
            dp[i][0] = dp[i - 1][0] + charCrossLocaleEquivalentScore(a[i - 1], (char)0);

        for (int i = 1; i <= n; i++)
        {
            for (int j = 1; j <= m; j++)
            {
                char cha = a[i - 1];
                char chb = b[j - 1];

                double deleteCost = dp[i - 1][j] + charCrossLocaleEquivalentScore(cha, (char)0);
                double insertCost = dp[i][j - 1] + charCrossLocaleEquivalentScore(chb, (char)0);
                double replaceCost = dp[i - 1][j - 1] + charCrossLocaleEquivalentScore(cha, chb);

                double minCost = Util.min(deleteCost, insertCost, replaceCost);

                dp[i][j] = minCost;
            }

        }

        if (outdp)
            for (int i = 0; i < dp.length; i++)
                System.out.println(Arrays.toString(dp[i]));

        return dp[n][m];
    }

    public double textCrossLocaleEquivalenceScore(char[] a, char b[])
    {
        double CLEED = crossLocaleEquivalentEditDistance(a, b, false);

        int alen = a.length - countIgnorableCount(a, 0.1);
        int blen = b.length - countIgnorableCount(b, 0.1);

        double metric = 1 - CLEED / Math.max(alen, blen);

        if (metric < 0 || metric > 1)
            System.err.println("ERROR");

        return metric;
    }

    public double textCrossLocaleEquivalenceScore(char[] a, char b[], double CLEED)
    {
        int alen = a.length - countIgnorableCount(a, 0.1);
        int blen = b.length - countIgnorableCount(b, 0.1);

        double metric = 1 - CLEED / Math.max(alen, blen);

        if (metric < 0 || metric > 1)
            System.err.println("ERROR");

        return metric;
    }
}