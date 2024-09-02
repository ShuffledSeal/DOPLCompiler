using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;

public class Compiler
{
    public static void Main(string[] args)
    {
        // Prompt the user for the DOPL source file name
        Console.Write("Enter the DOPL source file name: ");
        string inputFilePath = Console.ReadLine();

        if (string.IsNullOrWhiteSpace(inputFilePath))
        {
            Console.WriteLine("No file name provided.");
            return;
        }

        if (!File.Exists(inputFilePath))
        {
            Console.WriteLine($"The file '{inputFilePath}' does not exist.");
            return;
        }

        // Read the DOPL code from the file
        string input = File.ReadAllText(inputFilePath);

        // Lexical analysis
        LexicalAnalyser lexer = new LexicalAnalyser(input);
        List<Token> tokens = lexer.Analyze();

        // Parsing
        Parser parser = new Parser(tokens);
        parser.Parse();
        List<IRInstruction> irInstructions = parser.GetIRInstructions();

        // Code generation
        CodeGenerator codeGenerator = new CodeGenerator(irInstructions);
        string generatedCode = codeGenerator.Generate();

        // Display the generated C# code
        Console.WriteLine("Generated C# Code:");
        Console.WriteLine(generatedCode);

        // Save the generated code to a file
        string outputPath = "GeneratedCode.cs";
        File.WriteAllText(outputPath, generatedCode);
        Console.WriteLine($"Generated code saved to {outputPath}");

        // Prepare the dotnet project directory
        string projectPath = "GeneratedCodeProject";
        if (!Directory.Exists(projectPath))
        {
            Process.Start("dotnet", $"new console -o {projectPath}").WaitForExit();
        }

        // Replace the default Program.cs with the generated code
        File.WriteAllText(Path.Combine(projectPath, "Program.cs"), generatedCode);

        // Build and run the generated code using dotnet CLI
        ProcessStartInfo processInfo = new ProcessStartInfo("dotnet", "run")
        {
            RedirectStandardOutput = true,
            UseShellExecute = false,
            CreateNoWindow = true,
            WorkingDirectory = projectPath
        };

        Process process = Process.Start(processInfo);
        process.WaitForExit();

        if (process.ExitCode == 0)
        {
            Console.WriteLine("Execution output:");
            Console.WriteLine(process.StandardOutput.ReadToEnd());
        }
        else
        {
            Console.WriteLine("Compilation or execution failed.");
        }
    }
}
