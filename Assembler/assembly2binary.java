import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class assembly2binary {
    private static HashMap<String, String> instructionSet = new HashMap<String, String>();
    private static HashMap<String, String> ramLabelSet = new HashMap<String, String>();
    private static HashMap<String, String> romLabelSet = new HashMap<String, String>();
    private static String optionalOutput = "";

    public static void main(String args[]) {

        initialiseAll();
        try {
            int instAddLine = -1;
            String aslFileName = args[0];
            if (!(aslFileName.substring(aslFileName.length() - 4, aslFileName.length()).equals(".sal")))
                return;
            String binWriteName = aslFileName.substring(0, aslFileName.length() - 4) + ".bin";
            BufferedReader aslFile = new BufferedReader(new FileReader(aslFileName));
            BufferedWriter binWriter = new BufferedWriter(new FileWriter(binWriteName));
            if (aslFile.readLine().charAt(1) == 'd')
                DateSection(aslFile);
            String s;
            while ((s = aslFile.readLine()) != null) {
                optionalOutput = "";
                if (s.indexOf("//") > 0) {
                    s = s.substring(0, s.indexOf("//"));
                }
                s = s.trim();
                String pattern = "^[a-zA-Z][a-zA-Z0-9_]*\s*:$";
                if ((s.matches(pattern))&&(!ramLabelSet.containsKey(s))) {
                    romLabelSet.put(s.substring(0, s.length() - 1), Integer.toBinaryString(instAddLine + 1));
                } else {
                    instAddLine++;
                    translateLineToBin(s);
                    if (!optionalOutput.isEmpty())
                        instAddLine++;
                }

            }
            aslFile = new BufferedReader(new FileReader(aslFileName));

            boolean notDone = true;
            while (notDone == true) {
                String test = aslFile.readLine();
                if ((test.charAt(0) == '.') && (test.charAt(1) == 'c'))
                    notDone = false;
            }
            while ((s = aslFile.readLine()) != null) {
                optionalOutput = "";
                if (s.indexOf("//") > 0) {
                    s = s.substring(0, s.indexOf("//"));
                }
                s = s.trim();
                String pattern = "^[a-zA-Z][a-zA-Z0-9_]*\s*:$";
                if (!s.matches(pattern) && !s.isEmpty()) {
                    instAddLine++;
                    String sanitizedOutput = translateLineToBin(s);
                    binWriter.write(sanitizedOutput + "\n");
                    if (!optionalOutput.isEmpty())
                        binWriter.write(optionalOutput + "\n");
                }

            }

            aslFile.close();
            binWriter.close();

        } catch (Exception ex) {

        }
    }

    private static void DateSection(BufferedReader asmFile) throws IOException {
        int count = 0;
        String ramLabel;
        boolean notDone = true;
        String pattern = "^[a-zA-Z][a-zA-Z0-9_#]*$";
        while (notDone == true) {
            ramLabel = asmFile.readLine();
            ramLabel = ramLabel.trim();
            if ((ramLabel.charAt(0)) == '.') {
                notDone = false;
            } else {
                if (ramLabel.matches(pattern) && !instructionSet.containsKey(ramLabel)) {
                    if (!ramLabel.matches("r[0-9]{1,2}")) {
                        ramLabelSet.put(ramLabel, Integer.toBinaryString(count));
                        count++;
                    }
                }
            }
        }

    }

    private static void initialiseAll() {
        instructionSet.put("ADD", "0000");
        instructionSet.put("SUB", "0001");
        instructionSet.put("AND", "0010");
        instructionSet.put("OR", "0011");
        instructionSet.put("JMP", "0100");
        instructionSet.put("JGT", "0101");
        instructionSet.put("JLT", "0110");
        instructionSet.put("JEQ", "0111");
        instructionSet.put("INC", "1001");
        instructionSet.put("DEC", "1010");
        instructionSet.put("NOT", "1011");
        instructionSet.put("LOAD", "1100");
        instructionSet.put("STORE", "1101");

        return;
    }

    private static String translateLineToBin(String instructionLine) {
        Pattern pattern = Pattern.compile("^([A-Z]+)(\\s+)([a-zA-Z0-9_#]+)(,\\s*)?([a-zA-Z0-9_#]*)");
        Matcher matcher = pattern.matcher(instructionLine);
        String finalOutput = "";
        if (matcher.find()) {
            String opcode = matcher.group(1);
            String register1 = matcher.group(3);
            String operand = matcher.group(5);

            finalOutput = finalOutput + instructionSet.get(opcode);
            if (register1.matches("r[0-9]{1,2}")) {
                int reg = Integer.parseInt(register1.substring(1, register1.length()));
                finalOutput = finalOutput + binElongator(Integer.toBinaryString(reg), 3);
            } else if (romLabelSet.containsKey(register1)) {
                finalOutput = finalOutput + "000" + "0" + "10" + binElongator(romLabelSet.get(register1), 6);
                return finalOutput;
            } else if (register1.matches("^[0-9]+$")) {
                int numAddr = Integer.parseInt(register1);
                if (numAddr > 31)
                    finalOutput = finalOutput + "000" + "1" + "10" + maxBinElongator(finalOutput);
                else
                    finalOutput = finalOutput + "000" + "0" + "10" + binElongator(finalOutput, 6);
                return finalOutput;
            }           

            if (!operand.isEmpty()) {
                if (operand.matches("r[0-9]{1,2}")) {
                    int reg = Integer.parseInt(operand.substring(1, register1.length()));
                    finalOutput = finalOutput + "0" + "00" + binElongator(Integer.toBinaryString(reg), 6);
                } else if (operand.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                    if (romLabelSet.containsKey(operand)) {
                        finalOutput = finalOutput + "0" + "10" + binElongator(romLabelSet.get(operand), 6);

                    } else {
                        if (Integer.parseInt(ramLabelSet.get(operand)) > 31)
                            finalOutput = finalOutput + "1" + "10" + maxBinElongator(ramLabelSet.get(operand));
                        else
                            finalOutput = finalOutput + "0" + "10" + binElongator(ramLabelSet.get(operand),6);

                    }
                } else if (operand.charAt(0) == '#') {
                    int value = Integer.parseInt(operand.substring(1, operand.length()));
                    if (value > 31)
                        finalOutput = finalOutput + "1" + "01" + maxBinElongator(Integer.toBinaryString(value));
                    else
                        finalOutput = finalOutput + "0" + "01" + binElongator(Integer.toBinaryString(value), 6);

                } else {
                    int value = Integer.parseInt(operand);
                    if (value > 31)
                        finalOutput = finalOutput + "1" + "10" + maxBinElongator(Integer.toBinaryString(value));
                    else
                        finalOutput = finalOutput + "0" + "10" + binElongator(Integer.toBinaryString(value), 6);
                }
            }else

    {
        finalOutput = finalOutput + "011000000";
    }

    }

    return finalOutput;}

    private static String binElongator(String binary, int extendTill) {
        while (binary.length() != extendTill) {
            binary = "0" + binary;
        }
        return binary;
    }

    private static String maxBinElongator(String binary) {
        binary = binElongator(binary, 22);
        String pog = binary.substring(0, 6);
        optionalOutput = binary.substring(6, 22);
        return pog;
    }
}