
package ueoq;

import java.io.*;
import java.util.*;

import ueot.*;

public class CommandObfuscationMetric
{
    private final TextObfuscationMetricEx textObfuscationMetric;

    public CommandObfuscationMetric(int ignorableVersion) throws IOException
    {
        this.textObfuscationMetric = new TextObfuscationMetricEx(ignorableVersion);
    }

    public String[] parseCommand(String command)
    {
        if (command == null || command.length() == 0)
            return new String[0];

        StringBuilder programName = new StringBuilder();

        int p = 0;

        p = parseProgramName(command, programName, p);

        if (p == command.length())
            return new String[] { programName.toString() };

        ReferenceBoolean inquote = new ReferenceBoolean(false);
        List<String> arguments = new ArrayList<String>();

        arguments.add(programName.toString());

        for (int M = 0; M < 32767; M++)
        {
            // skip space and tab
            while ((p < command.length()) && (command.charAt(p) == ' ' || command.charAt(p) == '\t'))
                p++;

            if (p >= command.length())
                break;

            // create new argument
            StringBuilder argument = new StringBuilder();

            p = parseArgument(command, argument, inquote, p);

            arguments.add(argument.toString());
        }

        return arguments.toArray(new String[arguments.size()]);
    }

    private static int parseProgramName(String command, StringBuilder programName, int p)
    {
        char c = 0;
        boolean inquote = false;

        do
        {
            if (command.charAt(p) == '^')
            {
                c = command.charAt(p++);
                continue;
            }

            // skip '"'
            if (command.charAt(p) == '"')
            {
                inquote = !inquote;
                c = command.charAt(p++);
                continue;
            }

            // copy program name
            c = command.charAt(p++);
            programName.append(c);
        }
        while ((p != command.length()) && (inquote || (c != ' ' && c != '\t')));

        if (c == ' ' || c == '\t')
            programName.deleteCharAt(programName.length() - 1);

        return p;
    }

    private static int parseArgument(String command, StringBuilder argument, ReferenceBoolean inquote, int p)
    {
        int numslash;
        boolean copychar;

        for (int N = 0; N < 32767; N++)
        {
            copychar = true;
            numslash = 0;

            if (p == command.length())
                break;

            // count number of backslashes for use below
            while (command.charAt(p) == '\\')
            {
                ++p;
                ++numslash;
            }

            if (command.charAt(p) == '"')
            {
                if (numslash % 2 == 0)
                {
                    if (inquote.value && (((p + 1) < command.length()) && command.charAt(p + 1) == '"'))
                        p++;
                    else
                    {
                        copychar = false;
                        inquote.value = !inquote.value;
                    }
                }

                numslash /= 2;
            }

            // copy slashes
            while ((numslash--) > 0)
                argument.append('\\');

            // if at end of arg, break loop
            if (!inquote.value && (command.charAt(p) == ' ' || command.charAt(p) == '\t'))
                break;

            if (command.charAt(p) == '^')
                copychar = false;

            // copy character into argument
            if (copychar)
                argument.append(command.charAt(p));

            ++p;
        }

        return p;
    }

    public double[] getArgumentIndexes(String argument)
    {
        return textObfuscationMetric.getIndexes(argument, false);
    }

    public double[][] getCommandIndexes(int L, String[] arguments, double beta)
    {
        final int INDEX_SIZE = 9;

        int LB = 0;

        double[] EOI = new double[INDEX_SIZE];

        LB += arguments.length - 1;

        for (int i = 0; i < arguments.length; i++)
        {
            String argument = arguments[i];

            LB += argument.length();

            double[] indexes = getArgumentIndexes(argument);

            for (int j = 0; j < INDEX_SIZE; j++)
                EOI[j] += indexes[j + 7];
        }

        double SOI_ALL = (L - LB) / (double)L;

        double[] SOM = new double[INDEX_SIZE];
        double[] AOI = new double[INDEX_SIZE];
        double[] COI = new double[INDEX_SIZE];

        for (int j = 0; j < INDEX_SIZE; j++)
        {
            SOM[j] = SOI_ALL;
            AOI[j] = (1 / (double)arguments.length) * EOI[j];
            COI[j] = beta * SOM[j] + (1 - beta) * AOI[j];
        }

        return new double[][] { SOM, AOI, COI };
    }
}