package assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import assembler.Parser.CommandType;

public class HackAssembler {
    private Parser parser;
    private SymbolTable symbolTable;
    private Code code;
    private String file;

    public static void main(String[] args) {
        String file = args[0];
        if (!file.endsWith(".asm"))
            throw new RuntimeException("Filetype error: filetype should be .asm");
        new HackAssembler(file).assemble();
    }

    public HackAssembler(String file) {
        this.file = file;
        List<String> inputCode = readFile(file);
        parser = new Parser(inputCode);
        symbolTable = new SymbolTable(parser);
        code = new Code();
    }

    public void assemble() {
        List<String> outputCode = new ArrayList<>();
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType() == CommandType.A_COMMAND) {
                if (symbolTable.contains(parser.symbol()))
                    outputCode.add("0" + toZeroPadded15BitBinaryString(symbolTable.getAddress(parser.symbol())));
                else
                    outputCode.add("0" + toZeroPadded15BitBinaryString(Integer.parseInt(parser.symbol())));
            } else if (parser.commandType() == CommandType.C_COMMAND)
                outputCode.add("111" + code.comp(parser.comp()) + code.dest(parser.dest()) + code.jump(parser.jump()));
            // else // if (parser.commandType() == CommandType.L_COMMAND) continue;
        }
        writeHackFile(file, outputCode);
    }

    private static List<String> readFile(String file) {
        List<String> inputCode = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
                inputCode.add(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputCode;
    }

    private static String toZeroPadded15BitBinaryString(int number) {
        StringBuilder numberString = new StringBuilder(Integer.toBinaryString(number));
        while (numberString.length() < 15)
            numberString.insert(0, "0");
        return numberString.toString();
    }

    private static void writeHackFile(String assemblyFile, List<String> binaryContentAsText) {
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
