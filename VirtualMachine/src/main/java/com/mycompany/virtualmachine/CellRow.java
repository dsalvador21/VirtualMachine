package com.mycompany.virtualmachine;

public class CellRow {

    private int cell;
    private int value;

    public CellRow(int cell, int value) {
        this.cell = cell;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCell() {
        return cell;
    }

    public int getValue() {
        return value;
    }

}
