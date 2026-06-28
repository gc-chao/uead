
package ueod;

import java.io.*;
import java.util.*;

import ueoq.*;

public class TCLESDetection
{
    public static class CommandContext
    {
        public int label;
        public String[] rules;
        public String template;
        public String original;
        public String obfuscated;
        public String obfuscation;
        public String logger;
        public double[] EOIs;
        public boolean[] matches;
        public double[] metrics;
        public int originalCommandLength;
        public int obfuscatedCommandLength;
        public int originalArgumentLength;
        public int obfuscatedArgumentLength;
    }

    public static String serializeCommand(int label, String rule, String original, String obfuscation, String obfuscated)
    {
        return label + ";" + rule + ";" + original + ";" + obfuscation + ";" + obfuscated;
    }

    public static CommandContext[] deserializeCommands(List<String> dataset)
    {
        CommandContext[] commands = new CommandContext[dataset.size()];

        // prepare data
        for (int i = 0; i < commands.length; i++)
        {
            String data = dataset.get(i);
            String[] features = data.split(";");

            commands[i] = new CommandContext();
            commands[i].label = Integer.parseInt(features[0]);
            commands[i].rules = features[1].split(",");
            commands[i].template = features[2];
            commands[i].original = features[2].replace("[", "").replace("]", "");
            commands[i].obfuscation = features[3];
            commands[i].obfuscated = features[4];
        }

        return commands;
    }

    public static void calculateCommandScores(int metricVersion, CommandContext[] commands, boolean printLog) throws IOException
    {
        TextObfuscationMetricEx converter = new TextObfuscationMetricEx(metricVersion);
        TextEncodingEquivalentMetric metricer = new TextEncodingEquivalentMetric(metricVersion);

        for (int i = 0; i < commands.length; i++)
        {
            if (i % 100 == 0)
                System.out.print((int)(i * 100 / (double)commands.length) + "% ");

            CommandContext command = commands[i];

            String[] rules = command.rules;
            String[] tmplArgs = command.template.split(" ");
            String[] originalArgs = command.original.split(" ");
            String[] obfuscatedArgs = command.obfuscated.split(" ");

            StringBuilder logger = new StringBuilder();

            if (printLog)
            {
                logger.append("============\n");
                logger.append("COMMAND\n");
                logger.append("============\n");
                logger.append(command.obfuscated);
                logger.append("\n");
            }

            // make all arguments string
            for (int j = 0; j < obfuscatedArgs.length; j++)
            {
                command.originalCommandLength += originalArgs[j].length();
                command.obfuscatedCommandLength += obfuscatedArgs[j].length();

                if (tmplArgs[j].contains("["))
                {
                    command.originalArgumentLength += originalArgs[j].length();
                    command.obfuscatedArgumentLength += obfuscatedArgs[j].length();
                }
            }

            command.matches = new boolean[rules.length];
            command.metrics = new double[rules.length];

            // calculate similarity in all arguments for each rule
            for (int j = 0; j < rules.length; j++)
            {
                // print all arguments
                if (printLog)
                {
                    logger.append("\t-----------\n");
                    logger.append("\tARGUMENTS FOR RULE:" + rules[j] + "\n");
                    logger.append("\t-----------\n");
                }

                for (String obfuscatedArgument : obfuscatedArgs)
                    if (obfuscatedArgument.contains(rules[j]))
                        command.matches[j] = true;

                for (String obfuscatedArgument : obfuscatedArgs)
                {
                    double[] similarities = getSimilarity(converter, metricer, rules[j], obfuscatedArgument);

                    if (printLog)
                        logger.append("\t" + similarities[0] + ",\t" + similarities[1] + ",\t" + rules[j] + ",\t" + obfuscatedArgument + "\n");

                    command.metrics[j] = Math.max(command.metrics[j], similarities[1]);
                }
            }

            if (printLog)
                command.logger = logger.toString();
        }

        System.out.println();
    }

    public static boolean predictMaliciousness(CommandContext command, double threshold, boolean usingTCLES)
    {
        if (usingTCLES)
        {
            double[] metrics = command.metrics;

            boolean matched = true;

            for (double metric : metrics)
            {
                if (metric < threshold)
                {
                    matched = false;
                    break;
                }
            }

            return matched;
        }
        else
        {
            boolean[] matches = command.matches;

            for (boolean match : matches)
                if (!match)
                    return false;

            return true;
        }
    }

    public static List<ROCPoint> computeROCPoints(CommandContext[] commands, boolean usingTCLES)
    {
        List<ROCPoint> points = new ArrayList<>();

        for (double threshold = 0.0; threshold <= 1.05; threshold += 0.05)
        {
            int tp = 0, fp = 0, tn = 0, fn = 0;

            for (CommandContext command : commands)
            {
                boolean predictPositive = predictMaliciousness(command, threshold, usingTCLES);
                boolean actualPositive = command.label == 1;

                if (actualPositive && predictPositive)
                    tp++;
                else if (!actualPositive && predictPositive)
                    fp++;
                else if (!actualPositive && !predictPositive)
                    tn++;
                else if (actualPositive && !predictPositive)
                    fn++;
            }

            double tpr = (tp + fn == 0) ? 0 : (double)tp / (tp + fn);
            double fpr = (fp + tn == 0) ? 0 : (double)fp / (fp + tn);
            double accuracy = (double)(tp + tn) / (tp + tn + fp + fn);

            System.out.println("threshold: " + threshold + ", tpr:" + tpr + ", fpr: " + fpr + ", acc:" + accuracy);

            points.add(new ROCPoint(threshold, tpr, fpr, accuracy));
        }

        return points;
    }

    private static double[] getSimilarity(TextObfuscationMetricEx converter, TextEncodingEquivalentMetric metricer, String originalText, String obfuscatedText)
    {
        Integer[][] chs = converter.stringToUnicodeArray(obfuscatedText);

        char[] originalChars = originalText.toCharArray();
        char[] basicChars = new char[chs.length];

        for (int i = 0; i < chs.length; i++)
            basicChars[i] = (char)chs[i][0].intValue();

        double cleed = metricer.crossLocaleEquivalentEditDistance(originalChars, basicChars, false);
        double tcles = metricer.textCrossLocaleEquivalenceScore(originalChars, basicChars, cleed);

        return new double[] { cleed, tcles };
    }
}