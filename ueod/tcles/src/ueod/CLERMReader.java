
package ueod;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import ueot.*;

public class CLERMReader implements ICharSimScore
{
    private final String DATA_PATH = "../ueot/data/";

    private final List<int[]> database;

    public CLERMReader() throws IOException
    {
        database = readFromFile(DATA_PATH + "CLERM_Lite.txt");
    }

    public boolean isEqual(char a, char b)
    {
        if (a == 0)
            return Util.binarySearch(database.get(26), b) > -1;
        else if (b == 0)
            return Util.binarySearch(database.get(26), a) > -1;

        if (a >= 'A' && a <= 'Z')
            a = (char)(a + 32);

        if (b >= 'A' && b <= 'Z')
            b = (char)(b + 32);

        for (int i = 0; i < 26; i++)
        {
            int[] codePoints = database.get(i);

            if (Util.binarySearch(codePoints, b) > -1)
                return (i + 'a') == a;
        }

        for (int i = 0; i < 26; i++)
        {
            int[] codePoints = database.get(i);

            if (Util.binarySearch(codePoints, a) > -1)
                return (i + 'a') == b;
        }

        return a == b;
    }

    @SuppressWarnings("resource")
    private List<int[]> readFromFile(String path) throws IOException
    {
        String line;
        List<int[]> result = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        while ((line = reader.readLine()) != null)
        {
            if (!line.trim().isEmpty())
            {
                String[] parts = line.split(",");

                Stream<String> lineStream = Arrays.stream(parts).map(String::trim);
                int[] codePoints = lineStream.mapToInt(Integer::parseInt).toArray();
                result.add(codePoints);
            }
        }

        return result;
    }
}