
package ueod;

import java.io.*;
import java.util.*;

import ueot.*;

public class RobustnessObfuscationCreator extends ArgumentObfuscationCreator
{
    private int seed = 0;

    private final int ignorableCharVersion;
    private final int insertionCount;
    private final int maxUnknowNum;

    public RobustnessObfuscationCreator(int variantVersion, int insertionCount, int maxUnknowNum)
    {
        this.ignorableCharVersion = variantVersion;
        this.insertionCount = insertionCount;
        this.maxUnknowNum = maxUnknowNum;
    }

    protected ArrayList<Pair<String, CommandObfuscator>> createObfuscators() throws IOException
    {
        ArrayList<Pair<String, CommandObfuscator>> commandObfuscators = new ArrayList<>();

        commandObfuscators.add(new Pair<String, CommandObfuscator>("", createCommandObfuscator()));

        return commandObfuscators;
    }

    private CommandObfuscator createCommandObfuscator() throws IOException
    {
        CommandObfuscator.Configuration config = new CommandObfuscator.Configuration();

        config.writingSystemNumber = 1;
        config.identifiedByScript = false;

        config.enabledIgnorableChars = true;
        config.ignorableCharsMax = insertionCount;
        config.ignorableCharsInsertMaxEachTime = 2;
        config.ignorableCharsInsertMinEachTime = 1;
        config.ignorableCharsProportion = insertionCount / (float)maxUnknowNum;

        config.blankMax = 0;
        config.caretMax = 0;
        config.quotaMax = 0;
        config.slashMax = 0;

        config.randomSeed = seed++;
        config.ignorableCharVersion = ignorableCharVersion;

        return new CommandObfuscator(config);
    }
}