module com.example.dorm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.dorm to javafx.fxml;
    opens com.example.dorm.auth to javafx.fxml;
    opens com.example.dorm.dashboard to javafx.fxml;
    opens com.example.dorm.tenant to javafx.fxml;
    opens com.example.dorm.room to javafx.fxml;
    opens com.example.dorm.building to javafx.fxml;
    opens com.example.dorm.reservation to javafx.fxml;
    opens com.example.dorm.payment to javafx.fxml;
    opens com.example.dorm.maintenance to javafx.fxml;
    opens com.example.dorm.furniture to javafx.fxml;
    opens com.example.dorm.visitor to javafx.fxml;
    opens com.example.dorm.report to javafx.fxml;
    opens com.example.dorm.shared to javafx.fxml;
    exports com.example.dorm;
    exports com.example.dorm.util;
}
