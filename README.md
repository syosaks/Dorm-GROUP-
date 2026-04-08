# Dormitory Management System

## Group Members
- Cassius L. Cenas
- Caswell Phyl E. Sarita
- Marielle Faith Basañez
- Nhiel David D. Miranda
- Ramzhan S. Booc

## Project Description
A Java desktop application for managing dormitory operations including room reservations, rent payments, tenant records, room assignments, and reporting. The system supports three user roles: Tenant, Admin, and Landlord, each with specific access to different features.

## Proposed Features
- User login with role-based access (Tenant, Admin, Landlord)
- Room reservation and approval workflow
- Rent payment tracking
- Tenant and room record management
- Room assignment by Admin
- Report generation for Landlord

## Planned Technologies
- Java
- JavaFX (with FXML)
- JDBC
- SQLite

## Evaluation Criteria Mapping (Initial)
- **OOP:** Multiple interacting classes — User, Room, Tenant, Payment, Reservation with associations and encapsulation
- **GUI:** JavaFX with 8 FXML views, layout containers (VBox, HBox, BorderPane), and controls (TableView, ComboBox, TextField, Button)
- **UML:** Use Case Diagram and Class Diagram included in `/diagrams`
- **Design Pattern:** Singleton (DatabaseConnection), MVC (FXML Views + Controllers + Model classes)
