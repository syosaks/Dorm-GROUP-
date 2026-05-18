# DormLink Presentation Guide — Dormitory Management System

## Login Credentials

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full system access |
| landlord | land123 | LANDLORD | Read-only: records, reports, maintenance, visitors |
| tenant1 | ten123 | TENANT | Juan dela Cruz (Room 102) |
| tenant2 | ten123 | TENANT | Maria Santos (Room 103) |
| tenant3 | ten123 | TENANT | Pedro Reyes (unassigned) |

**To reset database:** Delete `dormitory.db` from project root. It recreates with fresh seed data on next run.

---

## Professor Scoring Criteria & How We Address It

| Criteria | Implementation | Score Impact |
|----------|----------------|--------------|
| **OOP** | 9 model classes (User, Room, DormBuilding, Tenant, Payment, Reservation, MaintenanceRequest, VisitorLog, Furniture), encapsulation, associations via foreign keys | A+ |
| **GUI** | JavaFX, 14 FXML views, TableView, ComboBox, TextArea, GridPane, TitledPane, ScrollPane, role-based visibility | A+ |
| **UML** | Use Case Diagram + Class Diagram in `/resources/diagrams/` | A |
| **Design Patterns** | Singleton (DatabaseConnection), MVC (FXML+Controller+Model), DAO pattern (8 DAOs) | A+ |
| **Database** | SQLite JDBC, 9 tables, real CRUD, JOIN queries, schema migrations, aggregate reports | A+ |
| **Business Logic** | Role-based access, duplicate guards, room capacity, Furniture BROKEN → auto HIGH priority ticket, visitor time validation | A+ |

---

## 5-Person Presentation Division

---

### Person 1 — Cassius L. Cenas
**Topic: Project Overview, Architecture & Authentication**

**Files:**
- `HelloApplication.java` + `Launcher.java` — "DormLink" app entry
- `util/DatabaseConnection.java` — **Singleton** pattern + 9-table schema + seeding
- `util/SceneManager.java` — MVC navigation
- `util/Session.java` — session state
- `controller/LoginController.java` + `view/LoginView.fxml`

**Demo script:**
1. Show UML diagrams — "9 model entities, 3 user roles, 14 views"
2. Run app → DormLink login screen with blue branding
3. Demo wrong password → error message appears
4. Login as `admin / admin123 / ADMIN` → Dashboard shows all buttons + DormLink section
5. Logout → Login as `tenant1 / ten123 / TENANT` → only TENANT buttons visible
6. "Role-based access enforced at controller level via `Session.getCurrentUser().getRole()`"

**Key points:**
- **Singleton**: one DB connection reused throughout the app
- **MVC**: FXML = View, Controller = Controller, model classes = Model
- **9 tables** auto-created and migrated on startup

---

### Person 2 — Caswell Phyl E. Sarita
**Topic: Room Management + Dorm Buildings**

**Files:**
- `model/Room.java` — now includes `floor` and `buildingId` fields
- `model/DormBuilding.java` — new entity
- `dao/RoomDAO.java` — getAllWithBuilding(), countActiveOccupants()
- `dao/DormBuildingDAO.java` — getAllForDisplay(), add()
- `controller/AssignRoomController.java` + `view/AssignRoomView.fxml`
- `controller/DormBuildingController.java` + `view/DormBuildingView.fxml`

**Demo script (login as admin):**
1. Click **Dorm Buildings** → See 2 buildings (Building A and B), rooms grouped
2. Expand "Add New Building" → fill in name, floors, address → click Add
3. Filter room table by "Building A — Main" → shows only those rooms
4. Navigate back → Click **Assign Room** → combo shows only *unassigned* tenants
5. Select Pedro Reyes + Room 101 → click Assign
6. Select the row → Vacate → confirmation → Pedro removed, Room 101 AVAILABLE

**Key points:**
- DormBuilding is a new entity from the proposal — rooms now linked to buildings
- `countActiveOccupants(roomId)` used for capacity checking business rule
- Room model extended with `floor` and `buildingId` (OOP association)

---

### Person 3 — Marielle Faith Basañez
**Topic: Resident Management, Records & Add Resident**

**Files:**
- `model/Tenant.java` — Resident Profile (name, contact, email, room, user)
- `dao/TenantDAO.java` — getTenantsWithDetails(), getAssignmentsDisplay()
- `controller/ViewRecordsController.java` + `view/ViewRecordsView.fxml`
- `controller/AddTenantController.java` + `view/AddTenantView.fxml`

**Demo script (login as admin):**
1. Click **View Records** → filter = Tenants → all residents shown
2. Search "Maria" → filters to Maria Santos only
3. Switch filter to **Rooms** → all rooms with building/floor info
4. Switch to **Payments** → all payment history
5. Click Back → **Add New Resident** → fill form (duplicate username check demo)
6. Add `newuser / pass1 / Ana Reyes / 09451234567` → success

**Key OOP points:**
- Tenant has associations to Room (via room_id) and User (via user_id)
- Duplicate username guard: `userDAO.usernameExists(username)` → business rule

---

### Person 4 — Nhiel David D. Miranda
**Topic: Reservation Workflow + Visitor Log**

**Files:**
- `model/Reservation.java` + `model/VisitorLog.java`
- `dao/ReservationDAO.java` — hasPendingReservation() guard
- `dao/VisitorLogDAO.java` — getAllForDisplay(), getByTenantId()
- `controller/ReserveRoomController.java` + `ApproveReservationController.java`
- `controller/VisitorLogController.java` + `view/VisitorLogView.fxml`

**Demo script — Reservation:**
1. Login as tenant3 → **Reserve Room** → select Room 104 → Reserve
2. Try again → "You already have a pending reservation" guard fires
3. Login as admin → **Approve Reservation** → select Pedro's reservation → Approve
4. Room 104 becomes OCCUPIED, Pedro assigned atomically

**Demo script — Visitor Log:**
1. Login as tenant1 → **Visitor Log** → see own visitor history
2. Fill form: Visitor = "Sample Person", Purpose = "Study Group", TimeIn = 14:00, TimeOut = 16:00
3. Submit → entry appears in table
4. Demo time validation: TimeOut = 13:00 → "Time Out must be after Time In" error
5. Login as admin → Visitor Log → sees ALL visitor logs across all residents

**Key points:**
- 3-step atomic approval: reservation + room + tenant all updated together
- Visitor time validation business rule enforced in controller

---

### Person 5 — Ramzhan S. Booc
**Topic: Payments, Reports, Maintenance & Furniture**

**Files:**
- `model/Payment.java` + `model/MaintenanceRequest.java` + `model/Furniture.java`
- `dao/PaymentDAO.java` + `dao/ReportDAO.java` + `dao/MaintenanceDAO.java` + `dao/FurnitureDAO.java`
- All corresponding controllers and FXML views

**Demo script — Pay Rent:**
1. Login as tenant1 → **Pay Rent** → locked to own room, rate pre-filled
2. Type "May 2026" → Record Payment → appears in history

**Demo script — Maintenance (Active vs Resolved):**
1. Login as tenant1 → **Maintenance Requests** → sees only PENDING/IN_PROGRESS
2. Select priority = HIGH → Submit "Water heater broken"
3. Login as admin → Maintenance → selects the HIGH priority ticket → Mark In Progress → Mark Resolved
4. Resolved ticket DISAPPEARS from active list
5. Click **Resolved Records** → resolved ticket NOW appears here

**Demo script — Furniture (Business Rule):**
1. Login as admin → **Furniture Management** → see room-linked inventory
2. Select a DAMAGED item → Mark BROKEN
3. **Automatic HIGH priority maintenance ticket created instantly**
4. Navigate to Maintenance → see the auto-generated "[AUTO]" ticket at top

**Demo script — Reports:**
1. Login as admin → **View Reports** → Revenue → monthly totals
2. Occupancy → room status breakdown
3. Reservations → reservation counts by status

**Key points:**
- TENANT pay rent is locked to own profile (security enforcement)
- `ReportDAO` = Single Responsibility Principle
- Maintenance has PENDING → IN_PROGRESS → RESOLVED lifecycle
- Furniture BROKEN auto-ticket = business rule from DormLink proposal
- Maintenance split into active view + archived records view

---

## Summary: What Was Built — DormLink

| Feature | Status |
|---------|--------|
| Login with role-based authentication | ✅ |
| Role-based dashboard (per-role button visibility) | ✅ |
| Room reservation (duplicate + assignment guards) | ✅ |
| Reservation approval (3-step atomic: status + room + tenant) | ✅ |
| Pay rent (TENANT locked to own; ADMIN can pay for any) | ✅ |
| Assign room (unassigned tenants only) | ✅ |
| Vacate room (with confirmation dialog) | ✅ |
| View Records (search/filter: Residents, Rooms, Payments) | ✅ |
| View Reports (Occupancy, Revenue, Reservations) | ✅ |
| Add New Resident (duplicate username guard) | ✅ |
| Maintenance Requests — Active (PENDING + IN_PROGRESS only) | ✅ |
| Maintenance Records — Archived (RESOLVED, separate view) | ✅ |
| Priority levels on maintenance tickets (LOW/MEDIUM/HIGH) | ✅ |
| Visitor Log (TENANT logs own; ADMIN sees all; time validation) | ✅ |
| Dorm Buildings (group rooms by building + floor) | ✅ |
| Furniture Management (GOOD/DAMAGED/BROKEN per room) | ✅ |
| Furniture BROKEN → auto HIGH priority maintenance ticket | ✅ |
| SQLite database with 9 tables and full seed data | ✅ |
| Schema migration (ALTER TABLE for existing databases) | ✅ |
