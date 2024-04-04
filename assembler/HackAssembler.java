package assembler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HackAssembler {
    private static final Map<String, Integer> symbolTable;

    static {
        symbolTable = new HashMap<>();
        for (int i = 0; i < 16; i++)
            symbolTable.put(("R" + i), i);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }

    public static void main(String[] args) {
        if (!args[0].endsWith(".asm")) throw new RuntimeException("Filetype error: filetype should be .asm");

        final List<String> asmFileLines = readFile(args[0]);

        final List<String> uncommentedLines = new ArrayList<>();
        for (String line : asmFileLines)
            uncommentedLines.add(line.substring(0, line.contains("/") ? line.indexOf("/") : line.length()));

        final List<String> cleanedLines = new ArrayList<>();
        for (String line : uncommentedLines) {
            String cleanedLine = line.replaceAll("[\\s]", "");
            if (cleanedLine.length() != 0)
                cleanedLines.add(cleanedLine);
        }

        final List<String> hackBinaries = parseAssemblyToHack(cleanedLines);

        writeHackFile(args[0], hackBinaries);
    }

    public static List<String> readFile(String file) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
                result.add(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<String> parseAssemblyToHack(List<String> assemblyString) {
        List<String> labellessInstructions = resolveLabels(assemblyString);
        List<String> symbollessInstructions = resolveSymbols(labellessInstructions);
        return parseInstructions(symbollessInstructions);
    }

    // Records the line numbers of labels and removes the line they are defined
    public static List<String> resolveLabels(List<String> assemblyWithLabels) {
        List<String> result = new ArrayList<>();
        int labellessInstructionIndex = 0;
        for (String instruction : assemblyWithLabels) {
            if (instruction.charAt(0) == '(') {
                String label = instruction.substring(1, instruction.length() - 1);
                symbolTable.put(label, labellessInstructionIndex);
            } else {
                result.add(instruction);
                labellessInstructionIndex++;
            }
        }
        return result;
    }

    // Upon first encounter, allocates memory to a variable.
    // Converts labels and variables to referenced numbers.
    public static List<String> resolveSymbols(List<String> assemblyWithSymbols) {
        List<String> result = new ArrayList<>();
        int avaliableRAMAddress = 16;
        for (String instruction : assemblyWithSymbols) {
            if (instruction.charAt(0) == '@') {
                try{
                    Integer.parseInt(instruction.substring(1));
                    result.add(instruction);
                } catch (NumberFormatException e) {
                    String symbol = instruction.substring(1);
                    Integer symbolValue = symbolTable.get(symbol);
                    if(symbolValue == null){
                        symbolValue = avaliableRAMAddress++;
                        symbolTable.put(symbol, symbolValue);
                    }
                    result.add("@" + symbolValue);
                }
            } else {
                result.add(instruction);
            }
        }
        return result;
    }

    private static List<String> parseInstructions(List<String> symbollessInstructions) {
        final List<String> result = new ArrayList<>();
        for (String instruction : symbollessInstructions) {
            if (instruction.charAt(0) == '@')
                result.add(parseAInstruction(instruction));
            else
                result.add(parseCInstruction(instruction));
        }
        return result;
    }

    public static String parseAInstruction(String instruction) {
        Integer symbolValue = symbolTable.get(instruction.substring(1));
        if (symbolValue != null)
            return "0" /* op-code */ + toZeroPadded15BitBinaryString(symbolValue);
        else {
            int number = Integer.parseInt(instruction.substring(1));
            return "0" /* op-code */ + toZeroPadded15BitBinaryString(number);
        }
    }


    public static String toZeroPadded15BitBinaryString(int number) {
        StringBuilder numberString = new StringBuilder(Integer.toBinaryString(number));
        while (numberString.length() < 15)
            numberString.insert(0, "0");
        return numberString.toString();
    }

    public static String parseCInstruction(String instruction) {
        String destination = null, computation, jump = null;

        String[] dcAndJump = instruction.split("[;]");
        String dc = dcAndJump[0];
        if (dcAndJump.length == 2)
            jump = dcAndJump[1];
        String[] destinationAndComputation = dc.split("[=]");
        if (destinationAndComputation.length == 2) {
            destination = destinationAndComputation[0];
            computation = destinationAndComputation[1];
        } else {
            computation = destinationAndComputation[0];
        }
        String computationBinary = parseCompBits(computation);
        String destinationBinary = parseDestBits(destination);
        String jumpBinary = parseJumpBits(jump);

        return "111" + computationBinary + destinationBinary + jumpBinary;
    }

    public static String parseCompBits(String instruction) {
        if (instruction.equals("1")) return "0111111";
        if (instruction.equals("-1")) return "0111010";
        if (instruction.equals("0")) return "0101010";
        if (instruction.equals("M+1")) return "1110111";
        if (instruction.equals("A+1")) return "0110111";
        if (instruction.equals("-M")) return "1110011";
        if (instruction.equals("-A")) return "0110011";
        if (instruction.equals("M-1")) return "1110010";
        if (instruction.equals("A-1")) return "0110010";
        if (instruction.equals("!M")) return "1110001";
        if (instruction.equals("!A")) return "0110001";
        if (instruction.equals("M")) return "1110000";
        if (instruction.equals("A")) return "0110000";
        if (instruction.equals("D+1")) return "0011111";
        if (instruction.equals("D-M")) return "1010011";
        if (instruction.equals("D-A")) return "0010011";
        if (instruction.equals("D|M")) return "1010101";
        if (instruction.equals("D|A")) return "0010101";
        if (instruction.equals("-D")) return "0001111";
        if (instruction.equals("!D")) return "0001101";
        if (instruction.equals("D")) return "0001100";
        if (instruction.equals("D-1")) return "0001110";
        if (instruction.equals("M-D")) return "1000111";
        if (instruction.equals("A-D")) return "0000111";
        if (instruction.equals("D+M")) return "1000010";
        if (instruction.equals("D+A")) return "0000010";
        if (instruction.equals("D&M")) return "1000000";
        if (instruction.equals("D&A")) return "0000000";

        return "0000000";
    }

    public static String parseDestBits(String instruction) {
        if (instruction == null) return "000";
        boolean d1Bit = instruction.contains("A");
        boolean d2Bit = instruction.contains("D");
        boolean d3Bit = instruction.contains("M");
        return (d1Bit ? "1" : "0") + (d2Bit ? "1" : "0") + (d3Bit ? "1" : "0");
    }

    public static String parseJumpBits(String instruction) {
        if (instruction == null) return "000";
        if (instruction.equals("JGT")) return "001";
        if (instruction.equals("JEQ")) return "010";
        if (instruction.equals("JGE")) return "011";
        if (instruction.equals("JLT")) return "100";
        if (instruction.equals("JNE")) return "101";
        if (instruction.equals("JLE")) return "110";
        if (instruction.equals("JMP")) return "111";
        return "";
    }

    public static void writeHackFile(String assemblyFile, List<String> binaryContentAsText) {
        String hackFileLocation = assemblyFile
                .substring(0, assemblyFile.lastIndexOf("."))
                .concat(".hack");
        try (FileWriter fileWriter = new FileWriter(hackFileLocation)) {
            for (String line : binaryContentAsText)
                fileWriter.append(line).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
