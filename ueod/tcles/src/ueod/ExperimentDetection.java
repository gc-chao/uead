
package ueod;

import java.io.*;
import java.util.*;

import ueod.TCLESDetection.CommandContext;
import ueod.ArgumentObfuscationCreator.SampleAttribute;

public class ExperimentDetection
{
    private final static String DATASET_PATH = "../dataset/";

    private static int MAX_UNKNOWN = 20;

    private static int SAMPLING_COUNT = 5;

    public static class CommandInput
    {
        public String rule;
        public String command;
        public int label;

        public CommandInput(String ruleSet, String command, int label)
        {
            this.rule = ruleSet;
            this.command = command;
            this.label = label;
        }
    }
    
    public static void main(String[] args) throws IOException
    {
        System.out.println("Detecting Obfuscated Command Samples1 (Win10)...");
        makeROCCurve(10, TextEncodingEquivalentMetric.LEUCM_DATABASE);

        System.out.println("Detecting Obfuscated Command Samples2 (Win11)...");
        makeROCCurve(11, TextEncodingEquivalentMetric.LEUCM_DATABASE);

        System.out.println("Robustness Testing...");

        makeRobustnessCurve(71, TextEncodingEquivalentMetric.LEUCM_DATABASE);
    }

    private static void makeROCCurve(int obfuscationVersion, int metricVersion) throws IOException
    {
        CommandContext[] commands = readDatasetFromFile(obfuscationVersion);

        TCLESDetection.calculateCommandScores(metricVersion, commands, false);

        List<ROCPoint> rocPoints1 = TCLESDetection.computeROCPoints(commands, true);
        ROCPlotter tclesPlotter = new ROCPlotter(rocPoints1);
        tclesPlotter.plot();

        List<ROCPoint> rocPoints2 = TCLESDetection.computeROCPoints(commands, false);
        ROCPlotter regexPlotter = new ROCPlotter(rocPoints2);
        regexPlotter.plot();
    }

    private static void makeRobustnessCurve(int obfuscationVersion, int metricVersion) throws IOException
    {
        List<RobustnessCurvePoint> rcPoints = new ArrayList<>();

        for (int i = 0; i <= MAX_UNKNOWN; i++)
        {
            ArgumentObfuscationCreator creator = new RobustnessObfuscationCreator(obfuscationVersion, i, MAX_UNKNOWN);
            List<String> dataset = generateObfuscatedDataset(creator, SAMPLING_COUNT);
            CommandContext[] commands = TCLESDetection.deserializeCommands(dataset);

            TCLESDetection.calculateCommandScores(metricVersion, commands, false);

            RobustnessCurvePoint rcPoint = new RobustnessCurvePoint();

            int ration2Count = 0; int avgcount = 0;
            double unknownCharsOfCommandRatio = 0.0;
            double unknownCharsOfArgumentRatio = 0.0;

            for (CommandContext command : commands)
            {
                unknownCharsOfCommandRatio += (command.obfuscatedCommandLength - command.originalCommandLength) / (double)command.obfuscatedCommandLength;

                if (command.obfuscatedArgumentLength > 0)
                {
                    ration2Count++;
                    unknownCharsOfArgumentRatio += (command.obfuscatedArgumentLength - command.originalArgumentLength) / (double)command.obfuscatedArgumentLength;
                    avgcount += (command.obfuscatedArgumentLength - command.originalArgumentLength);
                }
            }

            rcPoint.unknownNumber = i;
            rcPoint.unknownOfCommandRatio = unknownCharsOfCommandRatio / commands.length;
            rcPoint.unknownOfArgumentRatio = unknownCharsOfArgumentRatio / ration2Count;
            rcPoint.avgUnkonwCount = avgcount / (float)ration2Count;

            List<ROCPoint> rocPoints = TCLESDetection.computeROCPoints(commands, true);

            for (ROCPoint rocPoint : rocPoints)
                if (rocPoint.accuracy > rcPoint.maxAcc)
                    rcPoint.maxAcc = rocPoint.accuracy;

            rcPoints.add(rcPoint);

            System.out.println("Point" + rcPoint.unknownNumber + ", AvgUnknownCharCount:" + rcPoint.avgUnkonwCount + ", maxAvgAcc: " + rcPoint.maxAcc + ", unknownOfCommandAvg: " + rcPoint.unknownOfCommandRatio + ", unknownOfArgumentAvg: " + rcPoint.unknownOfArgumentRatio);
        }

        RobustnessPlotter.showPlot(rcPoints, MAX_UNKNOWN);
    }

    private static List<String> generateObfuscatedDataset(ArgumentObfuscationCreator creator, int samplingCount) throws IOException
    {
        Set<String> outputs = new LinkedHashSet<>();

        ArrayList<CommandInput> inputs = getOriginalCommands();

        int inputSize = inputs.size();

        for (int i = 0; i < inputSize; i++)
        {
            System.out.print((int)(i * 100 / (double)inputSize) + "% ");

            CommandInput input = inputs.get(i);

            // generate obfuscated commands
            ArrayList<SampleAttribute[]> obfuscatedCommands = creator.createCommandSamples(input.command.split(" "), samplingCount, false);

            for (SampleAttribute[] obfuscatedCommand : obfuscatedCommands)
            {
                StringBuilder builder = new StringBuilder();

                for (SampleAttribute commandArgument : obfuscatedCommand)
                    builder.append(commandArgument.obfuscatedText + " ");

                builder.delete(builder.length() - 1, builder.length());

                outputs.add(TCLESDetection.serializeCommand(input.label, input.rule, input.command, obfuscatedCommand[0].obfuscation, builder.toString()));
            }
        }

        System.out.println();

        List<String> results = new ArrayList<>(outputs);
        Collections.shuffle(results, new Random(0));

        return results;
    }

    private static CommandContext[] readDatasetFromFile(int obfuscationVersion) throws IOException
    {
        String line;
        List<String> lines = new ArrayList<>();
        FileInputStream fis = new FileInputStream(DATASET_PATH + "obfuscated_commands_" + obfuscationVersion + ".txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-16"));

        while ((line = reader.readLine()) != null)
            lines.add(line);

        return TCLESDetection.deserializeCommands(lines);
    }

    private static ArrayList<CommandInput> getOriginalCommands()
    {
        ArrayList<CommandInput> originalCommands = new ArrayList<>();

        originalCommands.add(new CommandInput("reg,save,hklm\\sam", "reg [save] [hklm]\\sam 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,save,hklm\\system", "reg [save] [hklm]\\system 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,export,hklm\\sam", "reg [export] [hklm]\\sam 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,export,hklm\\system", "reg [export] [hklm]\\system 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,save,sam", "reg [save] [hklm] \\ sam 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,save,system", "reg [save] [hklm] \\ system 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,export,sam", "reg [export] [hklm] \\ sam 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,export,system", "reg [export] [hklm] \\ system 1.hiv", 1));
        originalCommands.add(new CommandInput("reg,save,hklm\\sam", "reg [query] [hklm]\\sam 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,hklm\\system", "reg [query] [hklm]\\system 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,hklm\\sam", "reg [query] [hklm]\\sam 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,hklm\\system", "reg [query] [hklm]\\system 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,sam", "reg [query] [hklm] \\ sam 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,system", "reg [query] [hklm] \\ system 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,sam", "reg [query] [hklm] \\ sam 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,system", "reg [query] [hklm] \\ system 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,hklm\\sam", "reg [save] [hklm]\\windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,hklm\\system", "reg [save] [hklm]\\windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,hklm\\sam", "reg [export] [hklm]\\windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,hklm\\system", "reg [export] [hklm]\\windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,sam", "reg [save] [hklm] \\ windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,save,system", "reg [save] [hklm] \\ windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,sam", "reg [export] [hklm] \\ windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,export,system", "reg [export] [hklm] \\ windows 1.hiv", 0));
        originalCommands.add(new CommandInput("reg,delete,run,/f", "reg [delete] [hklm] \\ software \\ microsoft \\ windows \\ currentversion \\ run /[f] /v test", 1));
        originalCommands.add(new CommandInput("reg,delete,run,/f", "reg [delete] [hklm] \\ software \\ microsoft \\ windows \\ currentversion\\ run /v test", 0));
        originalCommands.add(new CommandInput("certutil,-f", "certutil -[urlcache] -[split] -[f] http://www.test.com/xxx.exe", 1));
        originalCommands.add(new CommandInput("certutil,-f", "certutil -[urlcache] -[split] http://www.test.com/xxx.exe", 0));
        originalCommands.add(new CommandInput("certutil,quiet", "certutil -[quiet] -i file.msi", 1));
        originalCommands.add(new CommandInput("certutil,quiet", "certutil -[update] -i file.msi", 0));
        originalCommands.add(new CommandInput("certreq,post,config", "certreq -[get] -[config] https://www.example.org/file.ext C:\\Windows\\Temp\\file.ext", 0));
        originalCommands.add(new CommandInput("certreq,post,config", "certreq -[post] -[config] https://www.example.org/file.ext C:\\Windows\\Temp\\file.ext", 1));
        originalCommands.add(new CommandInput("cmstp,/ni,/s", "cmstp /[ni] /[s] C:\\Windows\\Temp\\file.inf", 1));
        originalCommands.add(new CommandInput("cmstp,/ni,/s", "cmstp /[nf] /[s] C:\\Windows\\Temp\\file.inf", 0));
        originalCommands.add(new CommandInput("taskkill,/im,/f,lsass", "taskkill /[im] lsass", 0));
        originalCommands.add(new CommandInput("taskkill,/im,/f,lsass", "taskkill /[f] /[im] lsass", 1));
        originalCommands.add(new CommandInput("taskkill,/im,winlogon", "taskkill /[im] notepad", 0));
        originalCommands.add(new CommandInput("taskkill,/im,winlogon", "taskkill /[im] winlogon", 1));
        originalCommands.add(new CommandInput("ipconfig,renew", "ipconfig /[renew]", 1));
        originalCommands.add(new CommandInput("ipconfig,renew", "ipconfig /[all]", 0));
        originalCommands.add(new CommandInput("whoami,all", "whoami /[user]", 0));
        originalCommands.add(new CommandInput("whoami,all", "whoami /[all]", 1));
        originalCommands.add(new CommandInput("msiexec,quiet", "msiexec /[quiet] /[forcerestart] test.exe", 1));
        originalCommands.add(new CommandInput("msiexec,quiet", "msiexec /[forcerestart] test.exe", 0));
        originalCommands.add(new CommandInput("cacls,c:,everyone:f", "cacls c: /[t] /[g] users:[f]", 0));
        originalCommands.add(new CommandInput("cacls,c:,everyone:f", "cacls c: /[t] /[g] everyone:[f]", 1));
        originalCommands.add(new CommandInput("cacls,c:,everyone:f", "cacls c: /[t] /[g] [users:f]", 0));
        originalCommands.add(new CommandInput("cacls,c:,everyone:f", "cacls c: /[t] /[g] [everyone:f]", 1));
        originalCommands.add(new CommandInput("icacls,everyone:f", "icacls C:\\ /grant administrators:[f] /[t]", 0));
        originalCommands.add(new CommandInput("icacls,everyone:f", "icacls c: /grant users:[rx] /[t]", 0));
        originalCommands.add(new CommandInput("icacls,everyone:f", "icacls c: /grant everyone:[f] /[t]", 1));
        originalCommands.add(new CommandInput("icacls,everyone:f", "icacls C:\\Windows\\Temp /grant everyone:[f] /[t]", 1));
        originalCommands.add(new CommandInput("takeown,/r,/d,/a", "takeown /[s] localhost /[f] c:\\1.txt /[a]", 0));
        originalCommands.add(new CommandInput("takeown,/r,/d,/a", "takeown /[f] c:\\*.* /[a] /[r] /[d] y", 1));
        originalCommands.add(new CommandInput("schtasks,create", "schtasks /[create] /[tn] test /[sc] [minute] /[tr] notepad", 1));
        originalCommands.add(new CommandInput("schtasks,change", "schtasks /[change] /[tn] ExistingTask /[tr] calc.exe /[f]", 1));
        originalCommands.add(new CommandInput("schtasks,create", "schtasks /[query] /[tn] test", 0));
        originalCommands.add(new CommandInput("schtasks,change", "schtasks /[query] /[v] /[fo] list", 0));
        originalCommands.add(new CommandInput("schtasks,create,minute", "schtasks /[create] /[tn] test /[sc] [minute] /[tr] notepad", 1));
        originalCommands.add(new CommandInput("schtasks,create,minute", "schtasks /[create] /[tn] test /[sc] [monthly] /[mo] 1 /[tr] notepad", 0));
        originalCommands.add(new CommandInput("wevtutil,cl", "wevtutil [cl] system", 1));
        originalCommands.add(new CommandInput("wevtutil,cl", "wevtutil [gl] system", 0));
        originalCommands.add(new CommandInput("wevtutil,cl", "wevtutil [cl] security", 1));
        originalCommands.add(new CommandInput("wevtutil,cl", "wevtutil [gli] security", 0));
        originalCommands.add(new CommandInput("wevtutil,e:false", "wevtutil [sl] system /e:true", 0));
        originalCommands.add(new CommandInput("wevtutil,e:false", "wevtutil [sl] system /e:false", 1));
        originalCommands.add(new CommandInput("wmic,process,create", "wmic process [call] [create] calc.exe", 1));
        originalCommands.add(new CommandInput("wmic,process,create", "wmic process [get] name", 0));

        return originalCommands;
    }
}