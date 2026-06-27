
package ueot;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import java.lang.Character.*;

public class TextObfuscator
{
    public static class Configuration
    {
        public long randomSeed;
        public boolean printLog;

        public float[] writingSystemProportion;
        public boolean identifiedByScript;

        public boolean enabledModifierSustitutableChars1;
        public boolean enabledOtherSustitutableChars1;
        public boolean enabledSubstitutableChars2;
        public boolean enabledSubstitutableChars3;
        public boolean enabledSubstitutableChars4;

        public float shuffleLocal;
    }

    private final Configuration config;
    private final RandomGenerator random;

    // scriptSystem => ('a'=>List(), ..., 'z'=>List())
    private HashMap<String, HashMap<Character, ArrayList<int[]>>> databases = new HashMap<>();

    /**
     * If specified script type is assigned, put them in database
     */
    private void initCharsDatabase() throws IOException
    {
        if (this.config.enabledModifierSustitutableChars1)
        {
            HashMap<Character, Character> substitutableChars1 = InternationalChar.getModifierLetterMap();

            for (Character category : substitutableChars1.keySet())
                categoryOneCharacter(substitutableChars1.get(category), new int[] { category });
        }

        if (this.config.enabledOtherSustitutableChars1)
        {
            @SuppressWarnings("unchecked")
            HashMap<Character, Character>[] substitutableChars1 = new HashMap[] { InternationalChar.getHansMap(), InternationalChar.getCyrillicMap(), InternationalChar.getGreekMap(), InternationalChar.getHebrewMap(), InternationalChar.getArabicMap(), InternationalChar.getKoreanMap(), InternationalChar.getThaiMap()
            };

            for (HashMap<Character, Character> map : substitutableChars1)
                for (Character category : map.keySet())
                    categoryOneCharacter(Character.toLowerCase(map.get(category)), new int[] { category });
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
    }

    private void categoryOneCharacter(char category, int[] charCodePoints)
    {
        int basicCharCodePoint = charCodePoints[0];

        UnicodeBlock block = UnicodeBlock.of(basicCharCodePoint);

        if (block == null)
            return;

        String blockName = block.toString();
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

        // 'a' => List(substitutable a), ..., 'z' => List(substitutable z)
        HashMap<Character, ArrayList<int[]>> systemDatabase = databases.get(systemName);

        if (!systemDatabase.containsKey(category))
            systemDatabase.put(category, new ArrayList<int[]>());

        ArrayList<int[]> codePointsList = systemDatabase.get(category);

        codePointsList.add(charCodePoints);
    }

    protected List<String> filterForSystemNumber()
    {
        int systemNumber = config.writingSystemProportion.length;

        Stream<Entry<String, HashMap<Character, ArrayList<int[]>>>> databaseStream = databases.entrySet().stream();
        List<Map.Entry<String, HashMap<Character, ArrayList<int[]>>>> databaseList = databaseStream.collect(Collectors.toList());

        // keep only characters with most code points
        databaseList.sort((e1, e2) ->
        {
            int size1 = e1.getValue() == null ? 0 : e1.getValue().size();
            int size2 = e2.getValue() == null ? 0 : e2.getValue().size();
            return Integer.compare(size2, size1);
        });

        return databaseList.stream().map(Entry::getKey).limit(Math.min(systemNumber, databaseList.size())).collect(Collectors.toList());
    }

    protected List<String> generateSystemSelection(int characterCount, List<String> system)
    {
        float[] properation = config.writingSystemProportion;

        if (config.printLog && system.size() != properation.length)
            System.err.println("NO ENOUGH SYSTEM SIZE");

        List<String> result = new ArrayList<>();

        for (int i = 0; i < properation.length; i++)
        {
            int count = (int)(properation[i] * characterCount);

            for (int j = 0; j < count; j++)
                result.add(system.get(i));
        }

        while (result.size() < characterCount)
            result.add("LATIN");

        shuffle(result, 0.1);

        return result;
    }

    private void shuffle(List<String> list, double switchIntensity)
    {
        if (list.isEmpty())
            return;

        List<String> temp = new ArrayList<>(list);
        Collections.shuffle(temp, random);

        list.clear();

        String last = null;

        while (!temp.isEmpty())
        {
            String candidate;

            if (last != null && random.nextDouble() < switchIntensity)
            {
                candidate = findDifferentCandidate(temp, last);

                if (candidate == null)
                    candidate = temp.get(random.nextInt(temp.size()));
            }
            else
            {
                candidate = temp.get(random.nextInt(temp.size()));
            }

            list.add(candidate);
            temp.remove(candidate);
            last = candidate;
        }
    }

    private String findDifferentCandidate(List<String> candidates, String differentFrom)
    {
        for (String candidate : candidates)
            if (!candidate.equals(differentFrom))
                return candidate;

        return null;
    }

    protected char[] mapObfuscatedChars(char[] chs, List<String> obfuscatedSystem)
    {
        List<Character> codePoints = new ArrayList<Character>();

        for (int i = 0; i < chs.length; i++)
        {
            String system = obfuscatedSystem.get(i);

            char ch = Character.toLowerCase(chs[i]);

            if (system.equals("LATIN"))
            {
                codePoints.add(ch);
                continue;
            }

            HashMap<Character, ArrayList<int[]>> database = databases.get(system);
            ArrayList<int[]> chars = database.get(ch);

            if (chars == null)
            {
                if (config.printLog)
                    System.err.println("NOT ENOUGH CHARS!");

                codePoints.add(ch);
            }
            else
            {
                int[] cps = random.getListItem(chars);

                for (int cp : cps)
                    codePoints.add((char)cp);
            }
        }

        return codePoints.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
    }

    public TextObfuscator(Configuration config) throws IOException
    {
        this.config = config;
        this.random = new RandomGenerator(config.randomSeed);

        initCharsDatabase();
    }

    /**
     * obfuscate a string.
     */
    public char[] obfuscate(char[] chs)
    {
        List<String> filteredSystemName = filterForSystemNumber();
        List<String> obfuscatedSystemName = generateSystemSelection(chs.length, filteredSystemName);

        return mapObfuscatedChars(chs, obfuscatedSystemName);
    }
}