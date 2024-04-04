package assembler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HackAssembler {
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
        final List<String> result = new ArrayList<>();
        for (String instruction : assemblyString) {
            if (instruction.charAt(0) == '@')
                result.add(parseAInstruction(instruction));
            else
                result.add(parseCInstruction(instruction));
        }
        return result;
    }

    public static String parseAInstruction(String instruction) {
        StringBuilder binaryHackAsText = new StringBuilder();
        int number = Integer.parseInt(instruction.substring(1));
        for (int i = 1; i < (1 << 15); i *= 2) {
            if ((number & i) == i)
                binaryHackAsText.insert(0, "1");
            else
                binaryHackAsText.insert(0, "0");
        }
        binaryHackAsText.insert(0, "0");    // op-code
        return binaryHackAsText.toString();
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
