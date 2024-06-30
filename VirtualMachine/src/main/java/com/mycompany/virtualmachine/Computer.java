package com.mycompany.virtualmachine;

import java.util.Arrays;

public class Computer {
    
    String[] ROM;
    int[] RAM;
    int A, D;
    int PC;
    int MAX_INSTRUCTIONS = 10000;
    int instructionCount;

    public Computer(int ROM_SIZE, int RAM_SIZE) {
        ROM = new String[ROM_SIZE];
        RAM = new int[RAM_SIZE];
        A = 0;
        D = 0;
        PC = 0;
        instructionCount = 0;
    }

    public void loadROM(int cell, String instruction) {
        if (cell >= 0 && cell < ROM.length) {
            ROM[cell] = instruction;
        }
    }

    public void updateCell(int cell, int value) {
        if (cell >= 0 && cell < RAM.length) {
            RAM[cell] = value;
        }
    }

    public boolean executeInstruction() {
        String instruction = ROM[PC];

        if (instruction == null || instructionCount >= MAX_INSTRUCTIONS) {
            return false;
        }

        boolean cInstruction = charToBoolean(instruction.charAt(0)); // A or C type?
        boolean a = charToBoolean(instruction.charAt(3)); // A or M?
        boolean c1 = charToBoolean(instruction.charAt(4));
        boolean c2 = charToBoolean(instruction.charAt(5));
        boolean c3 = charToBoolean(instruction.charAt(6));
        boolean c4 = charToBoolean(instruction.charAt(7));
        boolean c5 = charToBoolean(instruction.charAt(8));
        boolean c6 = charToBoolean(instruction.charAt(9));
        boolean d1 = charToBoolean(instruction.charAt(10)); // A
        boolean d2 = charToBoolean(instruction.charAt(11)); // D
        boolean d3 = charToBoolean(instruction.charAt(12)); // M
        boolean j1 = charToBoolean(instruction.charAt(13)); // JLT
        boolean j2 = charToBoolean(instruction.charAt(14)); // JEQ
        boolean j3 = charToBoolean(instruction.charAt(15)); // JGT

        if (cInstruction) {
            int secondInput = A;
            int output = 0;

            if (a) {
                secondInput = RAM[A];
            }

            // Comp.
            if (c1 && !c2 && c3 && !c4 && c5 && !c6) {
                output = 0;
            } else if (c1 && c2 && c3 && c4 && c5 && c6) {
                output = 1;
            } else if (c1 && c2 && c3 && !c4 && c5 && !c6) {
                output = -1;
            } else if (!c1 && !c2 && c3 && c4 && !c5 && !c6) {
                output = D;
            } else if (c1 && c2 && !c3 && !c4 && !c5 && !c6) {
                output = secondInput;
            } else if (!c1 && !c2 && c3 && c4 && !c5 && c6) {
                output = ~D & 0xFFFF;
            } else if (c1 && c2 && !c3 && !c4 && !c5 && c6) {
                output = ~secondInput & 0xFFFF;
            } else if (!c1 && !c2 && c3 && c4 && c5 && c6) {
                output = -D;
            } else if (c1 && c2 && !c3 && !c4 && c5 && c6) {
                output = -secondInput;
            } else if (!c1 && c2 && c3 && c4 && c5 && c6) {
                output = D + 1;
            } else if (c1 && c2 && !c3 && c4 && c5 && c6) {
                output = secondInput + 1;
            } else if (!c1 && !c2 && c3 && c4 && c5 && !c6) {
                output = D - 1;
            } else if (c1 && c2 && !c3 && !c4 && c5 && !c6) {
                output = secondInput - 1;
            } else if (!c1 && !c2 && !c3 && !c4 && c5 && !c6) {
                output = D + secondInput;
            } else if (!c1 && c2 && !c3 && !c4 && c5 && c6) {
                output = D - secondInput;
            } else if (!c1 && !c2 && !c3 && c4 && c5 && c6) {
                output = secondInput - D;
            } else if (!c1 && !c2 && !c3 && !c4 && !c5 && !c6) {
                output = D & secondInput;
            } else if (!c1 && c2 && !c3 && c4 && !c5 && c6) {
                output = D | secondInput;
            }

            // Dest.
            if (d1) {
                A = output;
            }

            if (d2) {
                D = output;
            }

            if (d3) {
                RAM[A] = output;
            }

            // Jump.
            if ((output < 0 && j1) || (output == 0 && j2) || (output > 0 && j3)) {
                PC = A;
            } else {
                PC++;
            }
        } else {
            A = Integer.parseInt(instruction, 2);
            PC++;
        }
        
        instructionCount++;
        return true;
    }

    private boolean charToBoolean(char c) {
        return c != '0';
    }

}
