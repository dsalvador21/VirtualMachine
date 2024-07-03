package com.mycompany.virtualmachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
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

    private static final int ROM_SIZE = 32768;
    private static final int RAM_SIZE = 16384;

    private Computer computer;
    private ObservableList<InstructionRow> instructions = FXCollections.observableArrayList();
    private ObservableList<CellRow> memoryCells = FXCollections.observableArrayList();

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

    // Función de inicialización de la interfaz gráfica.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        computer = new Computer(ROM_SIZE, RAM_SIZE);

        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        instructionColumn.setCellValueFactory(new PropertyValueFactory<>("instruction"));

        // Llenar la tabla de la ROM con cadenas vacías.
        for (int i = 0; i < computer.ROM.length; i++) {
            instructions.add(new InstructionRow(i, ""));
        }

        instructionsTable.setItems(instructions);

        cellColumn.setCellValueFactory(new PropertyValueFactory<>("cell"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        // Tabla con celdas editables para la RAM.
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // Actualizar RAM del computador si se ingresa un valor a una celda.
        valueColumn.setOnEditCommit(event -> {
            CellRow cellRow = event.getRowValue();
            cellRow.setValue(event.getNewValue());
            computer.RAM[cellRow.getCell()] = event.getNewValue();
        });

        // Llenar la tabla de la RAM con 0's.
        for (int i = 0; i < computer.RAM.length; i++) {
            memoryCells.add(new CellRow(i, 0));
        }

        memoryTable.setItems(memoryCells);
        memoryTable.setEditable(true);

        // Los botones de ejecución no son utilizables hasta que se cargue un programa.
        execute_instruction.setDisable(true);
        fast_execution.setDisable(true);
    }

    @FXML
    private void loadFile() throws FileNotFoundException {
        // Cargar archivo al programa.
        FileChooser fileChooser = new FileChooser();
        Stage stage = App.getStage();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hack files", "*.hack"));
        File file = fileChooser.showOpenDialog(stage);

        // Si no se seleccionó un archivo, salir del procedimiento.
        if (file == null)
            return;


        // Al cargar un nuevo programa es necesario resetear la ROM.
        computer.resetROM();

        // Limpiar la tabla de la ROM.        
        for (int i = 0; i < computer.ROM.length; i++) {
            instructions.set(i, new InstructionRow(i, ""));
        }

        instructionsTable.setItems(instructions);

        Scanner scanner = new Scanner(file);
        int i = 0;

        // Cargar las nuevas instrucciones a la ROM y a la tabla de visualización.
        while (scanner.hasNextLine()) {
            String instruction = scanner.nextLine();
            computer.ROM[i] = instruction;
            instructions.set(i, new InstructionRow(i, computer.ROM[i]));
            i++;
        }

        // Reinicio de los registros y la visualización de las tablas.
        resetComputer();
    }

    @FXML
    private void executeInstruction() {
        computer.executeInstruction();
        updateROMVisualization(false);
        updateRAMVisualization(false);
        updateRegistersVisualization();
    }

    @FXML
    private void fastExecution() {
        while (computer.executeInstruction()) {}

        updateROMVisualization(false);
        updateRAMVisualization(false);
        updateRegistersVisualization();
        execute_instruction.setDisable(true);
        fast_execution.setDisable(true);
    }

    @FXML
    private void resetComputer() {
        computer.resetRegisters();
        updateROMVisualization(true);
        updateRegistersVisualization();
        execute_instruction.setDisable(false);
        fast_execution.setDisable(false);
    }

    @FXML
    private void resetRAM() {
        computer.resetRAM();
        updateRAMVisualization(true);
    }

    private void updateROMVisualization(boolean reset) {
        instructionsTable.getSelectionModel().clearSelection();

        if (reset) {
            instructionsTable.scrollTo(0);
            instructionsTable.getSelectionModel().select(0);
        } else {
            instructionsTable.scrollTo(computer.PC);
            instructionsTable.getSelectionModel().select(computer.PC);
        }
    }

    private void updateRAMVisualization(boolean reset) {
        memoryCells.clear();

        if (reset) {
            for (int i = 0; i < computer.RAM.length; i++) {
                memoryCells.add(i, new CellRow(i, 0));
            }
        } else {
            for (int i = 0; i < computer.RAM.length; i++) {
                memoryCells.add(new CellRow(i, computer.RAM[i]));
            }
        }
    }

    private void updateRegistersVisualization() {
        ARegister.setText(String.valueOf(computer.A));
        DRegister.setText(String.valueOf(computer.D));
        PC.setText(String.valueOf(computer.PC));
    }
}
