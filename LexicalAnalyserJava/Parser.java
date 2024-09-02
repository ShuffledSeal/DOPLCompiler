import java.util.ArrayList;
import java.util.HashMap; 



public class Parser {
    private static ArrayList<Token> tokens;
    private static int index = 0;
    public static HashMap<String, String> variableTable = new HashMap<String, String>();


    public static void main(String args[]) {

        LexicalAnalyser lex = new LexicalAnalyser();
        tokens = lex.getTokens(args[0]);
        if (tokens == null) {
            System.out.println("error");
            return;
        }
        parseStart();
        parseVariables();
        parseStatements();
        parseFinish();
        System.out.println("ok");
    }

    private static void parseStart() {
        if (!tokens.get(index).getType().equals("START")) {
            System.out.println(tokens.get(index).getType());
            System.out.println("error");
            System.exit(0);
        }
        index++;
    }

    private static void parseFinish() {
        if (!tokens.get(index).getType().equals("FINISH")) {
            System.out.println(tokens.get(index).getType());
            System.out.println("error 2");
            System.exit(0);
        }
        index++;
    }

    private static void parseVariables() {
        while (tokens.get(index).getType().equals("INTEGER") || tokens.get(index).getType().equals("CHARACTER")||tokens.get(index).getType().equals("LOGICAL")) {
            parseVariable();
        }
    }

    private static void parseVariable() {
        String dataType = tokens.get(index).getType();
        index++;
        if (!tokens.get(index).getType().equals("IDENTIFIER")) {
            System.out.println("error 3");
            System.exit(0);
        }
        String varName = tokens.get(index).getValue();
        index++;
        variableTable.put(varName, dataType);
        while (tokens.get(index).getValue().equals(",")) {
            index++;
            if (!tokens.get(index).getType().equals("IDENTIFIER")) {
                System.out.println(tokens.get(index).getType());
                System.out.println("error 4");
                System.exit(0);
            }
            varName = tokens.get(index).getValue();
            index++;
            variableTable.put(varName, dataType);
        }
        if (!tokens.get(index).getValue().equals(";")) {
            System.out.println(tokens.get(index).getType());
            System.out.println("error 5");
            System.exit(0);
        }
        index++;
    }

    

    private static void parseStatements() {
        while (!tokens.get(index).getType().equals("FINISH")) {
            parseStatement();
            
            
        }
    }

    private static void parseStatement() {
        String currentToken = tokens.get(index).getType();
        switch (currentToken) {
            case "PRINT":
                parsePrint();
                break;
            case "IF":
                parseIf();
                break;
            case "LOOPIF":
                parseLoop();
                break;
            case "IDENTIFIER":
                parseAssignment();
                break;
            default:
                System.out.println("error: invalid statement found");
                System.out.println(tokens.get(index).getType());
            System.out.println(index);
            System.out.println(tokens.get(index).getValue());
                System.exit(0);
        }

        
        if (!tokens.get(index).getType().equals("SEMICOLON")) {
            System.out.println("error: expected semicolon at end ossssssf statement");
            System.out.println(tokens.get(index).getType());
            System.out.println(index);
            System.out.println(tokens.get(index).getValue());
            System.exit(0);
        }

        index++;
        
    }

    private static void parseExpression() {
        parseTerm();
        while (isBinaryOp()) {
            String op = tokens.get(index).getValue();
            index++;
            parseTerm();
        }
    }
    
    private static boolean isBinaryOp() {
        String value = tokens.get(index).getValue();
        return value.equals(".plus.") || value.equals(".minus.") || value.equals(".mul.") || value.equals(".div.") || 
               value.equals(".and.") || value.equals(".or.") || value.equals(".eq.") || value.equals(".ne.") || 
               value.equals(".lt.") || value.equals(".le.") || value.equals(".gt.") || value.equals(".ge.");
    }

    private static void parseTerm() {
        if (tokens.get(index).getType().equals("IDENTIFIER")) {
            index++;
        } else if (tokens.get(index).getType().equals("INTEGER")) {
            index++;
        } else if (tokens.get(index).getType().equals("LEFT_BRACKET")) {
            index++;
            parseExpression();
            if (!tokens.get(index).getType().equals("RIGHT_BRACKET")) {
                System.out.println("error: expected right bracket");
                System.exit(0);
                System.out.println(tokens.get(index).getType());
            }
            index++;
        } else if (tokens.get(index).getType().equals("CHARACTER_CONSTANT")) {
            index++;
        } else if (tokens.get(index).getType().equals("INTEGER_CONSTANT")) {
                index++;
        } else {
            System.out.println("error");
            System.exit(0);
            System.out.println(tokens.get(index).getType());
        }
    }

    private static void parseIf() {
        index++;
    
        parseExpression();
    
        if (!tokens.get(index).getType().equals("THEN")) {
            System.out.println("error: expected THEN keyword");
            System.out.println(tokens.get(index).getType());
            System.exit(0);
        }
        index++;

        while (!tokens.get(index).getType().equals("ELSE")) {
        parseStatement();
        }
        if (tokens.get(index).getType().equals("ELSE")) {
            index++;
            while (!tokens.get(index).getType().equals("END_IF")) {
                parseStatement();
                }
            
        }
        if (!tokens.get(index).getType().equals("END_IF")) {
            System.out.println(tokens.get(index).getType());
            System.exit(0);
        }
        index++;
    }
    
    private static void parsePrint() {
        index++;
        parseExpression();
    }

   

    private static void parseLoop() {
        if (!tokens.get(index).getType().equals("LOOPIF")) {
            System.out.println("error 69");
            System.out.println(tokens.get(index).getType());
            return;
        }
        index++;

        parseExpression();

        if (!tokens.get(index).getType().equals("DO")) {
            System.out.println("error 434");
            System.out.println(tokens.get(index).getType());
            return;
        }
        index++;

        while (!tokens.get(index).getType().equals("END_LOOP")) {
            parseStatement();
        }

        if (!tokens.get(index).getType().equals("END_LOOP")) {
            System.out.println("error 4343433");
            System.out.println(tokens.get(index).getType());
            return;
        }
        index++;
    }

    private static void parseAssignment() {
        if (!tokens.get(index).getType().equals("IDENTIFIER")) {
            System.out.println(tokens.get(index).getType());
            System.out.println("error 434444334");
            return;
        }
        String varName = tokens.get(index).getValue();
        index++;
    
        if (!tokens.get(index).getType().equals("ASSIGN")) {
            System.out.println(tokens.get(index).getType());
            System.out.println("error 4343434343434343");
            return;
        }
        index++;
    
        parseExpression();
    
    }
}
