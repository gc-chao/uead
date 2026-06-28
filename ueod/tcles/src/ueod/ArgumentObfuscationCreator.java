
package ueod;

import java.io.*;
import java.util.ArrayList;

import ueot.*;

public abstract class ArgumentObfuscationCreator
{
    public static class SampleAttribute
    {
        public String obfuscation;
        public String originalText;
        public String obfuscatedText;
    }

    private boolean isNeedToObfuscating(String[] arguments)
    {
        for (String argument : arguments)
            if (argument.contains("[") && argument.contains("]"))
                return true;

        return false;
    }

    private ArrayList<SampleAttribute[]> createNonObfuscationSamples(String[] arguments)
    {
        ArrayList<SampleAttribute[]> samples = new ArrayList<SampleAttribute[]>();

        ArrayList<SampleAttribute> sample = new ArrayList<>();

        for (String argument : arguments)
        {
            SampleAttribute attribute = new SampleAttribute();

            attribute.originalText = argument;
            attribute.obfuscatedText = argument;
            attribute.obfuscation = "S=0,I=0,C=0";

            sample.add(attribute);
        }

        samples.add(sample.toArray(new SampleAttribute[sample.size()]));

        return samples;
    }

    private SampleAttribute createObfuscatedSample(String obfuscation, String originalText, String obfuscatedText, boolean printResult)
    {
        SampleAttribute sample = new SampleAttribute();

        sample.obfuscation = obfuscation;
        sample.originalText = originalText.replace("[", "").replace("]", "");
        sample.obfuscatedText = obfuscatedText;

        if (printResult)
            System.out.println(sample.obfuscation + "," + sample.originalText + "," + sample.obfuscatedText);

        return sample;
    }

    protected abstract ArrayList<Pair<String, CommandObfuscator>> createObfuscators() throws IOException;

    public ArrayList<SampleAttribute[]> createCommandSamples(String[] originalArguments, int sampleCount, boolean printResult) throws IOException
    {
        ArrayList<SampleAttribute[]> results = new ArrayList<>();

        if (!isNeedToObfuscating(originalArguments))
            return createNonObfuscationSamples(originalArguments);

        ArrayList<Pair<String, CommandObfuscator>> commandObfuscators = createObfuscators();

        for (int i = 0; i < sampleCount; i++)
        {
            for (Pair<String, CommandObfuscator> pair : commandObfuscators)
            {
                CommandObfuscator obfuscator = pair.getValue();

                // obfuscate arguments
                String[] obfscatedArguments = obfuscator.encodingObfuscate(originalArguments);

                // sample array
                SampleAttribute[] samples = new SampleAttribute[originalArguments.length];

                for (int j = 0; j < originalArguments.length; j++)
                    samples[j] = createObfuscatedSample(pair.getKey(), originalArguments[j], obfscatedArguments[j], printResult);

                results.add(samples);
            }
        }

        return results;
    }
}