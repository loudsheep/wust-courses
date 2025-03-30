# Logic Expression Parser

The **Logic Expression Parser** is a Java-based program designed to evaluate logical formulas, generate truth tables, and analyze logical expressions using an interactive interface. The program allows users to define logical variables, assign truth values, and evaluate the result of a logical formula while also generating a truth table.

---

## Features
1. **Variable Detection**: Automatically identifies variables in a logical formula.
2. **Interactive Value Assignment**: Allows the user to input truth values (`0` or `1`) for each variable.
3. **Logical Operations**: Supports logical operations:
   - **AND (`&`)**
   - **OR (`|`)**
   - **NOT (`!`)**
   - **IMPLICATION (`>`)**
   - **EQUIVALENCE (`=`)**
4. **Truth Table Generation**: Dynamically generates and prints a truth table for the logical formula provided.
5. **Error Handling**: Handles invalid characters and variable misassignment gracefully.

---

## Prerequisites
- **Java Development Kit (JDK)**: Ensure you have JDK 8 or later installed.
- **IDE or Command-Line Interface**: Use any IDE like IntelliJ IDEA, Eclipse, or simply a terminal to run the program.

---

## Example
Input:
```
Enter formula: (A & B) > C
Enter value of 'A': 1
Enter value of 'B': 0
Enter value of 'C': 1

```

Output:
```
Result: true

Generated Truth Table
A B C A&B|C
0 0 0 0
0 0 1 1
0 1 0 0
0 1 1 1
1 0 0 0
1 0 1 1
1 1 0 1
1 1 1 1
```
