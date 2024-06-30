package com.mycompany.virtualmachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class PrimaryController implements Initializable {

    Computer computer;
    ObservableList<InstructionRow> instructions = FXCollections.observableArrayList();
    ObservableList<CellRow> memoryCells = FXCollections.observableArrayList();

    @FXML
    private Button load_file;
    @FXML
    private TableView<InstructionRow> instructionsTable;
    @FXML
    private TableColumn<InstructionRow, Integer> numberColumn;
    @FXML
    private TableColumn<InstructionRow, String> instructionColumn;
    @FXML
    private Button execute_instruction;
    @FXML
    private Button fast_execution;
    @FXML
    private TableView<CellRow> memoryTable;
    @FXML
    private TableColumn<CellRow, Integer> cellColumn;
    @FXML
    private TableColumn<CellRow, Integer> valueColumn;
    @FXML
    private TextField ARegister;
    @FXML
    private TextField DRegister;
    @FXML
    private TextField PC;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int ROM_SIZE = 32768;
        int RAM_SIZE = 16384;
        computer = new Computer(ROM_SIZE, RAM_SIZE);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));

        for (int i = 0; i < computer.ROM.length; i++) {
            instructions.add(new InstructionRow(i, ""));
        }

        instructionsTable.setItems(instructions);

        cellColumn.setCellValueFactory(new PropertyValueFactory<>("cell"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        valueColumn.setOnEditCommit(event -> {
            CellRow cellRow = event.getRowValue();
            cellRow.setValue(event.getNewValue());
            computer.RAM[cellRow.getCell()] = event.getNewValue();
        });

        for (int i = 0; i < computer.RAM.length; i++) {
            memoryCells.add(new CellRow(i, 0));
        }

        memoryTable.setItems(memoryCells);
        memoryTable.setEditable(true);

        execute_instruction.setVisible(false);
        fast_execution.setVisible(false);
        fast_execution.setOnAction(e -> {
            fastExecution();
            execute_instruction.setDisable(true);
            fast_execution.setDisable(true);
        });
    }

    @FXML
    private void loadFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = App.getStage();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hack files", "*.hack")
        );
        File file = fileChooser.showOpenDialog(stage);

        Scanner scanner = new Scanner(file);
        int i = 0;

        while (scanner.hasNextLine()) {
            String instruction = scanner.nextLine();
            computer.loadROM(i, instruction);
            instructions.set(i, new InstructionRow(i, computer.ROM[i]));
            i++;
        }

        execute_instruction.setVisible(true);
        fast_execution.setVisible(true);

    }

    @FXML
    private void executeInstruction() {
        markLine(computer.PC);
        PC.setText(String.valueOf(computer.PC));
        ARegister.setText(String.valueOf(computer.A));
        DRegister.setText(String.valueOf(computer.D));
        memoryCells.clear();
        for (int i = 0; i < computer.RAM.length; i++) {
            memoryCells.add(new CellRow(i, computer.RAM[i]));
        }
        computer.executeInstruction();
    }

    private void markLine(int lineNumber) {
        instructionsTable.getSelectionModel().clearSelection();
        instructionsTable.getSelectionModel().select(lineNumber);
        instructionsTable.scrollTo(lineNumber);
    }

    @FXML
    private void fastExecution() {
        while (computer.executeInstruction()) {}
        System.out.println(Arrays.toString(computer.RAM));
        memoryCells.clear();
        for (int i = 0; i < computer.RAM.length; i++) {
            memoryCells.add(new CellRow(i, computer.RAM[i]));
        }
        markLine(computer.PC);
        PC.setText(String.valueOf(computer.PC));
        ARegister.setText(String.valueOf(computer.A));
        DRegister.setText(String.valueOf(computer.D));
        //fast_execution.setDisable(true);
    }
}
