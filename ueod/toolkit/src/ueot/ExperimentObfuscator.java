
package ueot;

import java.io.*;
import java.util.ArrayList;

import java.nio.charset.*;

public class ExperimentObfuscator
{
    private final PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    protected TextObfuscator createTextObfuscator(float[] writingSystemNumbers, boolean usingSubstitutableChars1, boolean usingSubstitutableChars2, boolean usingSubstitutableChars3, boolean usingSubstitutableChars4, float shuffleLocal) throws IOException
    {
        TextObfuscator.Configuration config = new TextObfuscator.Configuration();

        config.writingSystemProportion = writingSystemNumbers;
        config.identifiedByScript = false;

        if (usingSubstitutableChars1)
        {
            config.enabledModifierSustitutableChars1 = true;
            config.enabledOtherSustitutableChars1 = true;
        }

        config.enabledSubstitutableChars2 = usingSubstitutableChars2;
        config.enabledSubstitutableChars3 = usingSubstitutableChars3;
        config.enabledSubstitutableChars4 = usingSubstitutableChars4;

        config.shuffleLocal = shuffleLocal;
        config.randomSeed = 12345L;

        return new TextObfuscator(config);
    }

    protected CommandObfuscator createCommandObfuscator(int writingSystemNumber, boolean enabledSubstitutable, int blankMax, int caretMax, int quotaMax, int slashMax) throws IOException
    {
        CommandObfuscator.Configuration config = new CommandObfuscator.Configuration();

        config.writingSystemNumber = writingSystemNumber;
        config.identifiedByScript = false;

        if (writingSystemNumber > 0)
        {
            config.enabledIgnorableChars = true;
            config.ignorableCharsMax = Integer.MAX_VALUE;
            config.ignorableCharsInsertMaxEachTime = 1;
            config.ignorableCharsInsertMinEachTime = 1;
            config.ignorableCharsProportion = 1.0f;
        }

        if (enabledSubstitutable)
        {
            config.substitutableCharsProportion = 1.0f;

            config.enabledModifierSustitutableChars1 = true;
            config.enabledSubstitutableChars2 = true;
            config.enabledSubstitutableChars3 = true;
            config.enabledSubstitutableChars4 = true;
        }

        config.blankMax = blankMax;
        config.caretMax = caretMax;
        config.quotaMax = quotaMax;
        config.slashMax = slashMax;

        config.randomSeed = 12345L;

        return new CommandObfuscator(config);
    }

    protected String obfuscatedText(TextObfuscator obfuscator, String text, boolean printResult)
    {
        char[] obfuscatedChs = obfuscator.obfuscate(text.toCharArray());
        String obfuscatedStr = new String(obfuscatedChs);
        int[] intArray = obfuscatedStr.chars().toArray();

        if (printResult)
        {
            printStream.print(text);
            printStream.print("\t");
            printStream.print(obfuscatedChs.length);
            printStream.print("\t");
            printStream.print(obfuscatedStr);
            printStream.print("\t");
            printStream.print(Util.intArrayToString(intArray));
            printStream.print("\n");
        }

        return obfuscatedStr;
    }

    protected String obfuscatedCommand(CommandObfuscator obfuscator, String command, boolean printResult)
    {
        command = obfuscator.syntaxObfuscate(command);
        command = obfuscator.encodingObfuscate(command);

        if (printResult)
            printStream.println(command);

        return command;
    }

    public ArrayList<Pair<String, String>> createObfuscateStringSamples(String text, int sampleCount, boolean printResult) throws IOException
    {
        ArrayList<Pair<String, String>> results = new ArrayList<>();

        ArrayList<Pair<String, TextObfuscator>> textObfuscators = new ArrayList<Pair<String, TextObfuscator>>()
        {
            private static final long serialVersionUID = 1L;
            {
                add(new Pair<String, TextObfuscator>("S=1,P=0.50,C=2,W=0.1",  createTextObfuscator(new float[] { 0.5f }, false, true, false, false, 0.1f)));
                add(new Pair<String, TextObfuscator>("S=1,P=1.00,C=2,W=0.5",  createTextObfuscator(new float[] { 1.0f }, false, true, false, false, 0.5f)));
                add(new Pair<String, TextObfuscator>("S=20,P=0.05,C=2,W=1.0", createTextObfuscator(new float[] { 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f }, true, true, false, false, 1.0f)));
            }
        };

        for (int i = 0; i < textObfuscators.size(); i++)
        {
            for (int j = 0; j < sampleCount; j++)
            {
                Pair<String, TextObfuscator> pair = textObfuscators.get(i);

                String obfuscatedText = obfuscatedText(pair.getValue(), text, printResult);

                results.add(new Pair<String, String>(pair.getKey(), obfuscatedText));
            }
        }

        return results;
    }

    public ArrayList<Pair<String, String>> createObfuscateTextSamples(String text, int sampleCount, boolean printResult) throws IOException
    {
        ArrayList<Pair<String, String>> results = new ArrayList<>();

        ArrayList<Pair<String, TextObfuscator>> textObfuscators = new ArrayList<Pair<String, TextObfuscator>>()
        {
            private static final long serialVersionUID = 1L;
            {
                add(new Pair<String, TextObfuscator>("S=0,P=0.00,C=1,W=0.0",  createTextObfuscator(new float[] { 0.0f }, false, false, false, false, 1.0f)));

                // claim ceo
                add(new Pair<String, TextObfuscator>("S=1,P=0.50,C=2,W=0.1",  createTextObfuscator(new float[] { 0.5f }, false, true, false, false, 0.1f)));
                add(new Pair<String, TextObfuscator>("S=1,P=1.00,C=2,W=0.5",  createTextObfuscator(new float[] { 1.0f }, false, true, false, false, 0.5f)));
                add(new Pair<String, TextObfuscator>("S=1,P=1.00,C=3,W=0.8",  createTextObfuscator(new float[] { 1.0f }, false, false, true,  false, 0.8f)));
                add(new Pair<String, TextObfuscator>("S=1,P=1.00,C=4,W=1.0",  createTextObfuscator(new float[] { 1.0f }, false, false, false,  true,  1.0f)));

                // claim geo
                add(new Pair<String, TextObfuscator>("S=1,P=0.01,C=2,W=0.2",  createTextObfuscator(new float[] { 0.01f }, true, true, false, false, 0.2f)));
                add(new Pair<String, TextObfuscator>("S=2,P=0.01,C=2,W=0.4",  createTextObfuscator(new float[] { 0.01f, 0.01f }, true, true, false, false, 0.4f)));
                add(new Pair<String, TextObfuscator>("S=5,P=0.01,C=2,W=0.6",  createTextObfuscator(new float[] { 0.01f, 0.01f, 0.01f, 0.01f, 0.01f }, true, true, false, false, 0.6f)));
                add(new Pair<String, TextObfuscator>("S=10,P=0.01,C=2,W=0.8", createTextObfuscator(new float[] { 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f }, true, true, false, false, 0.8f)));

                // claim leo
                add(new Pair<String, TextObfuscator>("S=20,P=0.01,C=2,W=0.1", createTextObfuscator(new float[] { 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f }, true, true, false, false, 0.1f)));
                add(new Pair<String, TextObfuscator>("S=20,P=0.02,C=2,W=0.5", createTextObfuscator(new float[] { 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f, 0.02f }, true, true, false, false, 0.5f)));
                add(new Pair<String, TextObfuscator>("S=20,P=0.05,C=2,W=1.0", createTextObfuscator(new float[] { 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f }, true, true, false, false, 1.0f)));
            }
        };

        for (int i = 0; i < textObfuscators.size(); i++)
        {
            for (int j = 0; j < sampleCount; j++)
            {
                Pair<String, TextObfuscator> pair = textObfuscators.get(i);

                String obfuscatedText = obfuscatedText(pair.getValue(), text, printResult);

                results.add(new Pair<String, String>(pair.getKey(), obfuscatedText));
            }
        }

        return results;
    }

    public ArrayList<Pair<String, String>> createObfuscateCommandSamples(String text, int sampleCount, boolean printResult) throws IOException
    {
        ArrayList<Pair<String, String>> results = new ArrayList<>();

        ArrayList<Pair<String, CommandObfuscator>> commandObfuscators = new ArrayList<Pair<String, CommandObfuscator>>()
        {
            private static final long serialVersionUID = 1L;
            {
                add(new Pair<String, CommandObfuscator>("E=0,S=0,BCQS=0", createCommandObfuscator(0, false, 0, 0, 0, 0)));
                add(new Pair<String, CommandObfuscator>("E=0,S=0,BCQS=5", createCommandObfuscator(0, false, 5, 5, 5, 5)));
                add(new Pair<String, CommandObfuscator>("E=1,S=1,BCQS=0", createCommandObfuscator(1, false, 0, 0, 0, 0)));
                add(new Pair<String, CommandObfuscator>("E=1,S=2,BCQS=1", createCommandObfuscator(2, false, 1, 1, 1, 1)));
                add(new Pair<String, CommandObfuscator>("E=1,S=5,BCQS=3", createCommandObfuscator(5, true,  3, 3, 3, 3)));
                add(new Pair<String, CommandObfuscator>("E=1,S=9,BCQS=7", createCommandObfuscator(9, true,  7, 7, 7, 7)));
            }
        };

        for (int i = 0; i < commandObfuscators.size(); i++)
        {
            for (int j = 0; j < sampleCount; j++)
            {
                Pair<String, CommandObfuscator> pair = commandObfuscators.get(i);

                String obfuscatedText = obfuscatedCommand(pair.getValue(), text, printResult);

                results.add(new Pair<String, String>(pair.getKey(), obfuscatedText));
            }
        }

        return results;
    }

    public static void main(String[] args) throws IOException
    {
        ExperimentObfuscator instance = new ExperimentObfuscator();

        instance.createObfuscateStringSamples("TheQuickBrownFoxJumpsOverTheLazyDog", 1, true);
        instance.createObfuscateCommandSamples("reg [save] [hklm]\\sam c:\\11.hiv", 1, true);
    }
}