module com.example.dorm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.dorm to javafx.fxml;
    opens com.example.dorm.controller to javafx.fxml;
    exports com.example.dorm;
}
