# DormLink - Dormitory Management System

## Group Members
- Cassius L. Cenas
- Caswell Phyl E. Sarita
- Marielle Faith Basañez
- Nhiel David D. Miranda
- Ramzhan S. Booc

## Project Description
A Java desktop application for managing dormitory operations including room reservations, rent payments, tenant records, room assignments, maintenance requests, visitor logging, furniture tracking, and reporting. The system supports three user roles — **Tenant**, **Admin**, and **Landlord** — each with specific access to different features.

## Technologies Used
- Java 17
- JavaFX 21 (with FXML)
- JDBC (MySQL via XAMPP)
- Maven

---

## Capstone Evaluation Criteria Implementation

### 1. Object-Oriented Programming Principles (15%)

**Encapsulation:**
All model classes (`User`, `Tenant`, `Room`, `Payment`, etc.) use `private` fields accessed only through public getters and setters. Example: `src/main/java/com/example/dorm/model/Tenant.java`

**Abstraction:**
`BaseEntity` (`src/main/java/com/example/dorm/model/BaseEntity.java`) is an abstract class that all entity models extend. It defines the abstract method `getDisplayName()` which forces each subclass to provide its own implementation.

**Inheritance:**
All 9 model classes inherit from `BaseEntity`:
- `User extends BaseEntity`
- `Tenant extends BaseEntity`
- `Room extends BaseEntity`
- `Reservation extends BaseEntity`
- `Payment extends BaseEntity`
- `MaintenanceRequest extends BaseEntity`
- `Furniture extends BaseEntity`
- `VisitorLog extends BaseEntity`
- `DormBuilding extends BaseEntity`

**Polymorphism:**
The `getDisplayName()` method is overridden in each subclass with a different meaningful return value. For example:
- `Tenant.getDisplayName()` → returns `"Juan dela Cruz"`
- `Room.getDisplayName()` → returns `"Room 101"`
- `Payment.getDisplayName()` → returns `"Payment - March 2026 (4500.00)"`
- `MaintenanceRequest.getDisplayName()` → returns `"[HIGH] Leaking faucet"`

**Interfaces:**
`Displayable` (`src/main/java/com/example/dorm/model/Displayable.java`) is an interface implemented by `BaseEntity`, enforcing the `getDisplayName()` contract for all entity classes.

---

### 2. Java Generics (10%)

Generic collections are used throughout the project to ensure type safety:

- `List<Tenant>`, `List<Room>`, `List<Payment>` — in all DAO classes (e.g., `TenantDAO.getAllTenants()`)
- `List<String[]>` — used for display data passed from DAOs to controllers
- `TableView<String[]>`, `TableColumn<String[], String>` — in all 13 controllers
- `TableView<Room>`, `TableView<Payment>` — in `ReserveRoomController`, `PayRentController`
- `ComboBox<String>`, `ComboBox<DormBuilding>` — in `AssignRoomController`, `DormBuildingController`

Example files: `src/main/java/com/example/dorm/dao/TenantDAO.java`, `src/main/java/com/example/dorm/controller/AddTenantController.java`

---

### 3. Multithreading and Concurrency (10%)

**JavaFX Task (Background Threads):**
Three controllers use `javafx.concurrent.Task` to run database queries on a background thread, preventing the UI from freezing during data loading:

- `AddTenantController.loadTenants()` — loads tenant list in background
- `ViewRecordsController.loadRecords()` — loads tenant/room/payment records in background
- `MaintenanceController.loadRequests()` — loads maintenance requests in background

Pattern used in each:
```java
Task<List<String[]>> loadTask = new Task<List<String[]>>() {
    @Override
    protected List<String[]> call() throws Exception {
        return tenantDAO.getTenantsWithDetails(); // runs on background thread
    }
};
loadTask.setOnSucceeded(event -> {
    tenantsTable.setItems(...); // runs back on UI thread safely
});
Thread thread = new Thread(loadTask);
thread.setDaemon(true); // stops automatically when app closes
thread.start();
```

**Synchronization:**
`DatabaseConnection.getInstance()` is marked `synchronized` to prevent race conditions when multiple threads attempt to initialize the connection simultaneously:
```java
public static synchronized DatabaseConnection getInstance() { ... }
```

File: `src/main/java/com/example/dorm/util/DatabaseConnection.java`

---

### 4. Graphical User Interface (15%)

**JavaFX with FXML:**
15 fully functional FXML screens built with JavaFX:
`LoginView`, `DashboardView`, `AddTenantView`, `TenantStatusView`, `ReserveRoomView`, `ApproveReservationView`, `AssignRoomView`, `PayRentView`, `ViewRecordsView`, `ViewReportsView`, `MaintenanceView`, `MaintenanceRecordsView`, `DormBuildingView`, `FurnitureView`, `VisitorLogView`

**Event-Driven Programming:**
All user interactions are handled through `@FXML`-annotated event handler methods (e.g., `onLoginButtonClick()`, `onReserveButtonClick()`, `onApproveButtonClick()`). ComboBox listeners use `setOnAction()`.

**User-Friendly Design:**
- Role-based button visibility: Dashboard shows only buttons relevant to the logged-in role (TENANT / ADMIN / LANDLORD)
- Color-coded status labels (green = success, red = error/unassigned, orange = pending)
- Confirmation dialogs before destructive actions (vacate, reject)

---

### 5. Database Connectivity (15%)

**MySQL via JDBC:**
Connected to a MySQL database (`dormlink`) using JDBC. Connection is managed through `DatabaseConnection` (Singleton pattern).

**CRUD Operations:**
All 9 database tables support full CRUD:
- **Create:** `TenantDAO.addTenant()`, `PaymentDAO.add()`, `ReservationDAO.add()`, `FurnitureDAO.add()`, `VisitorLogDAO.add()`, `MaintenanceDAO.add()`
- **Read:** `TenantDAO.getAllTenants()`, `RoomDAO.getAllRooms()`, `ReportDAO.getRevenueByMonth()`, etc.
- **Update:** `TenantDAO.updateRoomId()`, `RoomDAO.updateStatus()`, `MaintenanceDAO.updateStatus()`, `VisitorLogDAO.updateTimeOut()`
- **Delete/Vacate:** `TenantDAO.vacateRoom()` (sets room_id to NULL)

**Security Measures:**
- All SQL queries use `PreparedStatement` to prevent SQL injection
- Passwords are stored as SHA-256 hashes (never plaintext) via `PasswordUtil.hash()`
- Multi-step operations use transactions (`beginTransaction()`, `commit()`, `rollback()`) for data integrity

---

### 6. Unified Modeling Language (10%)

Diagrams are located in `docs/diagrams/`:
- `class-diagram.png` — shows all 9 model classes, their attributes, methods, and relationships (including `BaseEntity` hierarchy)
- `use-case-diagram.png` — shows all interactions for Tenant, Admin, and Landlord roles

---

### 7. Design Patterns (10%)

**Singleton Pattern:**
`DatabaseConnection` (`src/main/java/com/example/dorm/util/DatabaseConnection.java`) uses the Singleton pattern with a thread-safe `synchronized` `getInstance()` method. This ensures only one database connection is created for the entire application lifetime.

**Observer Pattern (Event Listeners):**
JavaFX event-driven programming is a direct implementation of the Observer pattern. Example: `ComboBox.setOnAction(e -> loadPaymentHistory())` in `PayRentController` — the controller observes ComboBox selection changes and reacts automatically.

**DAO Pattern (Data Access Object):**
All database interactions are isolated in dedicated DAO classes (`UserDAO`, `TenantDAO`, `RoomDAO`, `ReservationDAO`, `PaymentDAO`, `MaintenanceDAO`, `FurnitureDAO`, `VisitorLogDAO`, `ReportDAO`, `DormBuildingDAO`). Controllers never write SQL directly — they call DAO methods. This separates business logic from database access.

**MVC Pattern (Model-View-Controller):**
- **Model:** `com.example.dorm.model` — data classes extending `BaseEntity`
- **View:** FXML files in `src/main/resources/com/example/dorm/view/`
- **Controller:** `com.example.dorm.controller` — 13 controller classes handling user interaction

---

### 8. Code Quality and Documentation (15%)

**Clean and Modular Structure:**
```
com.example.dorm
├── model/       — 9 entity classes + BaseEntity + Displayable interface
├── dao/         — 10 DAO classes (one per entity)
├── controller/  — 13 controller classes (one per screen)
└── util/        — DatabaseConnection, Session, SceneManager, PasswordUtil
```

**Naming Conventions:**
- Classes: PascalCase (`TenantDAO`, `ReserveRoomController`)
- Methods: camelCase (`getAllTenants()`, `onLoginButtonClick()`)
- Constants: UPPER_SNAKE_CASE (`DB_URL`, `DB_USER`)
- FXML IDs: camelCase matching controller field names

**Documentation:**
- Javadoc comments on all model and utility classes
- Inline comments explaining OOP concepts, threading, and business logic
- This README documents implementation of all 8 criteria with file references

---

## Login Credentials (for demo)

| Role     | Username  | Password  |
|----------|-----------|-----------|
| Admin    | admin     | admin123  |
| Landlord | landlord  | land123   |
| Tenant   | tenant1   | ten123    |
| Tenant   | tenant2   | ten123    |
| Tenant   | tenant3   | ten123    |
