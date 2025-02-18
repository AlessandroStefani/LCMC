package visualsvm;

import org.antlr.v4.runtime.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestVisualASM {
    public static void main(String[] args) throws Exception {

        String fileName = "test.code.asm";

        CharStream charsASM = CharStreams.fromFileName(fileName);
        SVMLexer lexerASM = new SVMLexer(charsASM);
        CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
        SVMParser parserASM = new SVMParser(tokensASM);


        parserASM.assembly();

        System.out.println("You had: "+lexerASM.lexicalErrors+" lexical errors and "+parserASM.getNumberOfSyntaxErrors()+" syntax errors.");
        if (lexerASM.lexicalErrors>0 || parserASM.getNumberOfSyntaxErrors()>0) System.exit(1);

        System.out.println("Starting Virtual Machine...");
        ExecuteVM vm = new ExecuteVM(parserASM.code,parserASM.sourceMap, Files.readAllLines(Paths.get(fileName)));
        vm.cpu();


    }
}
