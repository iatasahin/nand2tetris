#!/bin/bash

# Help
if [ "$1" == "-h"  ] || [ "$1" == "--help"  ]; then
  echo "Usage: `basename $0` ./addressTo/program.asm"
  echo "Writes output to file ./addressTo/program.hack"
  echo "Example use:"
  echo "./assemble.sh ./add/Add.asm"
  exit 0
fi

# Main program, calls compiled java class file
java -cp "$(pwd)/../../out/production/nand2tetris/" assembler.HackAssembler "$(pwd)/$1"
