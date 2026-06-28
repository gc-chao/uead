
package ueoq;

import java.text.*;
import java.util.*;

import ueot.*;

public class TextObfuscationMetric
{
    public static interface SystemDiscriminator<E>
    {
        int getSystem(E e);

        String getSystemName(int k);

        int compositeCount(E e);
    }

    public static <E> HashMap<Integer, Integer> countSystems(E[] elements, SystemDiscriminator<E> discriminator)
    {
        // store: O(n)
        HashMap<Integer, Integer> stats = new HashMap<Integer, Integer>();

        for (int i = 0; i < elements.length; i++)
        {
            Integer system = discriminator.getSystem(elements[i]);

            if (!stats.containsKey(system))
                stats.put(system, 1);
            else
                stats.put(system, stats.get(system) + 1);
        }

        return stats;
    }

    public static <E> double usingRatio(int k, HashMap<Integer, Integer> stats)
    {
        return (stats.size() - 1) / (double)(k - 1);
    }

    public static <E> double simpsonDiversity(int k, int n, HashMap<Integer, Integer> stats)
    {
        double hhi = 0;

        Set<Integer> systems = stats.keySet();

        for (Integer system : systems)
        {
            int ci = stats.get(system);
            double pi = ci / (double)n;

            hhi += pi * pi;
        }

        double simpson = 1 - hhi;
        double simpsonNorm = simpson / (1 - 1.0 / k);

        return simpsonNorm;
    }

    public static <E> double shannonEntropy(int k, int n, HashMap<Integer, Integer> stats)
    {
        if (k == 1)
            return 0;

        double entropy = 0;

        Set<Integer> systems = stats.keySet();

        for (Integer system : systems)
        {
            int ci = stats.get(system);
            double pi = ci / (double)n;

            entropy -= pi * Util.log2n(pi);
        }

        double norm = entropy / Util.log2n(k);

        return norm;
    }

    public static <E> double switchRatio(E[] elements, SystemDiscriminator<E> discr)
    {
        int r = 0;
        int n = elements.length;

        if (n == 1)
            return 0;

        for (int i = 1; i < n; i++)
        {
            E cl = elements[i];
            E cr = elements[i - 1];

            if (discr.getSystem(cl) != discr.getSystem(cr))
                r++;
        }

        return r / (double)(n - 1);
    }

    public static <E> double conditionalEntropy(int k, E[] elements, SystemDiscriminator<E> discr, boolean printLog)
    {
        int total = 0;
        int n = elements.length;
        int[][] counter = new int[k][k];

        // all system
        HashSet<Integer> systems = new HashSet<Integer>();

        for (int i = 0; i < n - 1; i++)
        {
            E cl = elements[i];
            E cr = elements[i + 1];

            int sl = discr.getSystem(cl);
            int sr = discr.getSystem(cr);

            total++;
            counter[sl][sr] += 1;

            systems.add(sl);
            systems.add(sr);
        }

        if (systems.size() == 1)
            return 0;

        // print log
        if (printLog)
        {
            System.err.println();

            for (int j = 0; j < k; j++)
                System.err.print("\t" + discr.getSystemName(j));

            System.err.println();

            for (int i = 0; i < k; i++)
            {
                System.err.print(discr.getSystemName(i) + "\t");

                for (int j = 0; j < k; j++)
                    System.err.print(counter[i][j] + "\t");

                System.err.println();
            }
        }

        double entropy = 0;

        for (int i = 0; i < k; i++)
        {
            int ci = 0;

            for (int j = 0; j < k; j++)
                ci += counter[i][j];

            if (ci == 0)
                continue;

            for (int j = 0; j < k; j++)
            {
                double pij = counter[i][j] / (double)ci;
                double gij = counter[i][j] / (double)total;

                if (gij == 0)
                    continue;

                entropy -= gij * Util.log2n(pij);

                if (printLog)
                {
                    DecimalFormat df = new DecimalFormat("0.0000");

                    System.err.print("p(" + discr.getSystemName(j) + "|" + discr.getSystemName(i) + ")=" + counter[i][j] + "/" + ci + "=" + df.format(pij));
                    System.err.print("\t");
                    System.err.print("p(" + discr.getSystemName(i) + "," + discr.getSystemName(j) + ")=" + counter[i][j] + "/" + total + "=" + df.format(gij));
                    System.err.print("\t");
                    System.err.print(df.format(gij * Util.log2n(pij)));
                    System.err.println();
                }
            }
        }

        boolean marginalNorm = false;

        if (!marginalNorm)
            return entropy / Util.log2n(systems.size());
        else
        {
            int ct = 0;
            int[] cs = new int[k];

            for (int i = 1; i < n; i++)
            {
                E c = elements[i];
                int system = discr.getSystem(c);

                ct++;
                cs[system]++;
            }

            double marginalEntropy = 0;

            for (int j = 0; j < k; j++)
            {
                double pj = cs[j] / (double)ct;

                if (pj == 0)
                    continue;

                marginalEntropy -= pj * Util.log2n(pj);

                if (printLog)
                {
                    DecimalFormat df = new DecimalFormat("0.00");

                    System.err.print("p(" + discr.getSystemName(j) + ")=" + cs[j] + "/" + ct + "=" + df.format(pj));
                    System.err.print("\t");
                }
            }

            if (printLog)
                System.err.println("\n" + entropy + "/" + marginalEntropy + "=" + entropy / marginalEntropy);

            double norm = entropy / marginalEntropy;
            
            return norm;
        }
    }

    public static <E> double windowEntropy(int k, E[] elements, SystemDiscriminator<E> discr, int l, int s)
    {
        boolean normAll = false;

        int n = elements.length;
        int m = (n - l) / s + 1;

        double entropy = 0;

        for (int i = 0; i < m; i++)
        {
            E[] subArray = Arrays.copyOfRange(elements, i * s, i * s + l);

            HashMap<Integer, Integer> stats = countSystems(subArray, discr);

            if (stats.size() > 1)
                entropy += shannonEntropy(normAll ? k : stats.size(), subArray.length, stats);
        }

        return entropy / (double)m;
    }

    public static double compositeAbnormity‌(int count)
    {
        return compositeAbnormity‌(count, 0.02f, 3.0f, 2.0f);
    }

    public static double compositeAbnormity‌(float count, float slope, float scale, float kick)
    {
        return 1 - Math.exp(-slope * count * Math.exp(scale * (count - kick) / kick));
    }

    public static <E> double compositeAbnormity(E[] elements, SystemDiscriminator<E> trans)
    {
        double sum = 0;
        int len = elements.length;

        for (int i = 0; i < len; i++)
        {
            int compositeCount = trans.compositeCount(elements[i]);

            sum += compositeAbnormity‌(compositeCount);
        }

        return sum / (double)len;
    }

    public static <E> double[] getIndexes(int k, E[] elements, SystemDiscriminator<E> discr)
    {
        return getIndexes(k, elements, discr, 0.4, 0.4, 0.2);
    }

    public static <E> double[] getIndexes(int k, E[] elements, SystemDiscriminator<E> discr, double alpha1, double alpha2, double alpha3)
    {
        HashMap<Integer, Integer> stats = TextObfuscationMetric.countSystems(elements, discr);

        double GEO_R = usingRatio(k, stats);
        double GEO_S = simpsonDiversity(k, elements.length, stats);
        double GEO_H = shannonEntropy(k, elements.length, stats);

        double LEO_R = switchRatio(elements, discr);
        double LEO_H = conditionalEntropy(k, elements, discr, false);
        double LEO_W = windowEntropy(k, elements, discr, 3, 1);

        double CEO = compositeAbnormity(elements, discr);

        double RR = GEO_R * alpha1 + LEO_R * alpha2 + CEO * alpha3;
        double SR = GEO_S * alpha1 + LEO_R * alpha2 + CEO * alpha3;
        double HR = GEO_H * alpha1 + LEO_R * alpha2 + CEO * alpha3;
        double RH = GEO_R * alpha1 + LEO_H * alpha2 + CEO * alpha3;
        double SH = GEO_S * alpha1 + LEO_H * alpha2 + CEO * alpha3;
        double HH = GEO_H * alpha1 + LEO_H * alpha2 + CEO * alpha3;
        double RW = GEO_R * alpha1 + LEO_W * alpha2 + CEO * alpha3;
        double SW = GEO_S * alpha1 + LEO_W * alpha2 + CEO * alpha3;
        double HW = GEO_H * alpha1 + LEO_W * alpha2 + CEO * alpha3;

        return new double[] { GEO_R, GEO_S, GEO_H, LEO_R, LEO_H, LEO_W, CEO, RR, SR, HR, RH, SH, HH, RW, SW, HW };
    }
}