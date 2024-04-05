package assembler;

public class Code {
    public String dest(String mnemonic) {
        if (mnemonic == null) return "000";
        boolean d1Bit = mnemonic.contains("A");
        boolean d2Bit = mnemonic.contains("D");
        boolean d3Bit = mnemonic.contains("M");
        return (d1Bit ? "1" : "0") + (d2Bit ? "1" : "0") + (d3Bit ? "1" : "0");
    }

    public String comp(String mnemonic) {
        if (mnemonic.equals("1")) return "0111111";
        if (mnemonic.equals("-1")) return "0111010";
        if (mnemonic.equals("0")) return "0101010";
        if (mnemonic.equals("M+1")) return "1110111";
        if (mnemonic.equals("A+1")) return "0110111";
        if (mnemonic.equals("-M")) return "1110011";
        if (mnemonic.equals("-A")) return "0110011";
        if (mnemonic.equals("M-1")) return "1110010";
        if (mnemonic.equals("A-1")) return "0110010";
        if (mnemonic.equals("!M")) return "1110001";
        if (mnemonic.equals("!A")) return "0110001";
        if (mnemonic.equals("M")) return "1110000";
        if (mnemonic.equals("A")) return "0110000";
        if (mnemonic.equals("D+1")) return "0011111";
        if (mnemonic.equals("D-M")) return "1010011";
        if (mnemonic.equals("D-A")) return "0010011";
        if (mnemonic.equals("D|M")) return "1010101";
        if (mnemonic.equals("D|A")) return "0010101";
        if (mnemonic.equals("-D")) return "0001111";
        if (mnemonic.equals("!D")) return "0001101";
        if (mnemonic.equals("D")) return "0001100";
        if (mnemonic.equals("D-1")) return "0001110";
        if (mnemonic.equals("M-D")) return "1000111";
        if (mnemonic.equals("A-D")) return "0000111";
        if (mnemonic.equals("D+M")) return "1000010";
        if (mnemonic.equals("D+A")) return "0000010";
        if (mnemonic.equals("D&M")) return "1000000";
        if (mnemonic.equals("D&A")) return "0000000";

        return "0000000";
    }

    public String jump(String mnemonic) {
        if (mnemonic == null) return "000";
        if (mnemonic.equals("JGT")) return "001";
        if (mnemonic.equals("JEQ")) return "010";
        if (mnemonic.equals("JGE")) return "011";
        if (mnemonic.equals("JLT")) return "100";
        if (mnemonic.equals("JNE")) return "101";
        if (mnemonic.equals("JLE")) return "110";
        if (mnemonic.equals("JMP")) return "111";
        return "";
    }
}
