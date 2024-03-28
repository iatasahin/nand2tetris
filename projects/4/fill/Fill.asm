// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// pseudocode:
// RAM[screenEnd]=SCREEN+8192;
// while(true){
//   // keyPressed ? fillScreen : emptyScreen;
//   if(anyKeyPressed)  RAM[fillValue]=-1;      // RAM[fillValue]=ffff
//   else               RAM[fillValue]=0;       // RAM[fillValue]=0000
//   // paint the screen
//   RAM[pixelIndex]=SCREEN;                    // screenMemoryMapStartAddress
//   do {
//     RAM[RAM[pixelIndex]]=RAM[fillValue];     // fillScreen or emptyScreen
//   } while(RAM[screenEnd] > ++RAM[pixelIndex]);
// }

// RAM[screenEnd]=SCREEN+8192;
@SCREEN
D=A
@8192
D=D+A
@screenEnd
M=D

// while(true){
//   // keyPressed ? fillScreen : emptyScreen;
//   if(anyKeyPressed)  RAM[fillValue]=-1;      // RAM[fillValue]=ffff
//   else               RAM[fillValue]=0;       // RAM[fillValue]=0000
(WHILE)
@fillValue
M=-1            // RAM[fillValue]=ffff  // fillScreen
@KBD
D=M             // D=RAM[KBD]: keyboardKeyValue
@AFTERELSE
D; JNE          // anyKeyPressed is true; skip else statement
//   else       // if( ! anyKeyPressed)
@fillValue
M=0             // RAM[fillValue]=0000  // emptyScreen

(AFTERELSE)
// RAM[pixelIndex]=SCREEN
@SCREEN
D=A
@pixelIndex
M=D

//   do {
//     RAM[RAM[pixelIndex]]=RAM[fillValue];       // fillScreen or emptyScreen
//   } while(RAM[screenEnd] > ++RAM[pixelIndex]);

//   do
//     RAM[RAM[pixelIndex]]=RAM[fillValue]
(DOWHILE)
@fillValue
D=M             // D=RAM[fillValue]     // ffff or 0000
@pixelIndex
A=M             // A=RAM[pixelIndex]
M=D             // RAM[RAM[pixelIndex]]=RAM[fillValue]   // fillScreen or emptyScreen

// ++RAM[pixelIndex]
@pixelIndex
MD=M+1          // increment the index

//   } while(RAM[screenEnd] > RAM[pixelIndex]);
@screenEnd
D=M-D           // D = screenEnd - pixelIndex
@DOWHILE
D; JGT          // if(screenEnd > pixelIndex) goto DOWHILE

// }
// do-while is finished; jump to outer while loop:
@WHILE
0; JMP
