## About

This CPU Visual Simulator allows you to enter and visualize the execution of assembly language code.
Instructions and numeric data can be inserted or modified directly in RAM. It is possible to define "labels"
(identifiers to be used in place of memory addresses) in the greyed table on the left of the RAM: these labels
can then be used as parameters in jump instructions, or as variable identifiers. At any time, it is possible to
switch between symbolic and binary representations. It is also possible to directly modify the Program Counter,
in order to set the next instruction that will be executed.

![Screenshot](images/screenshot.png?raw=true)

## How to run

This Educational CPU Visual Simulator is implemented in Java, and packaged as a single .jar file that you need to download from here.
In addition to the .jar file, you’ll need the Java Runtime Environment (JRE) to run it. An easy way to find out if you have the JRE already installed, is to double-click the downloaded .jar file:
if the simulator doesn't start or if you are prompted to select an application to open the file, you might download and install the JRE from Oracle's website - https://www.oracle.com/java/technologies/javase-jre8-downloads.html


## Instruction set

#### Data Flow

| Direct Operand | Immediate Operand | Description |
|:--------------:|:-----------------:|:------------|
| LOD X          | LOD #X            | Load content of Memory Location X (or number #X) into the Accumulator. |
| STO X          |                   | Store the value of the Accumulator into Memory Location X. |

#### Control Flow

| Direct Operand | Immediate Operand | Description |
|:--------------:|:-----------------:|:------------|
| JMP P          |                   | Unconditional jump to the instruction at location P. |
| JZ P           |                   | Jump on Zero: if Flag Z is set, go to instruction number P, otherwise go to the next instruction. |
| JNZ P          |                   | Jump on Not Zero: if Flag Z is cleared, go to instruction number P, otherwise, go to the next instruction. |
| JN P           |                   | Jump on Negative: if Flag N is set, go to instruction number P, otherwise go to the next instruction. |
| JNN P          |                   | Jump on Not Negative: if Flag N is cleared, go to instruction number P, otherwise go to the next instruction. |
| NOP            |                   | No Operation, go to the next instruction. |
| HLT            |                   | Halt execution. |

#### Arithmetic-logic

| Direct Operand | Immediate Operand | Description |
|:--------------:|:-----------------:|:------------|
| ADD X          | ADD #X            | Add content of Memory Location X (or number #X) to the Accumulator. Flags are updated. |
| SUB X          | SUB #X            | Subtract content of Memory Location X (or number #X) from the Accumulator. Flags are updated. |
| MUL X          | MUL #X            | Multiply the Accumulator by the content of Memory Location X (or number #X). Flags are updated. |
| DIV X          | DIV #X            | Divide the Accumulator by the content of Memory Location X (or number #X). Flags are updated. |
| AND X          | AND #X            | Bitwise AND between the Accumulator and the content of Memory Location X (or number #X). Flags are updated. |
| CMP X          | CMP #X            | Subtract content of Memory Location X (or number #X) from the Accumulator. Flags are updated, but the content of the Accumulator is not modified. |
| NOT X          | NOT #X            | Bitwise NOT of the content of Memory Location X (or number #X). The result is stored in the Accumulator. |

## Examples

High level IF-THEN-ELSE example and its translation:

![IF-THEN-ELSE](images/if-then-else-example.png?raw=true)

High-level WHILE-DO example and its translation:

![WHILE-DO](images/while-do-example.png?raw=true)

## Copyright notice

Cengage Learning Inc. Reproduced by permission.
http://www.cengage.com/permissions

This simulator can be used exclusively for non-commercial, educational activities.
This CPU Visual Simulator was derived (modified and extended - see credits) in 2021 from the original PIPPIN Applet (© 1998 PWS Publishing Company), with permission from Cengage Learning Inc. Other deletions, additions, edits, changes to, or derivatives of the Cengage Material can only be made with Cengage’s express written approval, email sufficing.

## Credits

Original PIPPIN applet:
* Prof. Stuart Hirshfield and Prof. Rick Decker (Designers)

CPU Visual Simulator extension, carried out with an Open Pedagogy / OER-enabled process:
* Renato Cortinovis, PhD - Project coordinator
* Nicola Preda - Porting to a Java application, initial new functionalities, and documentation
* Jonathan Cancelli - Implementation of the great majority of new functionalities
* Alessandro Belotti - Implementation of visualizations and animations
* Davide Riva - Initial refactoring
* MariaPia Cavarretta, Giordano Cortinovis, Giovanni Ingargiola, Alessandro Suru, Piero Andrea Sileo and others - Documentation

Additional information:

* Cortinovis, R. (2021). An educational CPU Visual Simulator, Proceedings of the 32nd Annual Workshop of the Psychology of Programming Interest Group (PPIG).