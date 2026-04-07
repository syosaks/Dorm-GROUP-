module com.example.dorm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dorm to javafx.fxml;
    exports com.example.dorm;
}