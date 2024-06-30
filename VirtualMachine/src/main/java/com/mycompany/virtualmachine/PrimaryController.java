package com.mycompany.virtualmachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {

    Computer computer;

    @FXML
    private Button load_file;
    @FXML
    private TableView<String[]> tableView;
    @FXML
    private TableColumn<String[], Integer> numberColumn;
    @FXML
    private TableColumn<String[], String> instructionColumn;
    @FXML
    private Button execute_instruction;
    @FXML
    private Button fast_execution;
    @FXML
    private TextField A_Register;
    @FXML
    private TextField D_Register;
    @FXML
    private TextField PC;
    @FXML
    private ComboBox<Integer> cell_selection;
    @FXML
    private TextField cell_value;
    @FXML
    private Button save_value;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int ROM_SIZE = 32768;
        int RAM_SIZE = 16384;

        computer = new Computer(ROM_SIZE, RAM_SIZE);
        
        computer.updateCell(0, 4);
        computer.updateCell(1, 3);
        
        for (int i = 0; i < RAM_SIZE; i++) {
            cell_selection.getItems().add(i);
        }
    }

    @FXML
    private void loadFile(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = App.getStage();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hack files", "*.hack")
        );
        File file = fileChooser.showOpenDialog(stage);

        Scanner scanner = new Scanner(file);
        int lineNumber = 0;

        tableView.getItems().clear();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            computer.loadROM(lineNumber, line);

            String[] rowData = new String[2];
            rowData[0] = String.valueOf(lineNumber);
            rowData[1] = line;
            tableView.getItems().add(rowData);

            lineNumber++;
        }

        numberColumn.setCellValueFactory(cellData -> {
            Integer value = Integer.valueOf(cellData.getValue()[0]);
            return new SimpleIntegerProperty(value).asObject();
        });

        instructionColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue()[1];
            return new SimpleStringProperty(value);
        });
    }

    @FXML
    private void executeInstruction(ActionEvent event) {
        marcarLinea(computer.PC);
        PC.setText(computer.PC + "");
        A_Register.setText(computer.A + "");
        D_Register.setText(computer.D + "");
        computer.executeInstruction();
    }

    private void marcarLinea(int lineNumber) {
        tableView.getSelectionModel().clearSelection();

        if (lineNumber >= 0 && lineNumber < tableView.getItems().size()) {
            tableView.getSelectionModel().select(lineNumber);
            tableView.scrollTo(lineNumber);
        } else {
            // Marcar programa como terminado.
        }
    }

    @FXML
    private void fastExecution(ActionEvent event) {
        while(computer.executeInstruction()){}
        System.out.println(Arrays.toString(computer.RAM));
    }

    @FXML
    private void cellSelection(ActionEvent event) {
        cell_value.setText(computer.RAM[cell_selection.getValue()] + "");
        // Tiene que ser observable.
    }
    
}
