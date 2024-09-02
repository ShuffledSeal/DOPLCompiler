using System;
using System.Collections.Generic;
using System.Text;

public class Parser
{
    private List<Token> tokens;
    private int currentTokenIndex;
    private List<IRInstruction> irInstructions;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.irInstructions = new List<IRInstruction>();
    }

    public void Parse()
    {
        if (GetNextToken().Value != "start")
        {
            throw new Exception("Program must start with 'start'");
        }

        ParseDeclarations();
        ParseStatements();

        if (GetNextToken().Value != "finish")
        {
            throw new Exception("Program must end with 'finish'");
        }
    }

    public List<IRInstruction> GetIRInstructions()
    {
        return irInstructions;
    }

    private void ParseDeclarations()
    {
        while (LookAhead().Value == "integer" || LookAhead().Value == "character" || LookAhead().Value == "logical")
        {
            Token typeToken = GetNextToken();
            while (true)
            {
                Token identifierToken = GetNextToken();
                irInstructions.Add(new IRInstruction("DECLARE", typeToken.Value, identifierToken.Value, null));
                if (LookAhead().Value == ",")
                {
                    GetNextToken(); // consume ','
                }
                else
                {
                    break;
                }
            }
            if (GetNextToken().Value != ";")
            {
                throw new Exception("Missing ';' after declaration");
            }
        }
    }

    private void ParseStatements()
    {
        while (LookAhead().Value != "finish" && LookAhead().Value != "endloop")
        {
            ParseStatement();
        }
    }

    private void ParseStatement()
    {
        Token token = LookAhead();
        Console.WriteLine($"Parsing statement starting with token: {token.Value}");

        // Check for control statements first
        switch (token.Value)
        {
            case "if":
                ParseIfStatement();
                break;
            case "loopif":
                ParseLoopStatement();
                break;
            case "print":
                ParsePrintStatement();
                break;
            case ";":  // Skip semicolons that just terminate statements
                GetNextToken();
                break;
            default:
                ParseAssignmentStatement();
                break;
        }
    }

    private void ParseAssignmentStatement()
    {
        Token identifierToken = GetNextToken();
        Console.WriteLine($"Parsing assignment for: {identifierToken.Value}");

        Token assignToken = GetNextToken();
        if (assignToken.Value != "<-")
        {
            throw new Exception($"Missing '<-' in assignment, found: {assignToken.Value}");
        }
        string expression = ParseExpression();
        irInstructions.Add(new IRInstruction("ASSIGN", identifierToken.Value, expression, null));
        
        Token semicolonToken = GetNextToken();
        if (semicolonToken.Value != ";")
        {
            throw new Exception($"Missing ';' after assignment, found: {semicolonToken.Value}");
        }
    }

    private void ParseIfStatement()
    {
        GetNextToken(); // consume 'if'
        string condition = ParseExpression();
        irInstructions.Add(new IRInstruction("IF", condition, null, null));

        if (GetNextToken().Value != "then")
        {
            throw new Exception("Missing 'then' in if statement");
        }

        ParseStatements();

        if (LookAhead().Value == "else")
        {
            GetNextToken(); // consume 'else'
            irInstructions.Add(new IRInstruction("ELSE", null, null, null));
            ParseStatements();
        }

        if (GetNextToken().Value != "endif")
        {
            throw new Exception("Missing 'endif' after if statement");
        }

        irInstructions.Add(new IRInstruction("ENDIF", null, null, null));
    }

    private void ParseLoopStatement()
{
    GetNextToken(); // consume 'loopif'
    string condition = ParseExpression();

    Token doToken = LookAhead();
    if (doToken.Value != "do")
    {
        throw new Exception("Missing 'do' in loop statement");
    }

    GetNextToken(); // consume 'do'

    irInstructions.Add(new IRInstruction("LOOPIF", condition, null, null));

    ParseStatements();

    Token endloopToken = GetNextToken();
    if (endloopToken.Value != "endloop")
    {
        throw new Exception("Missing 'endloop' after loop statement");
    }

    irInstructions.Add(new IRInstruction("ENDLOOP", null, null, null));
}

    private void ParsePrintStatement()
    {
        GetNextToken(); // consume 'print'
        string expression = ParseExpression();
        irInstructions.Add(new IRInstruction("PRINT", expression, null, null));
        if (GetNextToken().Value != ";")
        {
            throw new Exception("Missing ';' after print statement");
        }
    }

    private string ParseExpression()
    {
        StringBuilder expression = new StringBuilder();
        
        Token token = LookAhead();
        while (token != null && (token.Type == "IDENTIFIER" || token.Type == "INTEGER_CONSTANT" || 
               token.Type == "CHARACTER_CONSTANT" || token.Type == "OPERATOR" || token.Type == "SYMBOL"))
        {
            expression.Append(token.Value);
            GetNextToken(); // consume the token
            token = LookAhead();

            if (token != null && (token.Type == "SYMBOL" && token.Value == ";"))
                break;
        }

        return expression.ToString();
    }

    private Token GetNextToken()
    {
        return currentTokenIndex < tokens.Count ? tokens[currentTokenIndex++] : null;
    }

    private Token LookAhead()
    {
        return currentTokenIndex < tokens.Count ? tokens[currentTokenIndex] : null;
    }
}
