
package ueot;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Util
{
    public static String toHexadecimal(final long number)
    {
        return Long.toHexString(number);
    }

    public static long fromHexadecimal(final String number)
    {
        return Long.valueOf(number, 16);
    }

    public static int c2i(Object object)
    {
        return object == null ? 0 : Integer.parseInt(object.toString());
    }

    public static String c2s(Object object)
    {
        return String.valueOf(object);
    }

    public static double log2n(double pij)
    {
        return Math.log(pij) / Math.log(2);
    }

    public static int min(int ... xs)
    {
        int min = xs[0];

        for (int i = 0; i < xs.length; i++)
            if (xs[i] < min)
                min = xs[i];

        return min;
    }

    public static double min(double ... xs)
    {
        double min = xs[0];

        for (int i = 0; i < xs.length; i++)
            if (xs[i] < min)
                min = xs[i];

        return min;
    }

    public static int binarySearch(int[] array, int target)
    {
        int left = 0;
        int right = array.length - 1;

        while (left <= right)
        {
            int mid = left + (right - left) / 2;

            if (array[mid] == target)
                return mid;
            else if (array[mid] < target)
                left = mid + 1;
            else
                right = mid - 1;
        }

        return -1;
    }

    public static char[] listToCharArray(List<Character> list)
    {
        char[] charArray = new char[list.size()];

        for (int i = 0; i < list.size(); i++)
            charArray[i] = list.get(i);

        return charArray;
    }

    public static String intArrayToString(int[] array)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < array.length; i++)
        {
            if (i != 0)
                builder.append(",");

            builder.append(array[i]);
        }

        return builder.toString();
    }

    public static int[] charArrayToIntArray(char[] chs)
    {
        int[] ints = new int[chs.length];

        for (int p = 0; p < chs.length; p++)
            ints[p] = chs[p];

        return ints;
    }

    public static int[] stringToIntArray(String str)
    {
        String content = str.substring(1, str.length() - 1);

        if (content.isEmpty())
            return new int[0];

        String[] parts = content.split(",\\s*");

        int[] result = new int[parts.length];

        for (int i = 0; i < parts.length; i++)
            result[i] = Integer.parseInt(parts[i].trim());

        return result;
    }

    public static Character[] stringToLatinArray(String str)
    {
        Character[] charArray = new Character[str.length()];

        for (int i = 0; i < str.length(); i++)
            charArray[i] = str.charAt(i);

        return charArray;
    }

    public static Integer[][] stringToUnicodeArray(String str)
    {
        IntStream stream = str.codePoints();
        int[] codePoints = stream.toArray();

        List<Integer[]> results = new ArrayList<>();
        List<Integer> lastChars = new ArrayList<>();

        for (int i = 0; i < codePoints.length; i++)
        {
            int type = Character.getType(codePoints[i]);

            if (type != Character.NON_SPACING_MARK)
            {
                if (!lastChars.isEmpty())
                {
                    Integer[] chs = lastChars.toArray(new Integer[lastChars.size()]);

                    results.add(chs);

                    lastChars.clear();
                }
            }

            lastChars.add(codePoints[i]);
        }

        if (!lastChars.isEmpty())
        {
            Integer[] chs = lastChars.toArray(new Integer[lastChars.size()]);

            results.add(chs);
        }

        return results.toArray(new Integer[results.size()][]);
    }

    public static String getJavaVersion()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Java version: " + System.getProperty("java.version") + "\n");
        builder.append("Java vendor: " + System.getProperty("java.vendor") + "\n");
        builder.append("JVM version: " + System.getProperty("java.vm.version") + "\n");
        builder.append("JVM name: " + System.getProperty("java.vm.name") + "\n");
        builder.append("Java runtime: " + Runtime.version() + "\n");

        return builder.toString();
    }

    public static String readTextFile(String filePath) throws IOException
    {
        return readTextFile(filePath, null, null, null);
    }

    public static String readTextFile(String filePath, Integer fromLine, Integer readLines, String charset) throws IOException
    {
        File file = new File(filePath);
        FileInputStream stream = new FileInputStream(file);

        return readTextFile(stream, fromLine, readLines, charset);
    }

    private static String readTextFile(InputStream fileStream, Integer fromLine, Integer readLines, String charset) throws IOException
    {
        StringBuilder buff = new StringBuilder();
        InputStreamReader reader = charset == null ? new InputStreamReader(fileStream) : new InputStreamReader(fileStream, charset);
        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        // skip from lines
        if (fromLine != null)
        {
            int counter = fromLine;

            while (--counter > 0)
                lineNumberReader.readLine();
        }

        int count = 1;

        for (String line; null != (line = lineNumberReader.readLine()); count++)
        {
            buff.append(line);
            buff.append("\n");

            if (readLines != null && readLines == count)
                break;
        }

        lineNumberReader.close();

        return buff.toString();
    }
}