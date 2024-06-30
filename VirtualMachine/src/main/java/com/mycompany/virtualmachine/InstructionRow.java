package com.mycompany.virtualmachine;

public class InstructionRow {

    private int number;
    private String instruction;

    public InstructionRow(int number, String instruction) {
        this.number = number;
        this.instruction = instruction;
    }

    public int getNumber() {
        return number;
    }

    public String getInstruction() {
        return instruction;
    }
}
