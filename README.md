# DOPL Compiler

## Overview

The DOPL Compiler is a C# program designed to translate code written in DOPL into C# code and automatically executes it.

## Features

- **Lexical Analysis**: Converts raw DOPL code into tokens.
- **Parsing**: Parses tokens according to DOPL grammar, checking for syntax errors.
- **Intermediate Representation (IR)**: Generates IR instructions from the parsed tokens.
- **Code Generation**: Converts IR instructions into valid C# code.
- **Compilation and Execution**: Uses the .NET CLI to compile and execute the generated C# code.

## Prerequisites

- .NET SDK installed on your machine.
- A terminal or command prompt to run the compiler and bash scripts.

## How to Use

### 1. Prepare a DOPL Source File

Create a DOPL source file, e.g., `example.dopl`, with content like this:

```dopl
start
    integer num;
    integer sum;
    sum <- 0;
    num <- 1;
    loopif num .lt. 10 do
        sum <- sum .plus. num;
        num <- num .plus. 1;
    endloop;
    if sum .gt. 10 then
        print sum;
    else
        print num;
    endif;
    print "Done!";
finish
