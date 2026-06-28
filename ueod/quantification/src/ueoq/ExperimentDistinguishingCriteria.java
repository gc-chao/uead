
package ueoq;

import java.io.*;
import java.util.*;
import java.util.HashMap;

import ueot.*;

public class ExperimentDistinguishingCriteria
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("chars   \ttotal\tblocks\tscripts\tblocks+script");

        int[] ignorableChars = InternationalChar.getIgnorableChars();
        List<int[]> basCombingingChars = InternationalChar.getBasicPartOfSubstitutableChars();
        List<int[]> extCombingingChars = InternationalChar.getExtPartOfSubstitutableChars();
        List<int[][]> combingingChars = InternationalChar.getSubstitutableChars2();

        printSingleCharResult(InternationalChar.getLatin(), "Latin   ");
        printSingleCharResult(InternationalChar.getCyrillic33(), "Cyrillic");
        printSingleCharResult(InternationalChar.getGreek24(), "Greek   ");
        printSingleCharResult(InternationalChar.getGematria22(), "Gematria");
        printSingleCharResult(InternationalChar.getArabic28(), "Arabic  ");
        printSingleCharResult(InternationalChar.getHangul40(), "Hangul  ");
        printSingleCharResult(InternationalChar.getThai76(), "Thai    ");
        printSingleCharResult(InternationalChar.getHans52(), "Hans    ");
        printSingleCharResult(InternationalChar.getModifiers43(), "ModifierLetters");
        printSingleCharResult(ignorableChars, "IgnorableChars");

        for (int i = 0; i < basCombingingChars.size(); i++)
        {
            int[] alpha = basCombingingChars.get(i);

            printSingleCharResult(alpha, "combingingBas" + (char)('A' + i));
        }

        for (int i = 0; i < extCombingingChars.size(); i++)
        {
            int[] alpha = extCombingingChars.get(i);

            printSingleCharResult(alpha, "combingingExt" + (char)('A' + i));
        }

        printDoubleCharsResult(combingingChars, "combingingChar");
    }

    private static void printSingleCharResult(int[] codePoints, String name) throws IOException
    {
        System.out.print(name + "\t" + codePoints.length + "\t");

        statBlockAndScriptCount(codePoints);

        System.out.print("\t");

        statBlockWithScriptCount(codePoints);

        System.out.println();
    }

    private static void printDoubleCharsResult(List<int[][]> codePoints, String name) throws IOException
    {
        for (int i = 0; i < codePoints.size(); i++)
        {
            int[][] codePoint = codePoints.get(i);

            System.out.print(name + (char)('A' + i) + "\t" + codePoint.length + "\t");

            calcBlockAndScriptCount(codePoint);

            System.out.print("\t");

            calcBlockWithScriptCount(codePoint);

            System.out.println();
        }
    }

    private static void statBlockAndScriptCount(int[] codePoints) throws IOException
    {
        HashMap<String, Integer> blockCount = new HashMap<>();
        HashMap<String, Integer> scriptCount = new HashMap<>();

        for (int i = 0; i < codePoints.length; i++)
        {
            int codePoint = codePoints[i];

            String blockName = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint);

            if (!blockCount.containsKey(blockName))
                blockCount.put(blockName, 1);
            else
                blockCount.put(blockName, blockCount.get(blockName) + 1);

            String scriptName = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint);

            if (!scriptCount.containsKey(scriptName))
                scriptCount.put(scriptName, 1);
            else
                scriptCount.put(scriptName, scriptCount.get(scriptName) + 1);
        }

        System.out.print(blockCount.size() + "\t" + scriptCount.size());
    }

    private static void statBlockWithScriptCount(int[] codePoints) throws IOException
    {
        HashMap<String, Integer> blockWithScriptCount = new HashMap<>();

        for (int i = 0; i < codePoints.length; i++)
        {
            int codePoint = codePoints[i];

            String blockName = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint);
            String scriptName = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint);
            String key = blockName + "," + scriptName;

            if (!blockWithScriptCount.containsKey(key))
                blockWithScriptCount.put(key, 1);
            else
                blockWithScriptCount.put(key, blockWithScriptCount.get(key) + 1);
        }

        System.out.print(blockWithScriptCount.size());
    }

    private static void calcBlockAndScriptCount(int[][] codePoint) throws IOException
    {
        HashMap<String, Integer> blockCount = new HashMap<>();
        HashMap<String, Integer> scriptCount = new HashMap<>();

        for (int i = 0; i < codePoint.length; i++)
        {
            int codePoint1 = codePoint[i][0];
            int codePoint2 = codePoint[i][1];

            String blockName1 = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint1);
            String blockName2 = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint2);
            String blockName = blockName1 + "," + blockName2;

            if (!blockCount.containsKey(blockName))
                blockCount.put(blockName, 1);
            else
                blockCount.put(blockName, blockCount.get(blockName) + 1);

            String scriptName1 = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint1);
            String scriptName2 = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint2);
            String scriptName = scriptName1 + "," + scriptName2;

            if (!scriptCount.containsKey(scriptName))
                scriptCount.put(scriptName, 1);
            else
                scriptCount.put(scriptName, scriptCount.get(scriptName) + 1);
        }

        System.out.print(blockCount.size() + "\t" + scriptCount.size());
    }

    private static void calcBlockWithScriptCount(int[][] codePoints) throws IOException
    {
        HashMap<String, Integer> blockWithScriptCount = new HashMap<>();

        for (int i = 0; i < codePoints.length; i++)
        {
            int codePoint1 = codePoints[i][0];
            int codePoint2 = codePoints[i][1];

            String blockNumber1 = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint1);
            String blockNumber2 = UnicodeStandard.getBlockNameUsingJavaImpl(codePoint2);

            String scriptName1 = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint1);
            String scriptName2 = UnicodeStandard.getScriptNameUsingJavaImpl(codePoint2);

            String key = blockNumber1 + "," + blockNumber2 + "," + scriptName1 + "," + scriptName2;

            if (!blockWithScriptCount.containsKey(key))
                blockWithScriptCount.put(key, 1);
            else
                blockWithScriptCount.put(key, blockWithScriptCount.get(key) + 1);
        }

        System.out.print(blockWithScriptCount.size());
    }
}