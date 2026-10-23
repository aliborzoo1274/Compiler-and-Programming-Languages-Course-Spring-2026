# MOL Compiler

A staged compiler for **MOL**, a small educational programming language developed for the
Compiler and Programming Languages course (Spring 2026).

The project follows the major steps of a compiler pipeline:

```text
MOL source (.mol)
        |
        v
ANTLR lexer/parser
        |
        v
Abstract syntax tree (AST)
        |
        +--> name analysis
        |
        +--> type analysis
        |
        v
Jasmin assembly (.j)
        |
        v
JVM bytecode (.class)  [when assembled with Jasmin]
```

## Features

The current Phase 4 implementation includes:

- ANTLR 4.13.2 lexer and parser generation
- An AST with declarations, expressions, statements, and control-flow nodes
- Modules and structs
- Fields, methods, constructors, parameters, and access modifiers
- Mutable and immutable variables and fields using the `mut` modifier
- Primitive types including `int`, `float`, `double`, `bool`, `char`, and `void`
- Arithmetic, comparison, unary, member-access, and method-call expressions
- Conditional statements with `elif` and `else`
- `while` and `for` loops
- `break`, `continue`, `return`, and `output`
- Name analysis for declarations, visibility, initialization, and reachability
- Type analysis for expressions, assignments, conditions, returns, and method calls
- Jasmin assembly generation for JVM execution

## Repository structure

```text
.
├── Phase-1/                 # Grammar, parsing, AST construction, and AST printing
├── Phase-2/                 # AST plus initial semantic-analysis infrastructure
├── Phase-3/                 # Name and type analysis
├── Phase-4/                 # Complete pipeline and Jasmin code generation
├── samples/                 # Valid MOL programs and semantic-error examples
├── CodeGenOutput/           # Generated Jasmin, assembled classes, and Jasmin tooling
├── utilities/
│   └── antlr-4.13.2-complete.jar
└── .vscode/                 # Java/ANTLR project and launch configurations
```

Phase 4 is the active implementation used by the VS Code project configuration. The earlier
phases are preserved as milestones and reference implementations for the compiler's
incremental development.

## Requirements

- Java Development Kit (JDK) 8 or newer
- PowerShell on Windows, or an equivalent shell with the classpath separator adjusted for
  your platform
- The bundled [ANTLR 4.13.2 complete JAR](utilities/antlr-4.13.2-complete.jar)
- Optional: the bundled Jasmin assembler at
  [CodeGenOutput/jasmin.jar](CodeGenOutput/jasmin.jar)
- Optional: Visual Studio Code with the Extension Pack for Java and an ANTLR extension

No Maven or Gradle build is required. All dependencies needed by the compiler are included
in the repository.

## Quick start on Windows

Run these commands from the repository root.

### 1. Compile Phase 4

```powershell
$build = "build"
$antlr = "utilities\antlr-4.13.2-complete.jar"
$sources = Get-ChildItem -Recurse -Filter *.java "Phase-4\src" |
    ForEach-Object { $_.FullName }

New-Item -ItemType Directory -Force $build | Out-Null
javac -cp $antlr -d $build $sources
```

The command compiles the Phase 4 Java sources, including the generated ANTLR parser and
lexer, into the local `build` directory.

### 2. Compile a MOL program to Jasmin

```powershell
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\sample1.mol
```

The compiler reads the source file, runs parsing and semantic analysis, and writes generated
Jasmin files to `CodeGenOutput`. For example, `sample1.mol` produces a `Main.j` file.

`MOL` expects exactly one positional argument: the path to a `.mol` source file.

### 3. Assemble and run the generated JVM class

If Jasmin is available, assemble the generated file and run the resulting class:

```powershell
java -jar CodeGenOutput\jasmin.jar -d CodeGenOutput CodeGenOutput\Main.j
java -cp CodeGenOutput Main
```

The generated class depends on the program's module names. For a different entry module,
replace `Main.j` and `Main` with the generated module name.

## Running the included examples

The repository includes valid programs:

```powershell
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\sample1.mol
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\sample2.mol
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\sample3.mol
```

It also includes examples designed to exercise semantic diagnostics:

```text
samples/
├── Name/
│   ├── duplicate.mol
│   ├── notDeclaredMethod.mol
│   ├── notDeclaredVar.mol
│   ├── private.mol
│   ├── uninitialized.mol
│   └── unreachable.mol
└── Type/
    ├── immutableAssignment.mol
    ├── invalidConditionType.mol
    ├── invalidMethodArgs.mol
    ├── returnTypeMismatch.mol
    └── typeMismatch.mol
```

For example:

```powershell
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\Name\uninitialized.mol
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\Type\typeMismatch.mol
```

## MOL at a glance

MOL uses `module ... begin ... end` blocks. A minimal program looks like this:

```mol
module Main begin
    public void main() begin
        mut int value = 10;
        value = value + 5;
        output value;
    end
end
```

Modules may contain fields, methods, constructors, and included modules:

```mol
module Account begin
    private int balance;

    public Account(int initialBalance) begin
        this.balance = initialBalance;
    end

    public int getBalance() begin
        return this.balance;
    end
end

module Main includes Account begin
    public void main() begin
        mut Account(100) account;
        output account.getBalance();
    end
end
```

### Statements and expressions

The grammar supports:

- Variable declarations: `int count;`, `mut int count = 0;`
- Assignments: `count = count + 1;`
- Output: `output count;`
- Conditions: `if`, `elif`, and `else`
- Loops: `while` and C-style `for`
- Loop control: `break` and `continue`
- Returns: `return;` or `return expression;`
- Calls and member access: `object.method(argument)`
- Operators: `+`, `-`, `*`, `/`, `%`, `<`, `>`, `<=`, `>=`, `==`, and `!=`
- Line comments beginning with `//`

The authoritative language definition is the Phase 4 grammar:
[Phase-4/src/main/grammar/MOL.g4](Phase-4/src/main/grammar/MOL.g4).

## Compiler pipeline

The Phase 4 entry point is [Phase-4/src/MOL.java](Phase-4/src/MOL.java). It:

1. Reads the input file with ANTLR's character-stream API.
2. Tokenizes it with `MOLLexer`.
3. Parses it with `MOLParser`.
4. Retrieves the AST from the parser.
5. Runs `NameAnalyzer`.
6. Runs `TypeAnalyzer`.
7. Runs `CodeGenerator`, writing output under `CodeGenOutput`.

Important implementation areas:

- [Phase-4/src/main/grammar/](Phase-4/src/main/grammar/) - grammar and generated parser sources
- [Phase-4/src/main/ast/](Phase-4/src/main/ast/) - AST node model
- [Phase-4/src/main/symbolTable/](Phase-4/src/main/symbolTable/) - symbol and semantic models
- [Phase-4/src/main/visitor/](Phase-4/src/main/visitor/) - analysis, printing, and code generation

## Regenerating the ANTLR sources

The generated lexer and parser are committed under each phase's `src/main/grammar`
directory. If you edit the grammar and have the ANTLR JAR available, regenerate the Phase 4
sources from the repository root:

```powershell
java -jar utilities\antlr-4.13.2-complete.jar `
    -visitor -listener `
    -o Phase-4\src\main\grammar `
    Phase-4\src\main\grammar\MOL.g4
```

Review the generated output before compiling. The VS Code workspace is configured to use
Phase 4 as its Java source path and `utilities/antlr-4.13.2-complete.jar` as its referenced
library.

## Troubleshooting

### `ClassNotFoundException: MOL`

Compile from the repository root and make sure the `build` directory is included in the
classpath:

```powershell
java -cp "build;utilities\antlr-4.13.2-complete.jar" MOL samples\sample1.mol
```

### ANTLR classes cannot be found

Pass the bundled ANTLR JAR to both `javac` and `java` with `-cp`, as shown in the quick-start
commands.

### Generated files appear in the wrong directory

`MOL` writes to `CodeGenOutput` relative to the current working directory. Run the command
from the repository root, or use an absolute working directory before invoking the compiler.

### A sample reports an error

The files under `samples/Name` and `samples/Type` intentionally contain invalid programs.
Use the files named `sample1.mol` through `sample8.mol` for valid examples.

## Academic context

This repository is organized as a sequence of compiler-construction phases for the
Compiler and Programming Languages course. The assignment PDFs are retained in their
corresponding phase directories:

- [Phase 1 assignment](Phase-1/document-mol.pdf)
- [Phase 2 assignment](Phase-2/plc-ca2.pdf)
- [Phase 3 assignment](Phase-3/PLC-CA3-Spring05.pdf)
- [Phase 4 assignment](Phase-4/PLC-Spring05-Phase4.pdf)
