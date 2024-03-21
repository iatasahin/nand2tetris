// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// pseudocode:
// RAM[1]=3*2^13 (24576);   // screenMemoryMapEndAddress+1, also keyboardMemoryMapAddress
// while(true){
//   // keyPressed ? fillScreen : emptyScreen;
//   if(anyKeyPressed)  RAM[2]=-1;      // RAM[2]=ffff
//   else               RAM[2]=0;       // RAM[2]=0000
//   // paint the screen
//   RAM[0]=2^13 (8192);    // screenMemoryMapStartAddress
//   do
//     RAM[RAM[0]]=RAM[2]       // fillScreen or emptyScreen
//   while RAM[1] > ++RAM[0]

// RAM[1]=24576
@24576
D=A
@1
M=D

// while(true):
//   if(anyKeyPressed)
@2          // ROM[4]
M=-1            // RAM[2]=ffff  // fillScreen
@1
A=M             // A=RAM[1]: keyboardMemoryMapAddress
D=M             // D=RAM[RAM[1]]: keyboardScanKeyValue
@13         // ROM addressing; address of the first line after the else statement
D; JNE          // anyKeyPressed is true; skip else statement
//   else       // if( ! anyKeyPressed)
@2
M=0             // RAM[2]=0000  // emptyScreen

// RAM[0]= 2^13 (8192) ; start
@8192       // ROM[13]
D=A
@0
M=D

//   do
//     RAM[RAM[0]]=RAM[2]
//   while RAM[1] > ++RAM[0]

//   do
//     RAM[RAM[0]]=RAM[2]
@2          // ROM[17]
D=M             // D=RAM[2]     // ffff or 0000
@0
A=M             // A=RAM[0]
M=D             // RAM[RAM[0]]=RAM[2]   // fillScreen or emptyScreen

// ++RAM[0]
@0
MD=M+1          // increment the index

@1
D=M-D           // i < length
@17         // ROM addressing; address of the first line of the do-while loop
D; JGT
//   while RAM[1] > RAM[0]

// do-while is finished; jump to outer while loop:
@4          // ROM addressing; address of the first line of the while loop
0; JMP
