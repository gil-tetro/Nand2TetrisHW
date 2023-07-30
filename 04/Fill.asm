// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.


(START)     // intilialize the index for the pixels
  @8192     //32 * 256 is the number of 16 bit pixel lines it takes to cover the screen black
  D=A       // store the current adress at the register
  @index
  M=D

(LOOP)
  @index
  M=M-1     // index = index - 1      
  D=M
  @START
  D;JLT     // if index < 0, return to start and reset it (screen overflow)
  
  @KBD
  D=M
  @WHITE    // if memory at kbd == 0, no key is pressed - color the pixel in white.
  D;JEQ
  @BLACK    // else: color the pixel in black
  0;JMP

(WHITE)
  @SCREEN   // load the screen starting index
  D=A
  @index
  A=D+M     // add the current index to the screen address to color 16 pixels in white
  M=0      // sets addres value to 0 which encodes white
  @LOOP    // loop in order to subtract the index and move to the next pixel set
  0;JMP

(BLACK)
  @SCREEN   // load the screen starting index
  D=A
  @index
  A=D+M     // add the current index to the screen address to color 16 pixels in black
  M=-1      // sets value to -1 and colors 16 bits in black
  @LOOP     // loop in order to subtract the index and move to the next pixel set
  0;JMP