// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

// R2 = 0
// while R0 > 0
//   R0 = R0 - 1
//   R2 = R1 + R2
// return           // simulated with an infinite loop

// R2 = 0
@R2
M = 0

// while --R0 >= 0
(WHILE)
@R0
MD=M-1      // R0--
@END
D ; JLT     // if(R0<0) break;

// R2=R1+R2
@R1
D=M         // D=R1
@R2
M=D+M       // R2=R1+R2

@WHILE
0 ; JMP     // goto while

(END)
@END
0 ; JMP     // end with an infinite loop
