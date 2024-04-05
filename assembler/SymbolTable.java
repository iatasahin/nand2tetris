package assembler;

import java.util.HashMap;
import java.util.Map;

import assembler.Parser.CommandType;

public class SymbolTable {
    private final Parser parser;
    private final Map<String, Integer> symbolTable;

    public SymbolTable(Parser parser) {
        this.parser = parser;
        symbolTable = new HashMap<>();

        for (int i = 0; i < 16; i++)
            addEntry(("R" + i), i);
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);

        parser.resetIterator();
        resolveLabels();
        parser.resetIterator();
        resolveVariables();
        parser.resetIterator();
    }

    private void resolveLabels() {
        int codeLineNumber = 0;
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType() == CommandType.L_COMMAND)
                addEntry(parser.symbol(), codeLineNumber);
            else
                codeLineNumber++;
        }
    }

    private void resolveVariables() {
        int avaliableRAMAddress = 16;
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType() == CommandType.A_COMMAND) {
                if (isNumber(parser.symbol()))
                    continue;
                if (!contains(parser.symbol()))
                    addEntry(parser.symbol(), avaliableRAMAddress++);
            }
        }
    }

    private boolean isNumber(String symbol) {
        try {
            Integer.parseInt(symbol);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public Integer getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
}
