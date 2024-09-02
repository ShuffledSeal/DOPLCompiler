using System;
using System.Collections.Generic;

public class LexicalAnalyser
{
    private string input;
    private int currentPos;

    public LexicalAnalyser(string input)
    {
        this.input = input;
        this.currentPos = 0;
    }

    public List<Token> Analyze()
    {
        List<Token> tokens = new List<Token>();
        while (currentPos < input.Length)
        {
            char currentChar = GetNextChar();

            if (char.IsWhiteSpace(currentChar))
            {
                continue; // skip whitespace
            }
            else if (char.IsLetter(currentChar))
            {
                string identifier = ReadWhile(char.IsLetterOrDigit);
                if (IsKeyword(identifier))
                {
                    tokens.Add(new Token("KEYWORD", identifier));
                }
                else
                {
                    tokens.Add(new Token("IDENTIFIER", identifier));
                }
            }
            else if (char.IsDigit(currentChar))
            {
                string number = ReadWhile(char.IsDigit);
                tokens.Add(new Token("INTEGER_CONSTANT", number));
            }
            else if (currentChar == '"')
            {
                string characterConstant = ReadCharacterConstant();
                tokens.Add(new Token("CHARACTER_CONSTANT", characterConstant));
            }
            else if (currentChar == '.')
            {
                string operatorSymbol = ReadOperator();
                tokens.Add(new Token("OPERATOR", operatorSymbol));
            }
            else
            {
                string symbol = currentChar.ToString();
                if (symbol == "<" && PeekNextChar() == '-')
                {
                    symbol += GetNextChar(); // read '<-'
                }
                tokens.Add(new Token("SYMBOL", symbol));
            }
        }
        return tokens;
    }

    private char GetNextChar()
    {
        return currentPos < input.Length ? input[currentPos++] : '\0';
    }

    private char PeekNextChar()
    {
        return currentPos < input.Length ? input[currentPos] : '\0';
    }

    private string ReadWhile(Func<char, bool> condition)
    {
        int start = currentPos - 1;
        while (currentPos < input.Length && condition(input[currentPos]))
        {
            currentPos++;
        }
        return input.Substring(start, currentPos - start);
    }

    private string ReadCharacterConstant()
    {
        int start = currentPos;
        while (currentPos < input.Length && input[currentPos] != '"')
        {
            currentPos++;
        }
        currentPos++; // skip closing quote
        return input.Substring(start, currentPos - start - 1);
    }

    private string ReadOperator()
    {
        int start = currentPos - 1;
        while (currentPos < input.Length && input[currentPos] != ' ' && input[currentPos] != ';')
        {
            currentPos++;
        }
        return input.Substring(start, currentPos - start);
    }

    private bool IsKeyword(string word)
    {
        return word == "start" || word == "finish" || word == "integer" || word == "character" ||
               word == "logical" || word == "if" || word == "then" || word == "else" || 
               word == "endif" || word == "loopif" || word == "do" || word == "endloop" || 
               word == "print";
    }
}
