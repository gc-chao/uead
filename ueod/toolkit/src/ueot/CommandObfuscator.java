
package ueot;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;
import java.util.function.*;
import java.lang.Character.*;

public class CommandObfuscator
{
    public static class Configuration
    {
        public long randomSeed;

        public boolean enabledIgnorableChars;
        public boolean enabledModifierSustitutableChars1;
        public boolean enabledSubstitutableChars2;
        public boolean enabledSubstitutableChars3;
        public boolean enabledSubstitutableChars4;

        public int ignorableCharsMax;
        public int ignorableCharsInsertMinEachTime;
        public int ignorableCharsInsertMaxEachTime;
        public float ignorableCharsProportion;
        public float substitutableCharsProportion;

        public boolean identifiedByScript;
        public int writingSystemNumber;

        public int blankMax;
        public int quotaMax;
        public int caretMax;
        public int slashMax;

        public boolean printLog;
        public int ignorableCharVersion;
    }

    private final Configuration config;
    private final RandomGenerator random;

    private final UnicodeStandard unicodeStandard;

    // scriptSystem => ('a'=>List(), ..., 'z'=>List(), '*'=>List())
    private HashMap<String, HashMap<Character, ArrayList<int[]>>> databases = new HashMap<>();

    public CommandObfuscator(Configuration config) throws IOException
    {
        this.config = config;
        this.random = new RandomGenerator(config.randomSeed);

        this.unicodeStandard = new UnicodeStandard();

        initCharsDatabase();
    }

    /**
     * obfuscate a string.
     */
    private char[] obfuscate(char[] cmd, ReferenceInteger remains)
    {
        List<Character> codePoints = new ArrayList<Character>();

        for (char ch : cmd)
        {
            ch = Character.toLowerCase(ch);

            if (!Character.isAlphabetic(ch))
                codePoints.add(ch);
            else
            {
                insertIgnorableChars(codePoints, remains);
                replaceSubstitutableChars(codePoints, ch);
            }
        }

        return codePoints.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
    }

    protected char[] encodingObfuscate(char[] cmd, ReferenceInteger remains)
    {
        boolean inBracket = false;

        StringBuilder temp = new StringBuilder();
        List<Character> result = new ArrayList<Character>();

        for (int i = 0; i < cmd.length; i++)
        {
            char ch = cmd[i];

            if (!inBracket)
            {
                if (ch == '[')
                    inBracket = true;
                else
                    result.add(ch);
            }
            else
            {
                if (ch != ']')
                    temp.append(ch);
                else
                {
                    inBracket = false;

                    char[] obfchs = obfuscate(temp.toString().toCharArray(), remains);

                    for (int j = 0; j < obfchs.length; j++)
                        result.add(obfchs[j]);

                    temp.setLength(0);
                }
            }
        }

        return Util.listToCharArray(result);
    }

    public String encodingObfuscate(String command)
    {
        ReferenceInteger remains = new ReferenceInteger(config.ignorableCharsMax);

        char[] chs = command.toCharArray();

        return String.valueOf(encodingObfuscate(chs, remains));
    }

    public String[] encodingObfuscate(String[] commands)
    {
        ReferenceInteger remains = new ReferenceInteger(config.ignorableCharsMax);

        String[] obfscatedArguments = new String[commands.length];

        for (int i = 0; i < commands.length; i++)
        {
            char[] chs = commands[i].toCharArray();

            obfscatedArguments[i] = String.valueOf(encodingObfuscate(chs, remains));
        }

        return obfscatedArguments;
    }

    public String syntaxObfuscate(String command)
    {
        int remainBlank = config.blankMax;
        int remainQuota = config.quotaMax;
        int remainCaret = config.caretMax;
        int remainSlash = config.slashMax;

        String[] parts = command.replace("\t", " ").replace("  ", " ").split(" ");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; i++)
        {
            String part = parts[i];

            boolean isPathParam = false;

            // is path parameter?
            if (Character.isAlphabetic(part.charAt(0)) && part.charAt(1) == ':' && part.charAt(2) == '\\')
                isPathParam = true;

            // insert slash
            if (isPathParam && remainSlash > 0)
            {
                StringBuilder slashBuilder = new StringBuilder();

                slashBuilder.append(part.substring(0, 3));
                slashBuilder.append('\\');

                remainSlash--;

                while (remainSlash / 2 > 0)
                {
                    remainSlash -= 2;
                    slashBuilder.append('\\');
                    slashBuilder.append('\\');
                }

                remainQuota--;
                slashBuilder.append('"');

                slashBuilder.append(part.substring(3));

                part = slashBuilder.toString();
            }

            // insert obfuscated blank
            remainBlank = insertObfuscatedBlank(remainBlank, parts, i, builder);

            boolean[] insertFlag = new boolean[2];

            // insert left quota
            if (!isPathParam)
            {
                int[] compondRemains1 = insertLeftObfuscatedQuota(1, remainQuota, remainCaret, builder);

                insertFlag[0] = compondRemains1[0] != remainQuota;
                insertFlag[1] = compondRemains1[1] != remainCaret;

                remainQuota = compondRemains1[0];
                remainCaret = compondRemains1[1];
            }

            // insert caret
            remainCaret = insertObfuscatedCaret(remainCaret, part, builder);

            // insert right quota
            if (!isPathParam)
            {
                int[] compondRemains2 = insertRightObfuscatedQuota(insertFlag, remainQuota, remainCaret, builder);

                remainQuota = compondRemains2[0];
                remainCaret = compondRemains2[1];
            }

            // insert cart in last ch
            if (remainCaret > 0)
            {
                remainCaret--;

                builder.append("^");
            }

            // insert at least blank
            if (i != parts.length - 1)
                builder.append(" ");
        }

        return builder.toString();
    }

    /**
     * Get all writing system according to specified category.
     * Such as getAllWritingSystemOfChar('a'), ..., getAllScriptSystemOfChar('z'), or getAllScriptSystemOfChar('*')
     */
    protected HashMap<String, ArrayList<int[]>> getAllWritingSystemOfChar(char category, Integer minCodePoint)
    {
        HashMap<String, ArrayList<int[]>> systemOfChar = new HashMap<>();

        for (String system : databases.keySet())
        {
            HashMap<Character, ArrayList<int[]>> database = databases.get(system);

            if (!database.containsKey(category))
                continue;

            ArrayList<int[]> arrayList = database.get(category);

            if (minCodePoint != null && arrayList.size() < minCodePoint)
                continue;

            systemOfChar.put(system, database.get(category));
        }

        return systemOfChar;
    }

    /**
     * Get a char of specified category (such as 'a', ..., 'z', '*') randomly.
     */
    protected ArrayList<int[]> randomGetObfuscatedChar(char category, int minCodePoint)
    {
        HashMap<String, ArrayList<int[]>> systemOfChar = getAllWritingSystemOfChar(category, minCodePoint);

        if (systemOfChar.size() == 0)
        {
            if (config.printLog)
                System.err.println("NOT ENOUGH CODE POINT COUNT OF '" + category + "'!");

            return null;
        }

        int systemIndex = random.nextRange(systemOfChar.size());

        for (String scriptSystem : systemOfChar.keySet())
        {
            if (systemIndex == 0)
            {
                ArrayList<int[]> obfusctedChars = systemOfChar.get(scriptSystem);
                ArrayList<int[]> clone = new ArrayList<>(obfusctedChars);
                ArrayList<int[]> selected = new ArrayList<>();

                Collections.shuffle(clone, random);

                for (int i = 0; i < Math.min(minCodePoint, clone.size()); i++)
                    selected.add(clone.get(i));

                return selected;
            }

            systemIndex--;
        }

        return null;
    }

    /**
     * If specified script type is assigned, put them in database
     */
    private void initCharsDatabase() throws IOException
    {
        if (this.config.enabledIgnorableChars)
        {
            int[] ignorableChars = InternationalChar.getIgnorableChars(config.ignorableCharVersion);

            for (int codePoint : ignorableChars)
                categoryOneCharacter('*', new int[] { codePoint });
        }

        if (this.config.enabledModifierSustitutableChars1)
        {
            HashMap<Character, Character> substitutableChars1 = InternationalChar.getModifierLetterMap();

            for (Character category : substitutableChars1.keySet())
                categoryOneCharacter(substitutableChars1.get(category), new int[] { category });
        }

        if (this.config.enabledSubstitutableChars2)
        {
            List<int[][]> substitutableChars2 = InternationalChar.getSubstitutableChars2();

            for (int i = 0; i < substitutableChars2.size(); i++)
                for (int[] codePoints : substitutableChars2.get(i))
                    categoryOneCharacter((char)('a' + i), codePoints);
        }

        if (this.config.enabledSubstitutableChars3)
        {
            List<int[][]> substitutableChars3 = InternationalChar.getSubstitutableChars3();

            for (int i = 0; i < substitutableChars3.size(); i++)
                for (int[] codePoints : substitutableChars3.get(i))
                    categoryOneCharacter((char)('a' + i), codePoints);
        }

        if (this.config.enabledSubstitutableChars4)
        {
            List<int[][]> substitutableChars4 = InternationalChar.getSubstitutableChars4();

            for (int i = 0; i < substitutableChars4.size(); i++)
                for (int[] codePoints : substitutableChars4.get(i))
                    categoryOneCharacter((char)('a' + i), codePoints);
        }

        databases = filterDatabases();
    }

    private void categoryOneCharacter(char category, int[] charCodePoints)
    {
        int basicCharCodePoint = charCodePoints[0];

        /* UnicodeBlock block = UnicodeBlock.of(basicCharCodePoint);
         * 
         * if (block == null)
         * return; */

        String blockName = Util.c2s(unicodeStandard.getBlockNumber(basicCharCodePoint)); // block.toString();
        String systemName = blockName;

        if (config.identifiedByScript)
        {
            UnicodeScript script = UnicodeScript.of(basicCharCodePoint);

            if ("UNKNOWN".equals(script.toString()))
                return;

            systemName = systemName + "," + script.toString();
        }

        if (!databases.containsKey(systemName))
            databases.put(systemName, new HashMap<Character, ArrayList<int[]>>());

        // 'a' => List(substitutable a), ..., 'z' => List(substitutable z), '*' => List(ignorable chars)
        HashMap<Character, ArrayList<int[]>> systemDatabase = databases.get(systemName);

        if (!systemDatabase.containsKey(category))
            systemDatabase.put(category, new ArrayList<int[]>());

        ArrayList<int[]> codePointsList = systemDatabase.get(category);

        codePointsList.add(charCodePoints);
    }

    /**
     * Filter database according configuration.
     */
    private HashMap<String, HashMap<Character, ArrayList<int[]>>> filterDatabases()
    {
        Stream<Entry<String, HashMap<Character, ArrayList<int[]>>>> databaseStream = databases.entrySet().stream();

        // if using ignorable chars, filter them first at least
        if (this.config.enabledIgnorableChars)
        {
            Predicate<? super Entry<String, HashMap<Character, ArrayList<int[]>>>> pridicateByStar = entry -> entry.getValue() != null && entry.getValue().containsKey('*');
            databaseStream = databaseStream.filter(pridicateByStar);
        }

        List<Map.Entry<String, HashMap<Character, ArrayList<int[]>>>> databaseList = databaseStream.collect(Collectors.toList());

        // keep only characters with most code points
        databaseList.sort((e1, e2) ->
        {
            int size1 = e1.getValue() == null ? 0 : e1.getValue().size();
            int size2 = e2.getValue() == null ? 0 : e2.getValue().size();
            return Integer.compare(size2, size1);
        });

        HashMap<String, HashMap<Character, ArrayList<int[]>>> result = new HashMap<>();

        // put to result databases
        for (int i = 0; i < Math.min(this.config.writingSystemNumber, databaseList.size()); i++)
            result.put(databaseList.get(i).getKey(), databaseList.get(i).getValue());

        return result;
    }

    private void insertIgnorableChars(List<Character> codePoints, ReferenceInteger remainIgnorableChars)
    {
        if (config.ignorableCharsInsertMaxEachTime == 0)
            return;

        if (remainIgnorableChars.value == 0)
            return;

        int insertNumber = random.nextRange(config.ignorableCharsInsertMinEachTime, config.ignorableCharsInsertMaxEachTime);
        ArrayList<int[]> obfuscatedIgnorableCodePoints = randomGetObfuscatedChar('*', insertNumber);

        if (obfuscatedIgnorableCodePoints != null && (random.nextDouble() < this.config.ignorableCharsProportion))
        {
            for (int[] cp : obfuscatedIgnorableCodePoints)
            {
                if (remainIgnorableChars.value == 0)
                    return;

                codePoints.add((char)cp[0]);

                remainIgnorableChars.value--;
            }
        }
    }

    private ArrayList<int[]> replaceSubstitutableChars(List<Character> codePoints, char ch)
    {
        ArrayList<int[]> obfuscatedSubstitutableCodePoints = randomGetObfuscatedChar(ch, 1);

        if (obfuscatedSubstitutableCodePoints == null || (random.nextDouble() > this.config.substitutableCharsProportion))
            codePoints.add(ch);
        else
        {
            for (int[] cps : obfuscatedSubstitutableCodePoints)
                for (int cp : cps)
                    codePoints.add((char)cp);
        }

        return obfuscatedSubstitutableCodePoints;
    }

    private int insertObfuscatedBlank(int remainBlank, String[] parts, int index, StringBuilder builder)
    {
        if (index > 0 && remainBlank > 0)
        {
            remainBlank--;

            if (random.nextBoolean())
                builder.append(' ');
            else
                builder.append('\t');

            if (index == parts.length - 1)
            {
                while (remainBlank > 0)
                {
                    remainBlank--;

                    if (random.nextBoolean())
                        builder.append(' ');
                    else
                        builder.append('\t');
                }
            }
        }

        return remainBlank;
    }

    private int[] insertLeftObfuscatedQuota(int checkCount, int remainQuota, int remainCaret, StringBuilder builder)
    {
        // insert quota
        if (remainQuota > checkCount)
        {
            if (remainCaret > checkCount)
            {
                remainCaret--;
                builder.append('^');
            }

            remainQuota--;
            builder.append('"');
        }

        return new int[] { remainQuota, remainCaret };
    }

    private int[] insertRightObfuscatedQuota(boolean[] flag, int remainQuota, int remainCaret, StringBuilder builder)
    {
        // insert quota
        if (flag[0])
        {
            if (flag[1])
            {
                remainCaret--;
                builder.append('^');
            }

            remainQuota--;
            builder.append('"');
        }

        return new int[] { remainQuota, remainCaret };
    }

    private int insertObfuscatedCaret(int remainCaret, String part, StringBuilder builder)
    {
        boolean inBracket = false;

        for (int j = 0; j < part.length(); j++)
        {
            char ch = part.charAt(j);

            if (ch == '[' || ch == ']')
            {
                builder.append(ch);
                inBracket = !inBracket;
                continue;
            }

            if (inBracket || remainCaret <= 0)
                builder.append(ch);
            else
            {
                remainCaret--;

                builder.append("^");
                builder.append(ch);
            }
        }

        return remainCaret;
    }
}