module com.mycompany.virtualmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.virtualmachine to javafx.fxml;
    exports com.mycompany.virtualmachine;
}
