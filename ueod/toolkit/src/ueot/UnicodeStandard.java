
package ueot;

import java.io.*;
import java.util.*;
import java.lang.Character.*;

public class UnicodeStandard
{
    private final static String DATA_PATH = "../ueot/data/";

    private final Long[] blockRange;
    private final int[] codePoints = null;
    private final String[] codePointDescs = null;

    private final static HashSet<String> languageBlockName = new HashSet<String>()
    {
        private static final long serialVersionUID = 1L;
        {
            add("ALPHABETIC_PRESENTATION_FORMS");
            add("ARABIC");
            add("ARABIC");
            add("ARABIC");
            add("ARABIC_EXTENDED_A");
            add("ARABIC_EXTENDED_A");
            add("ARABIC_PRESENTATION_FORMS_A");
            add("ARABIC_PRESENTATION_FORMS_A");
            add("ARABIC_PRESENTATION_FORMS_B");
            add("ARABIC_PRESENTATION_FORMS_B");
            add("ARMENIAN");
            add("ARMENIAN");
            add("BALINESE");
            add("BAMUM");
            add("BATAK");
            add("BENGALI");
            add("BENGALI");
            add("BOPOMOFO");
            add("BOPOMOFO_EXTENDED");
            add("BUGINESE");
            add("BUHID");
            add("CHAM");
            add("CHEROKEE");
            add("CJK_COMPATIBILITY_IDEOGRAPHS");
            add("CJK_COMPATIBILITY_IDEOGRAPHS");
            add("CJK_RADICALS_SUPPLEMENT");
            add("CJK_STROKES");
            add("CJK_UNIFIED_IDEOGRAPHS");
            add("CJK_UNIFIED_IDEOGRAPHS");
            add("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A");
            add("COMMON_INDIC_NUMBER_FORMS");
            add("COMMON_INDIC_NUMBER_FORMS");
            add("COPTIC");
            add("COPTIC");
            add("CYRILLIC_EXTENDED_B");
            add("CYRILLIC_EXTENDED_B");
            add("CYRILLIC_SUPPLEMENTARY");
            add("CYRILLIC_SUPPLEMENTARY");
            add("DEVANAGARI");
            add("DEVANAGARI");
            add("DEVANAGARI_EXTENDED");
            add("DEVANAGARI_EXTENDED");
            add("ENCLOSED_CJK_LETTERS_AND_MONTHS");
            add("ENCLOSED_CJK_LETTERS_AND_MONTHS");
            add("ETHIOPIC");
            add("ETHIOPIC_EXTENDED");
            add("ETHIOPIC_EXTENDED_A");
            add("ETHIOPIC_SUPPLEMENT");
            add("GEORGIAN");
            add("GEORGIAN");
            add("GEORGIAN_SUPPLEMENT");
            add("GEORGIAN_SUPPLEMENT");
            add("GLAGOLITIC");
            add("GREEK");
            add("GREEK_EXTENDED");
            add("GUJARATI");
            add("GUJARATI");
            add("GURMUKHI");
            add("HALFWIDTH_AND_FULLWIDTH_FORMS");
            add("HANGUL_COMPATIBILITY_JAMO");
            add("HANGUL_JAMO_EXTENDED_A");
            add("HANGUL_JAMO_EXTENDED_B");
            add("HANGUL_SYLLABLES");
            add("HANUNOO");
            add("HEBREW");
            add("HIRAGANA");
            add("IDEOGRAPHIC_DESCRIPTION_CHARACTERS");
            add("JAVANESE");
            add("KANBUN");
            add("KANGXI_RADICALS");
            add("KANNADA");
            add("KHMER");
            add("LAO");
            add("LAO");
            add("LATIN_1_SUPPLEMENT");
            add("LATIN_EXTENDED_D");
            add("LATIN_EXTENDED_D");
            add("LEPCHA");
            add("LIMBU");
            add("MALAYALAM");
            add("MANDAIC");
            add("MEETEI_MAYEK");
            add("MEETEI_MAYEK_EXTENSIONS");
            add("MEETEI_MAYEK_EXTENSIONS");
            add("MONGOLIAN");
            add("MONGOLIAN");
            add("MYANMAR_EXTENDED_A");
            add("NEW_TAI_LUE");
            add("NKO");
            add("OGHAM");
            add("ORIYA");
            add("PHAGS_PA");
            add("REJANG");
            add("RUNIC");
            add("SAMARITAN");
            add("SAURASHTRA");
            add("SINHALA");
            add("SMALL_FORM_VARIANTS");
            add("SUNDANESE");
            add("SUNDANESE_SUPPLEMENT");
            add("SUNDANESE_SUPPLEMENT");
            add("SYLOTI_NAGRI");
            add("SYRIAC");
            add("TAGALOG");
            add("TAGBANWA");
            add("TAI_LE");
            add("TAI_THAM");
            add("TAI_VIET");
            add("TAMIL");
            add("TELUGU");
            add("THAANA");
            add("THAI");
            add("TIBETAN");
            add("TIFINAGH");
            add("TIFINAGH");
            add("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED");
            add("VAI");
            add("VEDIC_EXTENSIONS");
            add("VEDIC_EXTENSIONS");
            add("VEDIC_EXTENSIONS");
            add("VERTICAL_FORMS");
            add("YI_RADICALS");
            add("YI_SYLLABLES");
        }
    };

    public UnicodeStandard() throws IOException
    {
        blockRange = readBlockRange(false);
    }

    public static String getBlockNameUsingJavaImpl(int codePoint)
    {
        UnicodeBlock block = Character.UnicodeBlock.of(codePoint);

        return block == null ? "No_Block" : block.toString();
    }

    public static String getScriptNameUsingJavaImpl(int codePoint)
    {
        UnicodeScript script = UnicodeScript.of(codePoint);

        return script.name();
    }

    public Long[] getBlockRange()
    {
        return blockRange;
    }

    /**
     * Get block number of the specified code point.
     */
    public int getBlockNumber(int codePoint)
    {
        for (int i = 0; i < blockRange.length; i++)
        {
            if (blockRange[i] > codePoint)
                return i;
        }

        return blockRange.length;
    }

    /**
     * Read code point block range.
     */
    private static Long[] readBlockRange(boolean printLog) throws IOException
    {
        ArrayList<Long> blocks = new ArrayList<>();

        String content = Util.readTextFile(DATA_PATH + "ubd.txt");
        String[] lines = content.split("\n");

        long last = -1;

        for (int i = 0; i < lines.length; i++)
        {
            String block = lines[i].split(";")[0];
            String[] range = block.split("\\.\\.");

            long start = Util.fromHexadecimal(range[0]);
            long end = Util.fromHexadecimal(range[1]);

            if (printLog && (start != last + 1))
                System.out.println("No_Block: " + Util.toHexadecimal(last + 1) + ".." + Util.toHexadecimal(start - 1));

            blocks.add((Long)start);
            last = end;
        }

        return blocks.toArray(new Long[blocks.size()]);
    }

    public boolean isAssignedCodePoint(int codePoint)
    {
        return Util.binarySearch(codePoints, codePoint) != -1;
    }

    public String getCodePointDescription(int codePoint)
    {
        int idx = Util.binarySearch(codePoints, codePoint);

        if (idx == -1)
            return null;

        return codePointDescs[idx];
    }

    @SuppressWarnings("unchecked")
    public HashSet<String> getLanguageBlockName()
    {
        return (HashSet<String>)languageBlockName.clone();
    }
}