import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Chenghan Wen
 * @author Wenbo Nan
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires [<"INSTRUCTION"> is a proper prefix of tokens]
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to statement string of body of
     *          instruction at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";

        Reporter.assertElseFatalError(tokens.dequeue().equals("INSTRUCTION"),
                "Instruction did not start with INSTRUCTION");
        String name = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(name),
                "Invailid identifier");
        Reporter.assertElseFatalError(tokens.dequeue().equals("IS"),
                "Program name should be followed by IS");
        body.parseBlock(tokens);
        Reporter.assertElseFatalError(tokens.dequeue().equals("END"),
                "END did not follow your identifier");
        Reporter.assertElseFatalError(tokens.dequeue().equals(name),
                "Identifier did not match");
        return name;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";
        Reporter.assertElseFatalError(tokens.dequeue().equals("PROGRAM"),
                "PROGRAM should be the start");
        String name = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(name),
                "This is not a valid identifier");
        this.replaceName(name);
        Map<String, Statement> nameofInstruction = this.newContext();
        Reporter.assertElseFatalError(tokens.dequeue().equals("IS"),
                "dont have IS after the identifier");
        Statement s;
        String newName;
        while (tokens.front().equals("INSTRUCTION")) {
            s = this.newBody();
            newName = parseInstruction(tokens, s);
            Reporter.assertElseFatalError(Tokenizer.isIdentifier(newName),
                    "This is not a valid identifier");
            Reporter.assertElseFatalError(!newName.equals("move"),
                    "The name can not be a primitive move");
            Reporter.assertElseFatalError(!newName.equals("infect"),
                    "The name can not be a primitive infect");
            Reporter.assertElseFatalError(!newName.equals("turnright"),
                    "The name can not be a primitive turnright");
            Reporter.assertElseFatalError(!newName.equals("turnleft"),
                    "The name can not be a primitive turnleft");
            Reporter.assertElseFatalError(!newName.equals("skip"),
                    "The name can not be a primitive skip");
            Reporter.assertElseFatalError(!nameofInstruction.hasKey(newName),
                    "This name has been used");
            nameofInstruction.add(newName, s);
        }
        this.replaceContext(nameofInstruction);
        Statement newS = this.newBody();
        Reporter.assertElseFatalError(tokens.dequeue().equals("BEGIN"),
                "BEGIN needs to start the body of the program");
        newS.parseBlock(tokens);
        this.replaceBody(newS);
        Reporter.assertElseFatalError(tokens.dequeue().equals("END"),
                "END is not at the end of the body");
        Reporter.assertElseFatalError(tokens.dequeue().equals(name),
                "Program name does not match");
        Reporter.assertElseFatalError(
                tokens.front().equals(Tokenizer.END_OF_INPUT)
                        && tokens.length() == 1,
                "END OF INPUT not found ");
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
