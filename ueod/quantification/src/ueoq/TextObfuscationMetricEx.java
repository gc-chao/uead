
package ueoq;

import java.io.*;
import java.util.*;
import java.lang.Character.*;

import ueot.*;

public class TextObfuscationMetricEx extends TextObfuscationMetric
{
    private List<int[]> substitutableChars;
    private Map<String, Integer[]> substitutableChars2;
    private Map<String, Integer[]> substitutableChars3;
    private Map<String, Integer[]> substitutableChars4;

    private final UnicodeStandard unicodeStandard;
    private final String[] unicodeWritingSystems;

    private final TextObfuscationMetric.SystemDiscriminator<Character> latinCharDiscr = new TextObfuscationMetric.SystemDiscriminator<Character>()
    {
        @Override
        public int getSystem(Character e)
        {
            return e - 'A';
        }

        @Override
        public String getSystemName(int k)
        {
            return String.valueOf((char)('A' + k));
        }

        @Override
        public int compositeCount(Character e)
        {
            return 8;
        }
    };

    private TextObfuscationMetric.SystemDiscriminator<Integer[]> unicodeDiscr = new TextObfuscationMetric.SystemDiscriminator<Integer[]>()
    {
        @Override
        public int getSystem(Integer[] codepoints)
        {
            try
            {
                Integer basicChar = codepoints[0];

                String systemName = basicChar < 256 ? getBlockWithScript('a') : getBlockWithScript(basicChar);

                for (int i = 0; i < unicodeWritingSystems.length; i++)
                    if (unicodeWritingSystems[i].equals(systemName))
                        return i;

                return unicodeWritingSystems.length - 1;
            }
            catch (Exception e2)
            {
                return -1;
            }
        }

        @Override
        public String getSystemName(int k)
        {
            return unicodeWritingSystems[k];
        }

        @Override
        public int compositeCount(Integer[] codepoints)
        {
            return codepoints.length;
        }
    };

    private String[] initSystems(int ignorableVersion)
    {
        int[] modifier43;
        int[] ignorableChars;

        try
        {
            modifier43 = InternationalChar.getModifiers43();
            ignorableChars = InternationalChar.getIgnorableChars(ignorableVersion);
            substitutableChars = InternationalChar.getBasicPartOfSubstitutableChars();
            substitutableChars2 = buildCache(InternationalChar.getSubstitutableChars2());
            substitutableChars3 = buildCache(InternationalChar.getSubstitutableChars3());
            substitutableChars4 = buildCache(InternationalChar.getSubstitutableChars4());
        }
        catch (IOException exception)
        {
            System.err.println("missing chars_database!");
            return null;
        }

        try
        {
            HashSet<String> systemSet = new HashSet<>();

            getBlockWithScript(new int[] { '\\', '^' }, systemSet);
            getBlockWithScript(InternationalChar.getLatin(), systemSet);
            getBlockWithScript(InternationalChar.getHans52(), systemSet);
            getBlockWithScript(InternationalChar.getCyrillic33(), systemSet);
            getBlockWithScript(InternationalChar.getGreek24(), systemSet);
            getBlockWithScript(InternationalChar.getGematria22(), systemSet);
            getBlockWithScript(InternationalChar.getArabic28(), systemSet);
            getBlockWithScript(InternationalChar.getHangul40(), systemSet);
            getBlockWithScript(InternationalChar.getThai76(), systemSet);
            getBlockWithScript(modifier43, systemSet);
            getBlockWithScript(ignorableChars, systemSet);

            for (int[] equal2 : substitutableChars)
                getBlockWithScript(equal2, systemSet);

            String[] systems = systemSet.toArray(new String[systemSet.size() + 1]);
            systems[systems.length - 1] = "UNSEEN";

            return systems;
        }
        catch (IOException e)
        {
            System.err.println("missing unicode_block.txt!");
            return null;
        }
    }

    private static Map<String, Integer[]> buildCache(List<int[][]> combinatingCharsList)
    {
        Map<String, Integer[]> map = new HashMap<String, Integer[]>();

        for (int[][] combinatingChars : combinatingCharsList)
        {
            for (int[] combinatingChar : combinatingChars)
            {
                StringBuilder key = new StringBuilder(combinatingChar.length * 4);

                for (int num : combinatingChar)
                    key.append((char)num);

                Integer[] value = new Integer[combinatingChar.length];

                for (int i = 0; i < combinatingChar.length; i++)
                    value[i] = combinatingChar[i];

                map.put(key.toString(), value);
            }
        }

        return map;
    }

    private String getBlockWithScript(int codePoint) throws IOException
    {
        String blockNumber = String.valueOf(unicodeStandard.getBlockNumber(codePoint));
        UnicodeScript script = UnicodeScript.of(codePoint);

        return blockNumber + script.name();
    }

    private void getBlockWithScript(int[] codePoints, HashSet<String> blockWithScriptSet) throws IOException
    {
        for (int i = 0; i < codePoints.length; i++)
        {
            int codePoint = codePoints[i];

            String key = getBlockWithScript(codePoint);

            blockWithScriptSet.add(key);
        }
    }

    public TextObfuscationMetricEx(int ignorableVersion) throws IOException
    {
        unicodeStandard = new UnicodeStandard();
        unicodeWritingSystems = initSystems(ignorableVersion);
    }

    public double[] getIndexes(String text, boolean isLatin)
    {
        if (isLatin)
        {
            Character[] elements = Util.stringToLatinArray(text);

            return super.getIndexes(3, elements, latinCharDiscr, 0.5, 0.5, 0);
        }
        else
        {
            Integer[][] elements = stringToUnicodeArray(text);

            return super.getIndexes(unicodeWritingSystems.length, elements, unicodeDiscr);
        }
    }

    public Integer[][] stringToUnicodeArray(String str)
    {
        int[] codePoints = str.codePoints().toArray();

        List<Integer[]> results = new ArrayList<>(codePoints.length);

        for (int i = 0; i < codePoints.length; i++)
        {
            Integer[] combination = null;

            if (codePoints.length >= i + 4)
                combination = findCombinatingChars(substitutableChars4, codePoints, i, 4);

            if (combination == null && codePoints.length >= i + 3)
                combination = findCombinatingChars(substitutableChars3, codePoints, i, 3);

            if (combination == null && codePoints.length >= i + 2)
                combination = findCombinatingChars(substitutableChars2, codePoints, i, 2);

            if (combination == null)
                results.add(new Integer[] { codePoints[i] });
            else
            {
                results.add(combination);
                i = i + combination.length - 1;
            }
        }

        return results.toArray(new Integer[results.size()][]);
    }

    private Integer[] findCombinatingChars(Map<String, Integer[]> cache, int[] codePoints, int start, int length)
    {
        StringBuilder key = new StringBuilder(length);

        for (int i = 0; i < length; i++)
            key.append((char)codePoints[start + i]);

        return cache.get(key.toString());
    }
}