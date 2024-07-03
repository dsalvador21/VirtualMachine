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
        disableExecutionButtons(true);
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
        if (file == null) return;

        Scanner scanner = new Scanner(file);

        // Al cargar un nuevo programa es necesario limpiar la ROM.
        computer.resetROM();

        // Cargar las nuevas instrucciones a la ROM.
        for (int i = 0; scanner.hasNextLine(); i++) {
            String instruction = scanner.nextLine();
            computer.ROM[i] = instruction;
        }

        scanner.close();

        // Cargas las nuevas instrucciones a la tabla de visualización.
        for (int i = 0; i < computer.ROM.length; i++) {
            instructions.set(i, new InstructionRow(i, computer.ROM[i]));
        }

        // Reinicio de los registros y reinicio de la visualización correspondiente.
        resetComputer();
    }

    // Ejecución paso a paso del programa. Se ejecuta la instrucción y se visualizan
    // los resultados.
    @FXML
    private void executeInstruction() {
        computer.executeInstruction();
        updateRegisters();
        updateRAM();
        updateLine();
    }

    // Ejecución en un paso del programa.
    @FXML
    private void fastExecution() {
        while (computer.executeInstruction()) {}

        updateRegisters();
        updateRAM();
        updateLine();
        // Deshabilitar botones de ejecución.
        disableExecutionButtons(true);
    }

    // Resetear ejecución de un programa o cargar nuevo programa.
    @FXML
    private void resetComputer() {
        computer.resetRegisters();
        updateRegisters();
        updateLine();
        // Habilitar botones de ejecución.
        disableExecutionButtons(false);
    }

    // Resetear los valores de la RAM.
    @FXML
    private void resetRAM() {
        computer.resetRAM();
        updateRAM();
    }

    // Actualizar la línea seleccionada.
    private void updateLine() {
        instructionsTable.getSelectionModel().clearSelection();
        instructionsTable.scrollTo(computer.PC);
        instructionsTable.getSelectionModel().select(computer.PC);
    }

    // Actualizar la tabla de la RAM.
    private void updateRAM() {
        for (int i = 0; i < computer.RAM.length; i++) {
            memoryCells.set(i, new CellRow(i, computer.RAM[i]));
        }
    }

    // Actualizar los valores de los registros mostrados.
    private void updateRegisters() {
        ARegister.setText(String.valueOf(computer.A));
        DRegister.setText(String.valueOf(computer.D));
        PC.setText(String.valueOf(computer.PC));
    }

    // Deshabilitar botones de ejecución.
    private void disableExecutionButtons(boolean disable) {
        if (disable) {
            execute_instruction.setDisable(true);
            fast_execution.setDisable(true);
        } else {
            execute_instruction.setDisable(false);
            fast_execution.setDisable(false);
        }
    }

}
