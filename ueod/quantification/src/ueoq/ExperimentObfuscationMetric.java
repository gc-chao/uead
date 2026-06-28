
package ueoq;

import java.io.*;
import java.text.*;
import java.util.*;

import ueot.*;

public class ExperimentObfuscationMetric
{
    private static TextObfuscationMetricEx textObfuscationMetric;
    private static CommandObfuscationMetric commandObfuscationMetric;

    private final static String TEXT_HEAD = "\nstring sample\tGEO_R\tGEO_S\tGEO_H|\tLEO_R\tLEO_H\tLEO_W|\tCEO|\tRR\tSR\tHR\tRH\tSH\tHH\tRW\tSW\tHW";
    private final static String SAMPLE_TEXT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public static void main(String[] args) throws IOException
    {
        textObfuscationMetric = new TextObfuscationMetricEx(10);
        commandObfuscationMetric = new CommandObfuscationMetric(10);

        testText();
        testCommand("reg [save] [hklm]\\sam C:\\1.hiv");
    }

    private static void testText() throws IOException
    {
        System.out.println(TEXT_HEAD);

        ExperimentObfuscator obfuscator = new ExperimentObfuscator();
        List<Pair<String, String>> dataset = obfuscator.createObfuscateTextSamples(SAMPLE_TEXT, 1, false);

        for (int i = 0; i < dataset.size(); i++)
        {
            Pair<String, String> obfuscatedText = dataset.get(i);
            String tag = "L=" + SAMPLE_TEXT.length() + "," + obfuscatedText.getKey();

            printTextObfuscation(tag, obfuscatedText.getValue(), false);
        }
    }

    private static void testCommand(String command) throws IOException
    {
        ExperimentObfuscator obfuscator = new ExperimentObfuscator();

        List<Pair<String, String>> dataset = obfuscator.createObfuscateCommandSamples(command, 1, false);

        for (int i = 0; i < dataset.size(); i++)
        {
            Pair<String, String> obfuscatedCommand = dataset.get(i);
            String tag = obfuscatedCommand.getKey();
            String cmd = obfuscatedCommand.getValue();
            String arg = Arrays.toString(commandObfuscationMetric.parseCommand(cmd));

            System.out.println("\n" + tag + " " + arg);
            System.out.println(cmd);
            System.out.println("ITEM\tRR\tSR\tHR\tRH\tSH\tHH\tRW\tSW\tHW");

            printCommandObfuscation(cmd);

            System.out.println();
        }
    }

    private static <T> void printTextObfuscation(String tag, String text, boolean isLatin)
    {
        if (tag == null)
            System.out.print(text);
        else
            System.out.print(tag);

        double[] indexes = textObfuscationMetric.getIndexes(text, isLatin);

        double GEO_R = indexes[0];
        double GEO_S = indexes[1];
        double GEO_H = indexes[2];
        double LEO_R = indexes[3];
        double LEO_H = indexes[4];
        double LEO_W = indexes[5];
        double CEO   = indexes[6];

        DecimalFormat df = new DecimalFormat("0.00");

        System.out.print("\t");
        System.out.print(df.format(GEO_R));
        System.out.print("\t");
        System.out.print(df.format(GEO_S));
        System.out.print("\t");
        System.out.print(df.format(GEO_H));
        System.out.print("|");

        System.out.print("\t");
        System.out.print(df.format(LEO_R));
        System.out.print("\t");
        System.out.print(df.format(LEO_H));
        System.out.print("\t");
        System.out.print(df.format(LEO_W));
        System.out.print("|");

        System.out.print("\t");
        System.out.print(df.format(CEO));
        System.out.print("|");

        System.out.print("\t");
        System.out.print(df.format(indexes[7]));
        System.out.print("\t");
        System.out.print(df.format(indexes[8]));
        System.out.print("\t");
        System.out.print(df.format(indexes[9]));
        System.out.print("\t");
        System.out.print(df.format(indexes[10]));
        System.out.print("\t");
        System.out.print(df.format(indexes[11]));
        System.out.print("\t");
        System.out.print(df.format(indexes[12]));
        System.out.print("\t");
        System.out.print(df.format(indexes[13]));
        System.out.print("\t");
        System.out.print(df.format(indexes[14]));
        System.out.print("\t");
        System.out.print(df.format(indexes[15]));

        System.out.println();
    }

    public static void printCommandObfuscation(String command)
    {
        String[] arguments = commandObfuscationMetric.parseCommand(command);

        for (int i = 0; i < arguments.length; i++)
        {
            System.out.print("ARG" + i + "\t");

            double[] argIndexes = commandObfuscationMetric.getArgumentIndexes(arguments[i]);

            printDoubleArray(Arrays.copyOfRange(argIndexes, 7, 16));
        }

        double[][] cmdIndexes = commandObfuscationMetric.getCommandIndexes(command.length(), arguments, 0.2);

        System.out.print("SOM " + "\t");

        printDoubleArray(cmdIndexes[0]);

        System.out.print("AOI " + "\t");

        printDoubleArray(cmdIndexes[1]);

        System.out.print("COI " + "\t");

        printDoubleArray(cmdIndexes[2]);
    }

    private static void printDoubleArray(double[] indexes)
    {
        for (int k = 0; k < indexes.length; k++)
        {
            System.out.printf("%.2f", indexes[k]);

            if (k < indexes.length - 1)
                System.out.print("\t");
        }

        System.out.println();
    }
}