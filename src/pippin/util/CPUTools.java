/*
	Copyright notice

	Cengage Learning Inc. Reproduced by permission. http://www.cengage.com/permissions

	This simulator can be used exclusively for non-commercial, educational activities.
	This CPU Visual Simulator was derived (modified and extended - see credits) in 2021
	from the original PIPPIN Applet (© 1998 PWS Publishing Company), with permission from
	Cengage Learning Inc. Other deletions, additions, edits, changes to, or derivatives of
	the Cengage Material can only be made with Cengage’s express written approval, email sufficing.

	Credits

	Original PIPPIN applet:
	Prof. Stuart Hirshfield and Prof. Rick Decker (designers)
	CPU Visual Simulator extension, carried out with an Open Pedagogy / OER-enabled process:

	Renato Cortinovis, PhD - project coordinator
	Nicola Preda - porting to a Java application, initial new functionalities, and documentation
	Jonathan Cancelli - implementation of the great majority of new functionalities
	Alessandro Belotti - implementation of visualizations and animations
	Davide Riva - initial refactoring
	MariaPia Cavarretta, Giordano Cortinovis, Giovanni Ingargiola, Alessandro Suru, Piero Andrea Sileo and others - documentation.
*/
package pippin.util;

public class CPUTools {

    public static String error;

    public static final int ADD = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int LOD = 4;
    public static final int STO = 5;
    public static final int JN = 6;
    public static final int JNN = 7;
    public static final int AND = 8;
    public static final int NOT = 9;
    public static final int CMP = 10;
    public static final int JNZ = 11;
    public static final int JMP = 12;
    public static final int JZ = 13;
    public static final int NOP = 14;
    public static final int HLT = 15;
    public static final int IMMEDIATE_FLAG = 16;


    private CPUTools() {
    }


    public static boolean isImmediate(String operand) {
        if (operand == null) {
            return false;
        }
        return operand.startsWith("#");
    }

    public static int instructionToInt16(String instruction) {
        error = null;
        String opcodeString = firstPart(instruction);
        String operandString = secondPart(instruction);
        int opcode = opcodeToInt8(opcodeString);

        if (error != null) {
            return -1;
        }

        int operand = 0;
        boolean opcodeTakesOperand = !opcodeTakesNoOperand(opcode);
        boolean opcodeTakesImmediate = opcodeTakesImmediate(opcode);
        boolean operandIsImmediate = isImmediate(operandString);

        if (!opcodeTakesOperand && operandString != null) {
            error = "opcode does not take an operand";
            return -1;
        } else if (opcodeTakesOperand && operandString == null) {
            error = "opcode requires an operand";
            return -1;
        } else if (!opcodeTakesOperand) {
            return joinBits(opcode, operand);
        } else if (operandIsImmediate && opcodeTakesImmediate) {
            operand = operandImmediateToInt(operandString);
            opcode += IMMEDIATE_FLAG;
            return joinBits(opcode, operand);
        } else if (operandIsImmediate) {
            error = "opcode doesn't take immediate operand";
            return -1;
        } else {
            if (!operandString.matches("\\d+")) {
                error = "label not found";
                return -1;
            }
            operand = operandDirectToInt(operandString);
            return error == null ? joinBits(opcode, operand) : -1;
        }
    }

    public static String int16ToInstruction(int instruction) {
        boolean isImmediate = false;
        error = null;
        int opcode = top8Bits(instruction);
        int operand = bottom8Bits(instruction);
        if (opcode >= 16) {
            isImmediate = true;
            opcode -= 16;
        }
        String opString = opcodeToString(opcode);
        if (opcodeTakesNoOperand(opcode)) {
            return opString;
        }
        String operString;
        if (isImmediate) {
            operString = operandImmediateToString(operand);
        } else if (opcode == 12 || opcode == 13) {
            operString = Integer.toString(operand);
        } else {
            operString = operandDirectToString(operand);
        }
        if (opString.length() > 0) {
            return opString + "\t" + operString;
        } else {
            error = "unknown opcode";
            return null;
        }
    }

    public static String int16ToOpcode(int instruction) {
        int top = top8Bits(instruction);
        if (top >= 16) {
            top -= 16;
        }
        return opcodeToString(top);
    }

    public static String int16ToOperand(int instruction) {
        int opcode = top8Bits(instruction);
        int operand = bottom8Bits(instruction);
        if (opcode >= IMMEDIATE_FLAG) {
            return operandImmediateToString(operand);
        }
        return operandDirectToString(operand);
    }

    public static boolean opcodeTakesNoOperand(int opcode) {
        return opcode == NOP || opcode == HLT;
    }

    public static boolean opcodeTakesImmediate(int opcode) {
        switch (opcode) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case AND:
            case NOT:
            case CMP:
            case LOD:
                return true;
            default:
                return false;
        }
    }

    public static char opcodeToOperation(int opcode) {
        switch (opcode) {
            case ADD:
                return '+';
            case SUB:
                return '-';
            case MUL:
                return 'x';
            case DIV:
                return '/';
            case LOD:
                return '=';
            case AND:
                return '&';
            case CMP:
                return ':';
            case NOT:
                return '!';
            default:
                return ' ';
        }
    }

    public static String intToBinString(int value, int digits) {
        String binString = Integer.toString(value, 2);
        return "0000000000000000".substring(binString.length(), digits) + binString;
    }

    public static String sEx8ToBinString(int value) {
        return intToBinString(value & 0xff, 8);
    }

    public static String sEx8ToDecString(int value) {
        return Integer.toString(intToSEx8(value), 10);
    }

    public static int intToSEx8(int value) {
        if ((value & 0x80) == 128) {
            return value | 0xffffff00;
        } else {
            return value & 0xff;
        }
    }

    public static int stringToSEx8(String value, int base) throws NumberFormatException {
        int i = Integer.valueOf(value, base);
        if (base == 2 && i >= 128 && i <= 255) {
            i |= 0xffffff00;
        }
        return i;
    }

    public static int top8Bits(int value) {
        return (byte)(value / 256);
    }

    public static int bottom8Bits(int value) {
        return value % 256;
    }

    public static String error() {
        return error;
    }

    private static int opcodeToInt8(String opcode) {
        opcode = opcode.toUpperCase();
        error = null;
        switch (opcode) {
            case "ADD":
                return ADD;
            case "SUB":
                return SUB;
            case "MUL":
                return MUL;
            case "DIV":
                return DIV;
            case "LOD":
                return LOD;
            case "STO":
                return STO;
            case "JN":
                return JN;
            case "JNN":
                return JNN;
            case "AND":
                return AND;
            case "NOT":
                return NOT;
            case "CMP":
                return CMP;
            case "JNZ":
                return JNZ;
            case "JMP":
                return JMP;
            case "JZ":
                return JZ;
            case "NOP":
                return NOP;
            case "HLT":
                return HLT;
            default:
                error = "unknown instruction";
                return -1;
        }
    }

    private static String opcodeToString(int opcode) {
        switch (opcode) {
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case DIV:
                return "DIV";
            case LOD:
                return "LOD";
            case STO:
                return "STO";
            case JN:
                return "JN";
            case JNN:
                return "JNN";
            case AND:
                return "AND";
            case NOT:
                return "NOT";
            case CMP:
                return "CMP";
            case JNZ:
                return "JNZ";
            case JMP:
                return "JMP";
            case JZ:
                return "JZ";
            case NOP:
                return "NOP";
            case HLT:
                return "HLT";
            default:
                return "";
        }
    }

    private static int operandDirectToInt(String operand) throws NumberFormatException {
        if (operand == null) {
            error = "opcode requires an operand";
            return 0;
        }
        if (operand.startsWith("#")) {
            error = "opcode requires a direct reference";
            return 0;
        }
        if (operand.matches("[A-Z]{1,9}")) {
            error = "label not found";
            return 0;
        }
        if (!operand.matches("\\d{1,3}")) {
            error = "invalid sintax";
            return 0;

        }
        int value = Integer.parseInt(operand);
        if (value % 2 != 0 || value < 0 || value > 126) {
            error = "invalid address";
            return 0;
        }
        return value; // 0
    }

    private static String operandDirectToString(int operand) {
        if (operand > 126 || operand < 0 || operand % 2 != 0) {
            error = "invalid address";
            return null;
        }
        try {
            return Integer.toString(operand);
        } catch (NumberFormatException e) {
            error = "invalid address";
            return null;
        }
    }

    private static int operandImmediateToInt(String operand) {
        if (operand == null) {
            error = "opcode requires an operand";
            return 0;
        }
        if (!operand.startsWith("#")) {
            error = "found direct operand instead of immediate";
            return 0;
        }
        String value = operand.substring(1);
        try {
            int integer = Integer.parseInt(value, 10);
            if (integer < -128 || integer > 127) {
                error = "operand out of range";
                return 0;
            } else {
                return integer;
            }
        } catch (NumberFormatException _ex) {
            error = "operand not a legal integer";
        }
        return 0;
    }

    private static String operandImmediateToString(int operand) {
        return "#" + intToSEx8(operand);
    }

    public static String firstPart(String string) {
        return string.trim().split("\\t", 2)[0];
    }

    public static String secondPart(String string) {
        String[] tokens = string.trim().split("\\t", 2);
        return tokens.length != 2 ? null : tokens[1];
    }

    public static int joinBits(int top8Bits, int bottom8Bits) {
        return (top8Bits << 8 & 0xff00) + (bottom8Bits & 0xff);
    }

}


