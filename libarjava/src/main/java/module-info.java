module com.example.libarjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.libarjava to javafx.fxml;
    exports com.example.libarjava;
}