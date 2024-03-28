// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// pseudocode:
// RAM[1]=24576;   // keyboardMemoryMapAddress, also screenMemoryMapEndAddress+1
// while(true){
//   // keyPressed ? fillScreen : emptyScreen;
//   if(anyKeyPressed)  RAM[2]=-1;      // RAM[2]=ffff
//   else               RAM[2]=0;       // RAM[2]=0000
//   // paint the screen
//   RAM[0]=16384;              // screenMemoryMapStartAddress
//   do
//     RAM[RAM[0]]=RAM[2]       // fillScreen or emptyScreen
//   while RAM[1] > ++RAM[0]

// RAM[1]=24576
@KBD
D=A
@R1
M=D

// while(true):
//   if(anyKeyPressed)
(WHILE)
@R2
M=-1            // RAM[2]=ffff  // fillScreen
@R1
A=M             // A=RAM[1]: keyboardMemoryMapAddress
D=M             // D=RAM[RAM[1]]: keyboardScanKeyValue
@AFTERELSE
D; JNE          // anyKeyPressed is true; skip else statement
//   else       // if( ! anyKeyPressed)
@R2
M=0             // RAM[2]=0000  // emptyScreen

(AFTERELSE)
// RAM[0]= 16384
@SCREEN
D=A
@R0
M=D

//   do
//     RAM[RAM[0]]=RAM[2]
//   while RAM[1] > ++RAM[0]

//   do
//     RAM[RAM[0]]=RAM[2]
(DOWHILE)
@R2
D=M             // D=RAM[2]     // ffff or 0000
@R0
A=M             // A=RAM[0]
M=D             // RAM[RAM[0]]=RAM[2]   // fillScreen or emptyScreen

// ++RAM[0]
@R0
MD=M+1          // increment the index

@R1
D=M-D           // i < length
@DOWHILE
D; JGT
//   while RAM[1] > RAM[0]

// do-while is finished; jump to outer while loop:
@WHILE
0; JMP
