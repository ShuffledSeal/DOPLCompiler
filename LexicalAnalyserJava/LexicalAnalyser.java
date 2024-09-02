import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyser {

    public LexicalAnalyser() {
        tokens = new ArrayList<Token>();
    }
    public ArrayList<Token> getTokens(String fileName) {
        try {
            String aslFileName = fileName;
            if (!(aslFileName.substring(aslFileName.length() - 5, aslFileName.length()).equals(".dopl")))
                return null;
            BufferedReader aslFile = new BufferedReader(new FileReader(aslFileName));
            String s;
            while ((s = aslFile.readLine()) != null) {
                s = s.trim();
                Tokenizer(s);
            }
            aslFile.close();
            return tokens;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ArrayList<Token> tokens = new ArrayList<Token>();
    public void Tokenizer(String s) {
        String pattern = "start|finish|integer|character|logical|if|then|else|endif|loopif|do|endloop|print|<-|\\+|\\-|\\*|/|\\.eq\\.|\\.ne\\.|\\.lt\\.|\\.le\\.|\\.gt\\.|\\.ge\\.|\\.plus\\.|\\.minus\\.|\\.mul\\.|\\.div\\.|\\.and\\.|\\.or\\.|\\.not\\.|;|,|\\(|\\)|[a-zA-Z][a-zA-Z0-9_]*|[0-9]+|'[^']+'|\"[^\"]+\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        while (m.find()) {
            String matched = m.group();
            String token = TokenConverter(matched);
            if (token.equals("")) {
                System.out.println("ERROR");
                return;
            } else {
                tokens.add(new Token(token, matched));
            }
        }
    }

    
    private static String TokenConverter(String matched) {
        switch (matched) {
            case "start":
                return "START";
            case "finish":
                return "FINISH";
            case "integer":
                return "INTEGER";
            case "character":
                return "CHARACTER";
            case "logical":
                return "LOGICAL";
            case "if":
                return "IF";
            case "then":
                return "THEN";
            case "else":
                return "ELSE";
            case "endif":
                return "END_IF";
            case "loopif":
                return "LOOPIF";
            case "do":
                return "DO";
            case "endloop":
                return "END_LOOP";
            case "print":
                return "PRINT";
            case "<-":
                return "ASSIGN";
            case ".eq.":
                return "EQUAL";
            case ".ne.":
                return "NE";
            case ".lt.":
                return "LT";
            case ".le.":
                return "LE";
            case ".gt.":
                return "GT";
            case ".ge.":
                return "GE";
            case ".plus.":
                return "PLUS";
            case ".minus.":
                return "MINUS";
            case ".mul.":
                return "MUL";
            case ".div.":
                return "DIV";
            case ".and.":
                return "AND";
            case ".or.":
                return "OR";
            case ".not.":
                return "NOT";
            case ";":
                return "SEMICOLON";
            case ",":
                return "COMMA";
            case "(":
                return "LEFT_BRACKET";
            case ")":
                return "RIGHT_BRACKET";
                
            default:
                if (matched.matches("^[a-zA-Z][a-zA-Z0-9_]*")) {
                    return "IDENTIFIER";
                } else if (matched.matches("^[0-9]+")) {
                    return "INTEGER_CONSTANT";
                } else if (matched.matches("^'[^']+'|^\"[^\"]+\"")) {
                    return "CHARACTER_CONSTANT";
                }
                else {
                    return "";
                }
        }
    }

    
}
