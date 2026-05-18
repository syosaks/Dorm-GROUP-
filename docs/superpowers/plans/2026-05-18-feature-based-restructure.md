# Feature-Based Package Restructure Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reorganize from layer-based packages (controller/, dao/, model/) to feature-based packages where each feature folder contains its controller, DAO, and model together.

**Architecture:** Each feature becomes its own Java package (`com.example.dorm.<feature>`) containing its Controller, DAO, and Model class. Shared base classes go in `com.example.dorm.shared`. Utilities stay in `com.example.dorm.util` unchanged except Session.java (User import update). FXML files mirror the same feature structure under `src/main/resources/com/example/dorm/`.

**Tech Stack:** Java 17, JavaFX 21.0.6, Maven, MySQL (XAMPP)

---

## Package Mapping Reference

| Old | New |
|-----|-----|
| `com.example.dorm.model.BaseEntity` | `com.example.dorm.shared.BaseEntity` |
| `com.example.dorm.model.Displayable` | `com.example.dorm.shared.Displayable` |
| `com.example.dorm.controller.LoginController` | `com.example.dorm.auth.LoginController` |
| `com.example.dorm.dao.UserDAO` | `com.example.dorm.auth.UserDAO` |
| `com.example.dorm.model.User` | `com.example.dorm.auth.User` |
| `com.example.dorm.controller.DashboardController` | `com.example.dorm.dashboard.DashboardController` |
| `com.example.dorm.controller.AddTenantController` | `com.example.dorm.tenant.AddTenantController` |
| `com.example.dorm.controller.TenantStatusController` | `com.example.dorm.tenant.TenantStatusController` |
| `com.example.dorm.controller.ViewRecordsController` | `com.example.dorm.tenant.ViewRecordsController` |
| `com.example.dorm.dao.TenantDAO` | `com.example.dorm.tenant.TenantDAO` |
| `com.example.dorm.model.Tenant` | `com.example.dorm.tenant.Tenant` |
| `com.example.dorm.controller.AssignRoomController` | `com.example.dorm.room.AssignRoomController` |
| `com.example.dorm.dao.RoomDAO` | `com.example.dorm.room.RoomDAO` |
| `com.example.dorm.model.Room` | `com.example.dorm.room.Room` |
| `com.example.dorm.controller.DormBuildingController` | `com.example.dorm.building.DormBuildingController` |
| `com.example.dorm.dao.DormBuildingDAO` | `com.example.dorm.building.DormBuildingDAO` |
| `com.example.dorm.model.DormBuilding` | `com.example.dorm.building.DormBuilding` |
| `com.example.dorm.controller.ReserveRoomController` | `com.example.dorm.reservation.ReserveRoomController` |
| `com.example.dorm.controller.ApproveReservationController` | `com.example.dorm.reservation.ApproveReservationController` |
| `com.example.dorm.dao.ReservationDAO` | `com.example.dorm.reservation.ReservationDAO` |
| `com.example.dorm.model.Reservation` | `com.example.dorm.reservation.Reservation` |
| `com.example.dorm.controller.PayRentController` | `com.example.dorm.payment.PayRentController` |
| `com.example.dorm.dao.PaymentDAO` | `com.example.dorm.payment.PaymentDAO` |
| `com.example.dorm.model.Payment` | `com.example.dorm.payment.Payment` |
| `com.example.dorm.controller.MaintenanceController` | `com.example.dorm.maintenance.MaintenanceController` |
| `com.example.dorm.controller.MaintenanceRecordsController` | `com.example.dorm.maintenance.MaintenanceRecordsController` |
| `com.example.dorm.dao.MaintenanceDAO` | `com.example.dorm.maintenance.MaintenanceDAO` |
| `com.example.dorm.model.MaintenanceRequest` | `com.example.dorm.maintenance.MaintenanceRequest` |
| `com.example.dorm.controller.FurnitureController` | `com.example.dorm.furniture.FurnitureController` |
| `com.example.dorm.dao.FurnitureDAO` | `com.example.dorm.furniture.FurnitureDAO` |
| `com.example.dorm.model.Furniture` | `com.example.dorm.furniture.Furniture` |
| `com.example.dorm.controller.VisitorLogController` | `com.example.dorm.visitor.VisitorLogController` |
| `com.example.dorm.dao.VisitorLogDAO` | `com.example.dorm.visitor.VisitorLogDAO` |
| `com.example.dorm.model.VisitorLog` | `com.example.dorm.visitor.VisitorLog` |
| `com.example.dorm.controller.ViewReportsController` | `com.example.dorm.report.ViewReportsController` |
| `com.example.dorm.dao.ReportDAO` | `com.example.dorm.report.ReportDAO` |

## FXML Path Mapping Reference

All FXML files move from `src/main/resources/com/example/dorm/view/` to feature sub-folders under `src/main/resources/com/example/dorm/`:

| Old | New |
|-----|-----|
| `view/LoginView.fxml` | `auth/LoginView.fxml` |
| `view/DashboardView.fxml` | `dashboard/DashboardView.fxml` |
| `view/AddTenantView.fxml` | `tenant/AddTenantView.fxml` |
| `view/TenantStatusView.fxml` | `tenant/TenantStatusView.fxml` |
| `view/ViewRecordsView.fxml` | `tenant/ViewRecordsView.fxml` |
| `view/AssignRoomView.fxml` | `room/AssignRoomView.fxml` |
| `view/DormBuildingView.fxml` | `building/DormBuildingView.fxml` |
| `view/ReserveRoomView.fxml` | `reservation/ReserveRoomView.fxml` |
| `view/ApproveReservationView.fxml` | `reservation/ApproveReservationView.fxml` |
| `view/PayRentView.fxml` | `payment/PayRentView.fxml` |
| `view/MaintenanceView.fxml` | `maintenance/MaintenanceView.fxml` |
| `view/MaintenanceRecordsView.fxml` | `maintenance/MaintenanceRecordsView.fxml` |
| `view/FurnitureView.fxml` | `furniture/FurnitureView.fxml` |
| `view/VisitorLogView.fxml` | `visitor/VisitorLogView.fxml` |
| `view/ViewReportsView.fxml` | `report/ViewReportsView.fxml` |

---

### Task 1: Create `shared` package (BaseEntity + Displayable)

**Files:**
- Create: `src/main/java/com/example/dorm/shared/Displayable.java`
- Create: `src/main/java/com/example/dorm/shared/BaseEntity.java`
- Delete: `src/main/java/com/example/dorm/model/Displayable.java`
- Delete: `src/main/java/com/example/dorm/model/BaseEntity.java`

- [ ] **Step 1: Create Displayable.java in shared**

```java
package com.example.dorm.shared;

public interface Displayable {
    String getDisplayName();
}
```

- [ ] **Step 2: Create BaseEntity.java in shared**

```java
package com.example.dorm.shared;

public abstract class BaseEntity implements Displayable {
    protected int id;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public abstract String getDisplayName();
}
```

- [ ] **Step 3: Delete old model/Displayable.java and model/BaseEntity.java**

```bash
rm "src/main/java/com/example/dorm/model/Displayable.java"
rm "src/main/java/com/example/dorm/model/BaseEntity.java"
```

---

### Task 2: Create `auth` package (User + UserDAO + LoginController)

**Files:**
- Create: `src/main/java/com/example/dorm/auth/User.java`
- Create: `src/main/java/com/example/dorm/auth/UserDAO.java`
- Create: `src/main/java/com/example/dorm/auth/LoginController.java`
- Delete: `src/main/java/com/example/dorm/model/User.java`
- Delete: `src/main/java/com/example/dorm/dao/UserDAO.java`
- Delete: `src/main/java/com/example/dorm/controller/LoginController.java`

- [ ] **Step 1: Create User.java in auth**

```java
package com.example.dorm.auth;

import com.example.dorm.shared.BaseEntity;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(int id, String username, String password, String role) {
        super(id);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getDisplayName() { return username + " (" + role + ")"; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```

- [ ] **Step 2: Create UserDAO.java in auth**

Copy `src/main/java/com/example/dorm/dao/UserDAO.java` to `src/main/java/com/example/dorm/auth/UserDAO.java`, changing only the top two lines:

```java
package com.example.dorm.auth;

import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.PasswordUtil;
// (keep all other imports and the rest of the file body identical)
```

Note: The `User` class is now in the same `auth` package — remove the old `import com.example.dorm.model.User;` line.

- [ ] **Step 3: Create LoginController.java in auth**

Copy `src/main/java/com/example/dorm/controller/LoginController.java` to `src/main/java/com/example/dorm/auth/LoginController.java`, replacing the top:

```java
package com.example.dorm.auth;

import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
```

`UserDAO` and `User` are in the same package — no imports needed for them.

Update the switchTo call:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/User.java"
rm "src/main/java/com/example/dorm/dao/UserDAO.java"
rm "src/main/java/com/example/dorm/controller/LoginController.java"
```

---

### Task 3: Create `dashboard` package (DashboardController)

**Files:**
- Create: `src/main/java/com/example/dorm/dashboard/DashboardController.java`
- Delete: `src/main/java/com/example/dorm/controller/DashboardController.java`

- [ ] **Step 1: Create DashboardController.java in dashboard**

Copy `controller/DashboardController.java`, changing the top and all switchTo calls:

```java
package com.example.dorm.dashboard;

import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
```

Update all `SceneManager.switchTo()` calls:
```java
@FXML private void onMyStatus()           { SceneManager.switchTo("tenant/TenantStatusView.fxml"); }
@FXML private void onReserveRoom()        { SceneManager.switchTo("reservation/ReserveRoomView.fxml"); }
@FXML private void onPayRent()            { SceneManager.switchTo("payment/PayRentView.fxml"); }
@FXML private void onAssignRoom()         { SceneManager.switchTo("room/AssignRoomView.fxml"); }
@FXML private void onApproveReservation() { SceneManager.switchTo("reservation/ApproveReservationView.fxml"); }
@FXML private void onViewRecords()        { SceneManager.switchTo("tenant/ViewRecordsView.fxml"); }
@FXML private void onViewReports()        { SceneManager.switchTo("report/ViewReportsView.fxml"); }
@FXML private void onMaintenance()        { SceneManager.switchTo("maintenance/MaintenanceView.fxml"); }
@FXML private void onAddTenant()          { SceneManager.switchTo("tenant/AddTenantView.fxml"); }
@FXML private void onDormBuildings()      { SceneManager.switchTo("building/DormBuildingView.fxml"); }
@FXML private void onVisitorLog()         { SceneManager.switchTo("visitor/VisitorLogView.fxml"); }
@FXML private void onFurniture()          { SceneManager.switchTo("furniture/FurnitureView.fxml"); }
@FXML private void onMaintenanceRecords() { SceneManager.switchTo("maintenance/MaintenanceRecordsView.fxml"); }
// logout:
SceneManager.switchTo("auth/LoginView.fxml");
```

- [ ] **Step 2: Delete old file**

```bash
rm "src/main/java/com/example/dorm/controller/DashboardController.java"
```

---

### Task 4: Create `tenant` package (Tenant + TenantDAO + 3 controllers)

**Files:**
- Create: `src/main/java/com/example/dorm/tenant/Tenant.java`
- Create: `src/main/java/com/example/dorm/tenant/TenantDAO.java`
- Create: `src/main/java/com/example/dorm/tenant/AddTenantController.java`
- Create: `src/main/java/com/example/dorm/tenant/TenantStatusController.java`
- Create: `src/main/java/com/example/dorm/tenant/ViewRecordsController.java`
- Delete: `src/main/java/com/example/dorm/model/Tenant.java`
- Delete: `src/main/java/com/example/dorm/dao/TenantDAO.java`
- Delete: `src/main/java/com/example/dorm/controller/AddTenantController.java`
- Delete: `src/main/java/com/example/dorm/controller/TenantStatusController.java`
- Delete: `src/main/java/com/example/dorm/controller/ViewRecordsController.java`

- [ ] **Step 1: Create Tenant.java in tenant**

```java
package com.example.dorm.tenant;

import com.example.dorm.shared.BaseEntity;

public class Tenant extends BaseEntity {
    private String name;
    private String contactNumber;
    private String email;
    private int roomId;
    private int userId;

    public Tenant() {}

    public Tenant(int id, String name, String contactNumber, String email, int roomId, int userId) {
        super(id);
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.roomId = roomId;
        this.userId = userId;
    }

    @Override
    public String getDisplayName() { return name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
```

- [ ] **Step 2: Create TenantDAO.java in tenant**

Copy `dao/TenantDAO.java`, change the top — `Tenant` is now in the same package:

```java
package com.example.dorm.tenant;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.Tenant;
// (keep all other imports and body identical)
```

- [ ] **Step 3: Create AddTenantController.java in tenant**

Copy `controller/AddTenantController.java`, change the top:

```java
package com.example.dorm.tenant;

import com.example.dorm.auth.UserDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
// Remove: import com.example.dorm.dao.TenantDAO; (same package)
// Remove: import com.example.dorm.dao.UserDAO; (moved to auth)
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Create TenantStatusController.java in tenant**

Copy `controller/TenantStatusController.java`, change the top:

```java
package com.example.dorm.tenant;

import com.example.dorm.auth.User;
import com.example.dorm.reservation.ReservationDAO;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
// TenantDAO and Tenant are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 5: Create ViewRecordsController.java in tenant**

Copy `controller/ViewRecordsController.java`, change the top:

```java
package com.example.dorm.tenant;

import com.example.dorm.payment.Payment;
import com.example.dorm.payment.PaymentDAO;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
// TenantDAO and Tenant are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 6: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/Tenant.java"
rm "src/main/java/com/example/dorm/dao/TenantDAO.java"
rm "src/main/java/com/example/dorm/controller/AddTenantController.java"
rm "src/main/java/com/example/dorm/controller/TenantStatusController.java"
rm "src/main/java/com/example/dorm/controller/ViewRecordsController.java"
```

---

### Task 5: Create `room` package (Room + RoomDAO + AssignRoomController)

**Files:**
- Create: `src/main/java/com/example/dorm/room/Room.java`
- Create: `src/main/java/com/example/dorm/room/RoomDAO.java`
- Create: `src/main/java/com/example/dorm/room/AssignRoomController.java`
- Delete: old model/Room.java, dao/RoomDAO.java, controller/AssignRoomController.java

- [ ] **Step 1: Create Room.java in room**

```java
package com.example.dorm.room;

import com.example.dorm.shared.BaseEntity;

public class Room extends BaseEntity {
    private String roomNumber;
    private int capacity;
    private String status;
    private double monthlyRate;
    private int floor;
    private int buildingId;

    public Room() {}

    public Room(int id, String roomNumber, int capacity, String status, double monthlyRate,
                int floor, int buildingId) {
        super(id);
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.status = status;
        this.monthlyRate = monthlyRate;
        this.floor = floor;
        this.buildingId = buildingId;
    }

    public Room(int id, String roomNumber, int capacity, String status, double monthlyRate) {
        this(id, roomNumber, capacity, status, monthlyRate, 1, 1);
    }

    @Override
    public String getDisplayName() { return "Room " + roomNumber; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(double monthlyRate) { this.monthlyRate = monthlyRate; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public int getBuildingId() { return buildingId; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }
}
```

- [ ] **Step 2: Create RoomDAO.java in room**

Copy `dao/RoomDAO.java`, change the top — `Room` is now in the same package:

```java
package com.example.dorm.room;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.Room;
```

- [ ] **Step 3: Create AssignRoomController.java in room**

Copy `controller/AssignRoomController.java`, change the top:

```java
package com.example.dorm.room;

import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
// Room and RoomDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/Room.java"
rm "src/main/java/com/example/dorm/dao/RoomDAO.java"
rm "src/main/java/com/example/dorm/controller/AssignRoomController.java"
```

---

### Task 6: Create `building` package (DormBuilding + DormBuildingDAO + DormBuildingController)

**Files:**
- Create: `src/main/java/com/example/dorm/building/DormBuilding.java`
- Create: `src/main/java/com/example/dorm/building/DormBuildingDAO.java`
- Create: `src/main/java/com/example/dorm/building/DormBuildingController.java`
- Delete: old model/DormBuilding.java, dao/DormBuildingDAO.java, controller/DormBuildingController.java

- [ ] **Step 1: Create DormBuilding.java in building**

```java
package com.example.dorm.building;

import com.example.dorm.shared.BaseEntity;

public class DormBuilding extends BaseEntity {
    private String buildingName;
    private int totalFloors;
    private String address;

    public DormBuilding() {}

    public DormBuilding(int id, String buildingName, int totalFloors, String address) {
        super(id);
        this.buildingName = buildingName;
        this.totalFloors = totalFloors;
        this.address = address;
    }

    @Override
    public String getDisplayName() { return buildingName; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() { return buildingName; }
}
```

- [ ] **Step 2: Create DormBuildingDAO.java in building**

Copy `dao/DormBuildingDAO.java`, change the top — `DormBuilding` is now in the same package:

```java
package com.example.dorm.building;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.DormBuilding;
```

- [ ] **Step 3: Create DormBuildingController.java in building**

Copy `controller/DormBuildingController.java`, change the top:

```java
package com.example.dorm.building;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
// DormBuilding and DormBuildingDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/DormBuilding.java"
rm "src/main/java/com/example/dorm/dao/DormBuildingDAO.java"
rm "src/main/java/com/example/dorm/controller/DormBuildingController.java"
```

---

### Task 7: Create `reservation` package (Reservation + ReservationDAO + 2 controllers)

**Files:**
- Create: `src/main/java/com/example/dorm/reservation/Reservation.java`
- Create: `src/main/java/com/example/dorm/reservation/ReservationDAO.java`
- Create: `src/main/java/com/example/dorm/reservation/ReserveRoomController.java`
- Create: `src/main/java/com/example/dorm/reservation/ApproveReservationController.java`
- Delete: old files for model/Reservation, dao/ReservationDAO, controller/ReserveRoomController, controller/ApproveReservationController

- [ ] **Step 1: Create Reservation.java in reservation**

```java
package com.example.dorm.reservation;

import com.example.dorm.shared.BaseEntity;

public class Reservation extends BaseEntity {
    private int tenantId;
    private int roomId;
    private String requestDate;
    private String status;

    public Reservation() {}

    public Reservation(int id, int tenantId, int roomId, String requestDate, String status) {
        super(id);
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.requestDate = requestDate;
        this.status = status;
    }

    @Override
    public String getDisplayName() { return "Reservation #" + id + " - " + status; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 2: Create ReservationDAO.java in reservation**

Copy `dao/ReservationDAO.java`, change the top — `Reservation` is now in the same package:

```java
package com.example.dorm.reservation;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.Reservation;
```

- [ ] **Step 3: Create ReserveRoomController.java in reservation**

Copy `controller/ReserveRoomController.java`, change the top:

```java
package com.example.dorm.reservation;

import com.example.dorm.auth.User;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
// Reservation and ReservationDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Create ApproveReservationController.java in reservation**

Copy `controller/ApproveReservationController.java`, change the top:

```java
package com.example.dorm.reservation;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
// ReservationDAO is in same package — no import needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 5: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/Reservation.java"
rm "src/main/java/com/example/dorm/dao/ReservationDAO.java"
rm "src/main/java/com/example/dorm/controller/ReserveRoomController.java"
rm "src/main/java/com/example/dorm/controller/ApproveReservationController.java"
```

---

### Task 8: Create `payment` package (Payment + PaymentDAO + PayRentController)

**Files:**
- Create: `src/main/java/com/example/dorm/payment/Payment.java`
- Create: `src/main/java/com/example/dorm/payment/PaymentDAO.java`
- Create: `src/main/java/com/example/dorm/payment/PayRentController.java`
- Delete: old model/Payment.java, dao/PaymentDAO.java, controller/PayRentController.java

- [ ] **Step 1: Create Payment.java in payment**

```java
package com.example.dorm.payment;

import com.example.dorm.shared.BaseEntity;

public class Payment extends BaseEntity {
    private int tenantId;
    private double amount;
    private String paymentDate;
    private String monthCovered;
    private String status;

    public Payment() {}

    public Payment(int id, int tenantId, double amount, String paymentDate,
                   String monthCovered, String status) {
        super(id);
        this.tenantId = tenantId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.monthCovered = monthCovered;
        this.status = status;
    }

    @Override
    public String getDisplayName() {
        return "Payment - " + monthCovered + " (" + String.format("%.2f", amount) + ")";
    }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public String getMonthCovered() { return monthCovered; }
    public void setMonthCovered(String monthCovered) { this.monthCovered = monthCovered; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 2: Create PaymentDAO.java in payment**

Copy `dao/PaymentDAO.java`, change the top — `Payment` is now in the same package:

```java
package com.example.dorm.payment;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.Payment;
```

- [ ] **Step 3: Create PayRentController.java in payment**

Copy `controller/PayRentController.java`, change the top:

```java
package com.example.dorm.payment;

import com.example.dorm.auth.User;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// Payment and PaymentDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/Payment.java"
rm "src/main/java/com/example/dorm/dao/PaymentDAO.java"
rm "src/main/java/com/example/dorm/controller/PayRentController.java"
```

---

### Task 9: Create `maintenance` package (MaintenanceRequest + MaintenanceDAO + 2 controllers)

**Files:**
- Create: `src/main/java/com/example/dorm/maintenance/MaintenanceRequest.java`
- Create: `src/main/java/com/example/dorm/maintenance/MaintenanceDAO.java`
- Create: `src/main/java/com/example/dorm/maintenance/MaintenanceController.java`
- Create: `src/main/java/com/example/dorm/maintenance/MaintenanceRecordsController.java`
- Delete: old model/MaintenanceRequest.java, dao/MaintenanceDAO.java, controller/MaintenanceController.java, controller/MaintenanceRecordsController.java

- [ ] **Step 1: Create MaintenanceRequest.java in maintenance**

```java
package com.example.dorm.maintenance;

import com.example.dorm.shared.BaseEntity;

public class MaintenanceRequest extends BaseEntity {
    private int tenantId;
    private String description;
    private String requestDate;
    private String status;   // PENDING, IN_PROGRESS, RESOLVED
    private String priority; // LOW, MEDIUM, HIGH

    public MaintenanceRequest() {}

    public MaintenanceRequest(int id, int tenantId, String description,
                              String requestDate, String status, String priority) {
        super(id);
        this.tenantId = tenantId;
        this.description = description;
        this.requestDate = requestDate;
        this.status = status;
        this.priority = priority;
    }

    @Override
    public String getDisplayName() { return "[" + priority + "] " + description; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
```

- [ ] **Step 2: Create MaintenanceDAO.java in maintenance**

Copy `dao/MaintenanceDAO.java`, change the top — `MaintenanceRequest` is now in the same package:

```java
package com.example.dorm.maintenance;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.MaintenanceRequest;
```

- [ ] **Step 3: Create MaintenanceController.java in maintenance**

Copy `controller/MaintenanceController.java`, change the top:

```java
package com.example.dorm.maintenance;

import com.example.dorm.auth.User;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// MaintenanceDAO and MaintenanceRequest are in same package — no imports needed
```

Update switchTo calls:
```java
SceneManager.switchTo("maintenance/MaintenanceRecordsView.fxml");
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Create MaintenanceRecordsController.java in maintenance**

Copy `controller/MaintenanceRecordsController.java`, change the top:

```java
package com.example.dorm.maintenance;

import com.example.dorm.auth.User;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
// MaintenanceDAO is in same package — no import needed
```

Update switchTo:
```java
SceneManager.switchTo("maintenance/MaintenanceView.fxml");
```

- [ ] **Step 5: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/MaintenanceRequest.java"
rm "src/main/java/com/example/dorm/dao/MaintenanceDAO.java"
rm "src/main/java/com/example/dorm/controller/MaintenanceController.java"
rm "src/main/java/com/example/dorm/controller/MaintenanceRecordsController.java"
```

---

### Task 10: Create `furniture` package (Furniture + FurnitureDAO + FurnitureController)

**Files:**
- Create: `src/main/java/com/example/dorm/furniture/Furniture.java`
- Create: `src/main/java/com/example/dorm/furniture/FurnitureDAO.java`
- Create: `src/main/java/com/example/dorm/furniture/FurnitureController.java`
- Delete: old model/Furniture.java, dao/FurnitureDAO.java, controller/FurnitureController.java

- [ ] **Step 1: Create Furniture.java in furniture**

```java
package com.example.dorm.furniture;

import com.example.dorm.shared.BaseEntity;

public class Furniture extends BaseEntity {
    private int roomId;
    private String itemType;
    private String condition;
    private String serialNumber;

    public Furniture() {}

    public Furniture(int id, int roomId, String itemType, String condition, String serialNumber) {
        super(id);
        this.roomId = roomId;
        this.itemType = itemType;
        this.condition = condition;
        this.serialNumber = serialNumber;
    }

    @Override
    public String getDisplayName() { return itemType + " (" + condition + ")"; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}
```

- [ ] **Step 2: Create FurnitureDAO.java in furniture**

Copy `dao/FurnitureDAO.java`, change the top — `Furniture` is now in the same package:

```java
package com.example.dorm.furniture;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.Furniture;
```

- [ ] **Step 3: Create FurnitureController.java in furniture**

Copy `controller/FurnitureController.java`, change the top:

```java
package com.example.dorm.furniture;

import com.example.dorm.maintenance.MaintenanceDAO;
import com.example.dorm.maintenance.MaintenanceRequest;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;
// Furniture and FurnitureDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/Furniture.java"
rm "src/main/java/com/example/dorm/dao/FurnitureDAO.java"
rm "src/main/java/com/example/dorm/controller/FurnitureController.java"
```

---

### Task 11: Create `visitor` package (VisitorLog + VisitorLogDAO + VisitorLogController)

**Files:**
- Create: `src/main/java/com/example/dorm/visitor/VisitorLog.java`
- Create: `src/main/java/com/example/dorm/visitor/VisitorLogDAO.java`
- Create: `src/main/java/com/example/dorm/visitor/VisitorLogController.java`
- Delete: old model/VisitorLog.java, dao/VisitorLogDAO.java, controller/VisitorLogController.java

- [ ] **Step 1: Create VisitorLog.java in visitor**

```java
package com.example.dorm.visitor;

import com.example.dorm.shared.BaseEntity;

public class VisitorLog extends BaseEntity {
    private String visitorName;
    private int tenantId;
    private String visitDate;
    private String timeIn;
    private String timeOut;
    private String purpose;

    public VisitorLog() {}

    public VisitorLog(int id, String visitorName, int tenantId, String visitDate,
                      String timeIn, String timeOut, String purpose) {
        super(id);
        this.visitorName = visitorName;
        this.tenantId = tenantId;
        this.visitDate = visitDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.purpose = purpose;
    }

    @Override
    public String getDisplayName() { return visitorName + " - " + visitDate; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
```

- [ ] **Step 2: Create VisitorLogDAO.java in visitor**

Copy `dao/VisitorLogDAO.java`, change the top — `VisitorLog` is now in the same package:

```java
package com.example.dorm.visitor;

import com.example.dorm.util.DatabaseConnection;
import java.sql.*;
import java.util.*;
// Remove: import com.example.dorm.model.VisitorLog;
```

- [ ] **Step 3: Create VisitorLogController.java in visitor**

Copy `controller/VisitorLogController.java`, change the top:

```java
package com.example.dorm.visitor;

import com.example.dorm.auth.User;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
// VisitorLog and VisitorLogDAO are in same package — no imports needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 4: Delete old files**

```bash
rm "src/main/java/com/example/dorm/model/VisitorLog.java"
rm "src/main/java/com/example/dorm/dao/VisitorLogDAO.java"
rm "src/main/java/com/example/dorm/controller/VisitorLogController.java"
```

---

### Task 12: Create `report` package (ReportDAO + ViewReportsController)

**Files:**
- Create: `src/main/java/com/example/dorm/report/ReportDAO.java`
- Create: `src/main/java/com/example/dorm/report/ViewReportsController.java`
- Delete: old dao/ReportDAO.java, controller/ViewReportsController.java

- [ ] **Step 1: Create ReportDAO.java in report**

Copy `dao/ReportDAO.java`, change only the package line:

```java
package com.example.dorm.report;

import com.example.dorm.util.DatabaseConnection;
// (keep all other imports and body identical)
```

- [ ] **Step 2: Create ViewReportsController.java in report**

Copy `controller/ViewReportsController.java`, change the top:

```java
package com.example.dorm.report;

import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
// ReportDAO is in same package — no import needed
```

Update switchTo:
```java
SceneManager.switchTo("dashboard/DashboardView.fxml");
```

- [ ] **Step 3: Delete old files**

```bash
rm "src/main/java/com/example/dorm/dao/ReportDAO.java"
rm "src/main/java/com/example/dorm/controller/ViewReportsController.java"
```

---

### Task 13: Update `util` package (Session.java only)

**Files:**
- Modify: `src/main/java/com/example/dorm/util/Session.java`

- [ ] **Step 1: Update Session.java to import User from auth**

Change:
```java
import com.example.dorm.model.User;
```
To:
```java
import com.example.dorm.auth.User;
```

---

### Task 14: Update SceneManager.java and HelloApplication.java

**Files:**
- Modify: `src/main/java/com/example/dorm/util/SceneManager.java`
- Modify: `src/main/java/com/example/dorm/HelloApplication.java`

- [ ] **Step 1: Update SceneManager.java — change resource base path**

Change:
```java
SceneManager.class.getResource("/com/example/dorm/view/" + fxmlFile)
```
To:
```java
SceneManager.class.getResource("/com/example/dorm/" + fxmlFile)
```

The full updated method:
```java
public static void switchTo(String fxmlPath) {
    try {
        FXMLLoader loader = new FXMLLoader(
            SceneManager.class.getResource("/com/example/dorm/" + fxmlPath)
        );
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 900, 600));
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

- [ ] **Step 2: Update HelloApplication.java**

Change:
```java
SceneManager.switchTo("LoginView.fxml");
```
To:
```java
SceneManager.switchTo("auth/LoginView.fxml");
```

---

### Task 15: Move FXML files to feature folders

**Files:** Move all 15 FXML files from `src/main/resources/com/example/dorm/view/` to new feature sub-folders.

- [ ] **Step 1: Create feature folders and move FXMLs**

```bash
RESOURCES=src/main/resources/com/example/dorm
mkdir -p $RESOURCES/auth $RESOURCES/dashboard $RESOURCES/tenant $RESOURCES/room
mkdir -p $RESOURCES/building $RESOURCES/reservation $RESOURCES/payment
mkdir -p $RESOURCES/maintenance $RESOURCES/furniture $RESOURCES/visitor $RESOURCES/report

mv $RESOURCES/view/LoginView.fxml              $RESOURCES/auth/
mv $RESOURCES/view/DashboardView.fxml          $RESOURCES/dashboard/
mv $RESOURCES/view/AddTenantView.fxml          $RESOURCES/tenant/
mv $RESOURCES/view/TenantStatusView.fxml       $RESOURCES/tenant/
mv $RESOURCES/view/ViewRecordsView.fxml        $RESOURCES/tenant/
mv $RESOURCES/view/AssignRoomView.fxml         $RESOURCES/room/
mv $RESOURCES/view/DormBuildingView.fxml       $RESOURCES/building/
mv $RESOURCES/view/ReserveRoomView.fxml        $RESOURCES/reservation/
mv $RESOURCES/view/ApproveReservationView.fxml $RESOURCES/reservation/
mv $RESOURCES/view/PayRentView.fxml            $RESOURCES/payment/
mv $RESOURCES/view/MaintenanceView.fxml        $RESOURCES/maintenance/
mv $RESOURCES/view/MaintenanceRecordsView.fxml $RESOURCES/maintenance/
mv $RESOURCES/view/FurnitureView.fxml          $RESOURCES/furniture/
mv $RESOURCES/view/VisitorLogView.fxml         $RESOURCES/visitor/
mv $RESOURCES/view/ViewReportsView.fxml        $RESOURCES/report/
rmdir $RESOURCES/view
```

- [ ] **Step 2: Update fx:controller in each FXML file**

For each file, replace the old controller package with the new one:

| File | Old fx:controller | New fx:controller |
|------|------------------|-------------------|
| `auth/LoginView.fxml` | `com.example.dorm.controller.LoginController` | `com.example.dorm.auth.LoginController` |
| `dashboard/DashboardView.fxml` | `com.example.dorm.controller.DashboardController` | `com.example.dorm.dashboard.DashboardController` |
| `tenant/AddTenantView.fxml` | `com.example.dorm.controller.AddTenantController` | `com.example.dorm.tenant.AddTenantController` |
| `tenant/TenantStatusView.fxml` | `com.example.dorm.controller.TenantStatusController` | `com.example.dorm.tenant.TenantStatusController` |
| `tenant/ViewRecordsView.fxml` | `com.example.dorm.controller.ViewRecordsController` | `com.example.dorm.tenant.ViewRecordsController` |
| `room/AssignRoomView.fxml` | `com.example.dorm.controller.AssignRoomController` | `com.example.dorm.room.AssignRoomController` |
| `building/DormBuildingView.fxml` | `com.example.dorm.controller.DormBuildingController` | `com.example.dorm.building.DormBuildingController` |
| `reservation/ReserveRoomView.fxml` | `com.example.dorm.controller.ReserveRoomController` | `com.example.dorm.reservation.ReserveRoomController` |
| `reservation/ApproveReservationView.fxml` | `com.example.dorm.controller.ApproveReservationController` | `com.example.dorm.reservation.ApproveReservationController` |
| `payment/PayRentView.fxml` | `com.example.dorm.controller.PayRentController` | `com.example.dorm.payment.PayRentController` |
| `maintenance/MaintenanceView.fxml` | `com.example.dorm.controller.MaintenanceController` | `com.example.dorm.maintenance.MaintenanceController` |
| `maintenance/MaintenanceRecordsView.fxml` | `com.example.dorm.controller.MaintenanceRecordsController` | `com.example.dorm.maintenance.MaintenanceRecordsController` |
| `furniture/FurnitureView.fxml` | `com.example.dorm.controller.FurnitureController` | `com.example.dorm.furniture.FurnitureController` |
| `visitor/VisitorLogView.fxml` | `com.example.dorm.controller.VisitorLogController` | `com.example.dorm.visitor.VisitorLogController` |
| `report/ViewReportsView.fxml` | `com.example.dorm.controller.ViewReportsController` | `com.example.dorm.report.ViewReportsController` |

---

### Task 16: Update module-info.java

**Files:**
- Modify: `src/main/java/module-info.java`

- [ ] **Step 1: Replace module-info.java with new package opens/exports**

```java
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
```

---

### Task 17: Delete now-empty old packages

**Files:**
- Delete directories: `controller/`, `dao/`, `model/` (should be empty after all tasks above)

- [ ] **Step 1: Verify directories are empty and delete**

```bash
# Verify empty first
ls src/main/java/com/example/dorm/controller/
ls src/main/java/com/example/dorm/dao/
ls src/main/java/com/example/dorm/model/

# Delete if empty
rmdir src/main/java/com/example/dorm/controller/
rmdir src/main/java/com/example/dorm/dao/
rmdir src/main/java/com/example/dorm/model/
```

---

### Task 18: Verify the build compiles

- [ ] **Step 1: Run Maven compile**

```bash
cd C:/Users/SYOSAK/IdeaProjects/Dorm-Group
./mvnw clean compile
```

Expected output: `BUILD SUCCESS`

If there are compilation errors, check the error message for which file has missing imports and fix the specific import.

- [ ] **Step 2: Commit**

```bash
git add -A
git commit -m "refactor: reorganize into feature-based package structure

Move from layer-based (controller/dao/model) to feature-based packages.
Each feature (auth, tenant, room, building, reservation, payment,
maintenance, furniture, visitor, report, dashboard) now contains its
own controller, DAO, and model. Shared base classes in shared/.
FXML files mirror the same feature folder structure."
```
