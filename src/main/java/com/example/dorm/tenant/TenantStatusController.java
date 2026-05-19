package com.example.dorm.tenant;

import com.example.dorm.reservation.ReservationDAO;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.auth.User;
import com.example.dorm.shared.BaseController;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TenantStatusController extends BaseController {
    @FXML private Label lblName;
    @FXML private Label lblContact;
    @FXML private Label lblEmail;
    @FXML private Label lblRoom;
    @FXML private Label lblRate;
    @FXML private Label lblReservation;

    private final TenantDAO tenantDAO         = new TenantDAO();
    private final RoomDAO roomDAO             = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        super.initialize();
        User user = Session.getCurrentUser();
        if (user == null) return;

        Tenant tenant = tenantDAO.getByUserId(user.getId());
        if (tenant == null) {
            lblName.setText("No tenant profile found. Contact admin.");
            return;
        }

        lblName.setText("Name: " + tenant.getName());
        lblContact.setText("Contact: " + (tenant.getContactNumber() != null
            && !tenant.getContactNumber().isEmpty() ? tenant.getContactNumber() : "—"));
        lblEmail.setText("Email: " + (tenant.getEmail() != null
            && !tenant.getEmail().isEmpty() ? tenant.getEmail() : "—"));

        if (tenant.getRoomId() > 0) {
            Room room = roomDAO.getRoomById(tenant.getRoomId());
            if (room != null) {
                lblRoom.setText("Room " + room.getRoomNumber() + "  |  Floor " + room.getFloor());
                lblRoom.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 14px;");
                lblRate.setText("Monthly Rate: \u20b1" + String.format("%.2f", room.getMonthlyRate()));
            }
        } else {
            lblRoom.setText("No room assigned yet.");
            lblRoom.setStyle("-fx-text-fill: #C62828; -fx-font-size: 14px;");
            lblRate.setText("Monthly Rate: —");
        }

        String[] resv = reservationDAO.getLatestReservationForTenant(tenant.getId());
        if (resv != null) {
            String status = resv[1];

            // Pick color based on reservation status
            String color;
            if (status.equals("PENDING")) {
                color = "#F57C00";
            } else if (status.equals("APPROVED")) {
                color = "#2E7D32";
            } else {
                color = "#555555";
            }

            lblReservation.setText("Room " + resv[0] + " - " + status + " (Requested: " + resv[2] + ")");
            lblReservation.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else {
            lblReservation.setText("No active reservation.");
            lblReservation.setStyle("-fx-text-fill: #555555;");
        }
    }

    @FXML
    private void onBack() {
        SceneManager.switchTo("dashboard/DashboardView.fxml");
    }
}
