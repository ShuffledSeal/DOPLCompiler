using System;
using System.Collections.Generic;
using System.Text;

public class CodeGenerator
{
    private List<IRInstruction> instructions;

    public CodeGenerator(List<IRInstruction> instructions)
    {
        this.instructions = instructions;
    }

    public string Generate()
    {
        StringBuilder code = new StringBuilder();
        foreach (var instruction in instructions)
        {
            switch (instruction.Operation)
            {
                case "DECLARE":
                    code.AppendLine($"{TranslateDataType(instruction.Operand1)} {instruction.Operand2};");
                    break;
                case "ASSIGN":
                    code.AppendLine($"{instruction.Operand1} = {TranslateExpression(instruction.Operand2)};");
                    break;
                case "IF":
                    code.AppendLine($"if ({TranslateExpression(instruction.Operand1)}) {{");
                    break;
                case "ELSE":
                    code.AppendLine("} else {");
                    break;
                case "ENDIF":
                    code.AppendLine("}");
                    break;
                case "LOOPIF":
                    code.AppendLine($"while ({TranslateExpression(instruction.Operand1)}) {{");
                    break;
                case "ENDLOOP":
                    code.AppendLine("}");
                    break;
                case "PRINT":
                    code.AppendLine($"Console.WriteLine({TranslateExpression(instruction.Operand1)});");
                    break;
                default:
                    throw new Exception($"Unknown IR operation: {instruction.Operation}");
            }
        }
        return code.ToString();
    }

    private string TranslateDataType(string doplType)
    {
        return doplType switch
        {
            "integer" => "int",
            "character" => "char",
            "logical" => "bool",
            _ => throw new Exception($"Unknown data type: {doplType}")
        };
    }

    private string TranslateExpression(string expression)
    {
        // Translate DOPL operators to C# operators
        return expression
            .Replace(".lt.", "<")
            .Replace(".gt.", ">")
            .Replace(".le.", "<=")
            .Replace(".ge.", ">=")
            .Replace(".eq.", "==")
            .Replace(".ne.", "!=")
            .Replace(".plus.", "+")
            .Replace(".minus.", "-")
            .Replace(".mul.", "*")
            .Replace(".div.", "/")
            .Replace(".and.", "&&")
            .Replace(".or.", "||")
            .Replace(".not.", "!");
    }
}
