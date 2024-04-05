package assembler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Parser {
    public enum CommandType {A_COMMAND, C_COMMAND, L_COMMAND}

    private final List<String> preprocessedCode;
    private Iterator<String> codeIterator;
    private String command;
    private CommandType commandType;

    public Parser(List<String> inputCode) {
        preprocessedCode = new ArrayList<>();
        for (String line : inputCode) {
            String uncommentedLine = line.substring(0, line.contains("/") ? line.indexOf("/") : line.length());
            String whitespacelessUncommentedLine = uncommentedLine.replaceAll("[\\s]", "");
            if (whitespacelessUncommentedLine.length() != 0)
                preprocessedCode.add(whitespacelessUncommentedLine);
        }
        resetIterator();
    }

    public boolean hasMoreCommands() {
        return codeIterator.hasNext();
    }

    public void advance() {
        command = codeIterator.next();
        if (command.startsWith("("))
            commandType = CommandType.L_COMMAND;
        else if (command.startsWith("@"))
            commandType = CommandType.A_COMMAND;
        else
            commandType = CommandType.C_COMMAND;
    }

    public CommandType commandType() {
        return commandType;
    }

    public String symbol() {
        if (commandType == CommandType.C_COMMAND)
            throw new RuntimeException(".symbol() is called when commandType == CommandType.C_COMMAND");
        return commandType == CommandType.L_COMMAND
                ? command.substring(1, command.length() - 1)
                : command.substring(1, command.length());   // CommandType.A_COMMAND
    }

    public String dest() {
        if (commandType != CommandType.C_COMMAND)
            throw new RuntimeException(".dest() is called when commandType != CommandType.C_COMMAND");
        return command.split("[=]").length == 2
                ? command.split("[=]")[0]
                : null;
    }

    public String comp() {
        if (commandType != CommandType.C_COMMAND)
            throw new RuntimeException(".comp() is called when commandType != CommandType.C_COMMAND");
        String destAndComp = command.split("[;]")[0];
        return destAndComp.split("[=]").length == 2
                ? destAndComp.split("[=]")[1]
                : destAndComp.split("[=]")[0];
    }

    public String jump() {
        if (commandType != CommandType.C_COMMAND)
            throw new RuntimeException(".jump() is called when commandType != CommandType.C_COMMAND");
        return command.split("[;]").length == 2
                ? command.split("[;]")[1]
                : null;
    }

    void resetIterator() {
        codeIterator = preprocessedCode.listIterator();
    }
}
