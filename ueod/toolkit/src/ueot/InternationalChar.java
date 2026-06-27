
package ueot;

import java.io.*;
import java.util.*;

public class InternationalChar
{
    private static String DATA_PATH = "../ueot/data/";

    private static int[] ignoranceChars7 = null;
    private static int[] ignoranceChars7l = null;
    private static int[] ignoranceChars10 = null;
    private static int[] ignoranceChars11 = null;
    private static int[] equivalentChars1 = null;
    private static List<int[][]> equivalentChars2 = null;
    private static List<int[][]> equivalentChars3 = null;
    private static List<int[][]> equivalentChars4 = null;

    public static int[] getLatin()
    {
        int[] chars = new int[26 * 2];

        for (int i = 0; i < chars.length / 2; i++)
        {
            chars[i * 2] = 'a' + i;
            chars[i * 2 + 1] = 'A' + i;
        }

        return chars;
    }

    public static int[] getHans52()
    {
        String letters = "Ａ, Ｂ, Ｃ, Ｄ, Ｅ, Ｆ, Ｇ, Ｈ, Ｉ, Ｊ, Ｋ, Ｌ, Ｍ, Ｎ, Ｏ, Ｐ, Ｑ, Ｒ, Ｓ, Ｔ, Ｕ, Ｖ, Ｗ, Ｘ, Ｙ, Ｚ,ａ, ｂ, ｃ, ｄ, ｅ, ｆ, ｇ, ｈ, ｉ, ｊ, ｋ, ｌ, ｍ, ｎ, ｏ, ｐ, ｑ, ｒ, ｓ, ｔ, ｕ, ｖ, ｗ, ｘ, ｙ, ｚ";

        return letterStringToCodePoints(letters);
    }

    public static int[] getCyrillic33()
    {
        String letters = "А, Б, В, Г, Д, Е, Ё, Ж, З, И, Й, К, Л, М, Н, О, П, Р, С, Т, У, Ф, Х, Ц, Ч, Ш, Щ, Ъ, Ы, Ь, Э, Ю, Я";

        return letterStringToCodePoints(letters);
    }

    public static int[] getGreek24()
    {
        String letters = "Α,Β,Γ,Δ,Ε,Ζ,Η,Θ,Ι,Κ,Λ,Μ,Ν,Ξ,Ο,Π,Ρ,Σ,Τ,Υ,Φ,Χ,Ψ,Ω";

        return letterStringToCodePoints(letters);
    }

    public static int[] getGematria22()
    {
        String letters = "א, ב, ג, ד, ה, ו, ז, ח, ט, י, כ, ל, מ, נ, ס, ע, פ, צ, ק, ר, ש, תs";

        return letterStringToCodePoints(letters);
    }

    public static int[] getArabic28()
    {
        String letters = "ا, ب, ت, ث, ج, ح, خ, د, ذ, ر, ز, س, ش, ص, ض, ط, ظ, ع, غ, ف, ق, ك, ل, م, ن, ه, و, ي";

        return letterStringToCodePoints(letters);
    }

    public static int[] getHangul40()
    {
        String letters = "ㄱ, ㄴ, ㄷ, ㄹ, ㅁ, ㅂ, ㅅ, ㅇ, ㅈ, ㅊ, ㅋ, ㅌ, ㅍ, ㅎ,ㅏ, ㅑ, ㅓ, ㅕ, ㅗ, ㅛ, ㅜ, ㅠ, ㅡ, ㅣ,ㄲ, ㄸ, ㅃ, ㅆ, ㅉ, ㅐ, ㅒ, ㅔ, ㅖ, ㅘ, ㅙ, ㅚ, ㅝ, ㅞ, ㅟ, ㅢ";

        return letterStringToCodePoints(letters);
    }

    public static int[] getThai76()
    {
        String letters = "ก, ข, ฃ, ค, ฅ, ฆ, ง, จ, ฉ, ช, ซ, ฌ, ญ, ฎ, ฏ, ฐ, ฑ, ฒ, ณ, ด, ต, ถ, ท, ธ, น, บ, ป, ผ, ฝ, พ, ฟ, ภ, ม, ย, ร, ล, ว, ศ, ษ, ส, ห, ฬ, อ, ฮ,ะ, า, ิ, ี, ึ, ื, ุ, ู, เ, โ, ใ, ไ";

        return letterStringToCodePoints(letters);
    }

    public static int[] getModifiers43() throws IOException
    {
        return getSubstitutableChars1();
    }

    public static int[] getIgnorableChars() throws IOException
    {
        return getIgnorableChars(10);
    }

    public static int[] getIgnorableChars(int version) throws IOException
    {
        if (version == 7)
        {
            if (ignoranceChars7 == null)
                ignoranceChars7 = readIgnorableChars("7");

            return ignoranceChars7;
        }
        else if (version == 71)
        {
            if (ignoranceChars7l == null)
                ignoranceChars7l = readIgnorableChars("7l");

            return ignoranceChars7l;
        }
        else if (version == 11)
        {
            if (ignoranceChars11 == null)
                ignoranceChars11 = readIgnorableChars("11");

            return ignoranceChars11;
        }
        else
        {
            if (ignoranceChars10 == null)
                ignoranceChars10 = readIgnorableChars("10");

            return ignoranceChars10;
        }
    }

    public synchronized static int[] getSubstitutableChars1() throws IOException
    {
        if (equivalentChars1 == null)
            equivalentChars1 = readSubstitutableChars1();

        return equivalentChars1;
    }

    public synchronized static List<int[][]> getSubstitutableChars2() throws IOException
    {
        if (equivalentChars2 == null)
            equivalentChars2 = readSubstitutableCharsK(2);

        return equivalentChars2;
    }

    public synchronized static List<int[][]> getSubstitutableChars3() throws IOException
    {
        if (equivalentChars3 == null)
            equivalentChars3 = readSubstitutableCharsK(3);

        return equivalentChars3;
    }

    public synchronized static List<int[][]> getSubstitutableChars4() throws IOException
    {
        if (equivalentChars4 == null)
            equivalentChars4 = readSubstitutableCharsK(4);

        return equivalentChars4;
    }

    public static List<int[]> getBasicPartOfSubstitutableChars() throws IOException
    {
        List<int[]> results = new ArrayList<int[]>();
        List<int[][]> charsEquals2 = getSubstitutableChars2();

        for (int[][] alpha : charsEquals2)
        {
            List<Integer> chars = new ArrayList<>();

            for (int i = 0; i < alpha.length; i++)
                if (!chars.contains(alpha[i][0]))
                    chars.add(alpha[i][0]);

            results.add(chars.stream().mapToInt(Integer::intValue).toArray());
        }

        return results;
    }

    public static List<int[]> getExtPartOfSubstitutableChars() throws IOException
    {
        List<int[]> results = new ArrayList<int[]>();
        List<int[][]> charsEquals2 = getSubstitutableChars2();

        for (int[][] alpha : charsEquals2)
        {
            HashSet<Integer> chars = new HashSet<>();

            for (int i = 0; i < alpha.length; i++)
                chars.add(alpha[i][1]);

            results.add(chars.stream().mapToInt(Integer::intValue).toArray());
        }

        return results;
    }

    public static HashMap<Character, Character> getModifierLetterMap() throws IOException
    {
        HashMap<Character, Character> modifierToLatin = new HashMap<>();
        String content = Util.readTextFile(DATA_PATH + "substitutable_1c_win10.txt");

        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            String[] chs = lines[i].split(",");

            for (int j = 2; j < chs.length; j++)
                modifierToLatin.put(chs[j].charAt(0), chs[0].charAt(0));
        }

        return modifierToLatin;
    }

    public static HashMap<Character, Character> getHansMap() throws IOException
    {
        HashMap<Character, Character> map = new HashMap<>();

        String fullWidth = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";
        String halfWidth = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < fullWidth.length(); i++)
            map.put(fullWidth.charAt(i), halfWidth.charAt(i));

        return map;
    }

    public static HashMap<Character, Character> getCyrillicMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('А', 'A'); map.put('Б', 'B'); map.put('В', 'V'); map.put('Г', 'G');
        map.put('Д', 'D'); map.put('Е', 'E'); map.put('Ё', 'E'); map.put('Ж', 'Z');
        map.put('З', 'Z'); map.put('И', 'I'); map.put('Й', 'Y'); map.put('К', 'K');
        map.put('Л', 'L'); map.put('М', 'M'); map.put('Н', 'N'); map.put('О', 'O');
        map.put('П', 'P'); map.put('Р', 'R'); map.put('С', 'S'); map.put('Т', 'T');
        map.put('У', 'U'); map.put('Ф', 'F'); map.put('Х', 'H'); map.put('Ц', 'T');
        map.put('Ч', 'C'); map.put('Ш', 'S'); map.put('Щ', 'S'); map.put('Ъ', ' ');
        map.put('Ы', 'Y'); map.put('Ь', ' '); map.put('Э', 'E'); map.put('Ю', 'Y');
        map.put('Я', 'Y');

        return map;
    }

    public static HashMap<Character, Character> getGreekMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('Α', 'A'); map.put('Β', 'B'); map.put('Γ', 'G'); map.put('Δ', 'D');
        map.put('Ε', 'E'); map.put('Ζ', 'Z'); map.put('Η', 'E'); map.put('Θ', 'T');
        map.put('Ι', 'I'); map.put('Κ', 'K'); map.put('Λ', 'L'); map.put('Μ', 'M');
        map.put('Ν', 'N'); map.put('Ξ', 'X'); map.put('Ο', 'O'); map.put('Π', 'P');
        map.put('Ρ', 'R'); map.put('Σ', 'S'); map.put('Τ', 'T'); map.put('Υ', 'Y');
        map.put('Φ', 'F'); map.put('Χ', 'H'); map.put('Ψ', 'P'); map.put('Ω', 'O');

        return map;
    }

    public static HashMap<Character, Character> getHebrewMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('א', 'A'); map.put('ב', 'B'); map.put('ג', 'G'); map.put('ד', 'D');
        map.put('ה', 'H'); map.put('ו', 'V'); map.put('ז', 'Z'); map.put('ח', 'H');
        map.put('ט', 'T'); map.put('י', 'Y'); map.put('כ', 'K'); map.put('ל', 'L');
        map.put('מ', 'M'); map.put('נ', 'N'); map.put('ס', 'S'); map.put('ע', 'A');
        map.put('פ', 'P'); map.put('צ', 'T'); map.put('ק', 'K'); map.put('ר', 'R');
        map.put('ש', 'S'); map.put('ת', 'T');

        return map;
    }

    public static HashMap<Character, Character> getArabicMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('ا', 'A'); map.put('ب', 'B'); map.put('ت', 'T'); map.put('ث', 'T');
        map.put('ج', 'J'); map.put('ح', 'H'); map.put('خ', 'K'); map.put('د', 'D');
        map.put('ذ', 'D'); map.put('ر', 'R'); map.put('ز', 'Z'); map.put('س', 'S');
        map.put('ش', 'S'); map.put('ص', 'S'); map.put('ض', 'D'); map.put('ط', 'T');
        map.put('ظ', 'Z'); map.put('ع', 'A'); map.put('غ', 'G'); map.put('ف', 'F');
        map.put('ق', 'Q'); map.put('ك', 'K'); map.put('ل', 'L'); map.put('م', 'M');
        map.put('ن', 'N'); map.put('ه', 'H'); map.put('و', 'W'); map.put('ي', 'Y');

        return map;
    }

    public static HashMap<Character, Character> getKoreanMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('ㄱ', 'G'); map.put('ㄴ', 'N'); map.put('ㄷ', 'D'); map.put('ㄹ', 'R');
        map.put('ㅁ', 'M'); map.put('ㅂ', 'B'); map.put('ㅅ', 'S'); map.put('ㅇ', ' ');
        map.put('ㅈ', 'J'); map.put('ㅊ', 'C'); map.put('ㅋ', 'K'); map.put('ㅌ', 'T');
        map.put('ㅍ', 'P'); map.put('ㅎ', 'H'); map.put('ㄲ', 'K'); map.put('ㄸ', 'T');
        map.put('ㅃ', 'P'); map.put('ㅆ', 'S'); map.put('ㅉ', 'J');
        
        map.put('ㅏ', 'A'); map.put('ㅑ', 'Y'); map.put('ㅓ', 'E'); map.put('ㅕ', 'Y');
        map.put('ㅗ', 'O'); map.put('ㅛ', 'Y'); map.put('ㅜ', 'U'); map.put('ㅠ', 'Y');
        map.put('ㅡ', 'E'); map.put('ㅣ', 'I'); map.put('ㅐ', 'A'); map.put('ㅒ', 'Y');
        map.put('ㅔ', 'E'); map.put('ㅖ', 'Y'); map.put('ㅘ', 'W'); map.put('ㅙ', 'W');
        map.put('ㅚ', 'O'); map.put('ㅝ', 'W'); map.put('ㅞ', 'W'); map.put('ㅟ', 'W');
        map.put('ㅢ', 'E');

        return map;
    }

    public static HashMap<Character, Character> getThaiMap()
    {
        HashMap<Character, Character> map = new HashMap<>();

        map.put('ก', 'K'); map.put('ข', 'K'); map.put('ฃ', 'K'); map.put('ค', 'K');
        map.put('ฅ', 'K'); map.put('ฆ', 'K'); map.put('ง', 'N'); map.put('จ', 'C');
        map.put('ฉ', 'C'); map.put('ช', 'C'); map.put('ซ', 'S'); map.put('ฌ', 'C');
        map.put('ญ', 'Y'); map.put('ฎ', 'D'); map.put('ฏ', 'T'); map.put('ฐ', 'T');
        map.put('ฑ', 'T'); map.put('ฒ', 'T'); map.put('ณ', 'N'); map.put('ด', 'D');
        map.put('ต', 'T'); map.put('ถ', 'T'); map.put('ท', 'T'); map.put('ธ', 'T');
        map.put('น', 'N'); map.put('บ', 'B'); map.put('ป', 'P'); map.put('ผ', 'P');
        map.put('ฝ', 'F'); map.put('พ', 'P'); map.put('ฟ', 'F'); map.put('ภ', 'P');
        map.put('ม', 'M'); map.put('ย', 'Y'); map.put('ร', 'R'); map.put('ล', 'L');
        map.put('ว', 'W'); map.put('ศ', 'S'); map.put('ษ', 'S'); map.put('ส', 'S');
        map.put('ห', 'H'); map.put('ฬ', 'L'); map.put('อ', ' '); map.put('ฮ', 'H');
        
        map.put('ะ', 'A'); map.put('า', 'A'); map.put('ิ', 'I'); map.put('ี', 'I');
        map.put('ึ', 'U'); map.put('ื', 'U'); map.put('ุ', 'U'); map.put('ู', 'U');
        map.put('เ', 'E'); map.put('โ', 'O'); map.put('ใ', 'A'); map.put('ไ', 'A');

        return map;
    }

    private static int[] letterStringToCodePoints(String letters)
    {
        String[] chars = letters.replace(" ", "").split(",");

        int[] results = new int[chars.length];

        for (int i = 0; i < chars.length; i++)
            results[i] = Character.valueOf(chars[i].charAt(0));

        return results;
    }

    private static int[] readIgnorableChars(String postfix) throws IOException
    {
        String content = Util.readTextFile(DATA_PATH + "ignorable_i_win" + postfix + ".txt");

        String[] lines = content.split("\n");

        int[] codePoints = new int[lines.length];

        for (int i = 0; i < lines.length; i++)
            codePoints[i] = Util.c2i(lines[i]);

        return codePoints;
    }

    private static int[] readSubstitutableChars1() throws IOException
    {
        String content = Util.readTextFile(DATA_PATH + "substitutable_1c_win10.txt");

        String[] lines = content.split("\n");

        int ptr = 0;
        int[] codePoints = new int[43];

        for (int i = 0; i < lines.length; i++)
        {
            String[] letters = lines[i].split(",");

            for (int j = 2; j < letters.length; j++)
                codePoints[ptr++] = (int)letters[j].charAt(0);
        }

        return codePoints;
    }

    private static List<int[][]> readSubstitutableCharsK(int codePointCount) throws IOException
    {
        String content = Util.readTextFile(DATA_PATH + "substitutable_" + codePointCount + "i_win10.txt");

        String[] lines = content.split("\n");

        List<int[][]> allCodePoints = new ArrayList<int[][]>();

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i].substring(2);
            String[] combinations = line.split(";");

            int[][] codePoints = new int[combinations.length][codePointCount];

            for (int j = 0; j < combinations.length; j++)
            {
                String[] chars = combinations[j].split(",");

                for (int k = 0; k < codePointCount; k++)
                    codePoints[j][k] = Util.c2i(chars[k]);
            }

            allCodePoints.add(codePoints);
        }

        return allCodePoints;
    }
}