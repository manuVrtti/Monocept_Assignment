# Insurance Policy & Claim Management System

A production-grade, premium dark-themed SaaS web application for managing insurance products, policy plans, customer policies, premium payments, and claims workflows.

---

## 🌟 Key Features

### 👤 Role-based Portals (Admin, Agent, Customer)
- **Admin**: Create products and plans, manage system users, register agents, toggle user activation status, decide claims (Approve/Reject).
- **Agent**: List customers, view customer profiles, issue policies, cancel policies, review claims (Recommend Approval/Rejection).
- **Customer**: Build and update customer profile, purchase policy plans, make premium payments to activate policies, submit claims with supporting documents, view claim status timeline.

### 🛡️ Core Systems
1. **Product & Plan Management**: SupportsHealth, Motor, Life, and Travel categories.
2. **Policy Issuance**: Handles automated policy generation, status transitions (`PENDING_PAYMENT` -> `ACTIVE` -> `EXPIRED`/`CANCELLED`), and cancels.
3. **Premium Payments**: Record transaction IDs, payment modes, and automatically activate pending policies when first premium payment requirements are met.
4. **Claims System**: Raise claims against active policies, upload multiple supporting documents, and display a visual vertical history timeline for status updates.

---

## 🛠️ Tech Stack

### Backend
- **Core**: Spring Boot 3.3.6 (Java 17)
- **Security**: Spring Security 6 + JWT Bearer Auth + BCrypt Password Encoding
- **Database**: MySQL 8.x + Hibernate/Spring Data JPA
- **APIs & Documentation**: RESTful APIs + Springdoc OpenAPI (Swagger UI)
- **Utilities**: Lombok, Jakarta Validation

### Frontend
- **Design System**: Premium Dark SaaS Theme (Glassmorphism card interfaces, smooth micro-animations, curated color palettes)
- **Styling**: Tailwind CSS v4 (CDN) + Custom Vanilla CSS Layouts
- **Logic**: Vanilla ES6 JavaScript (No React/Vue framework dependencies)
- **Responsive**: Sidebar desktop layout, collapsible hamburger menu for mobile/tablet.

---

## 🏁 Getting Started

### 1. Database Configuration
1. Open your MySQL Workbench or CLI client.
2. Ensure MySQL is running on `localhost:3306`.
3. The application will automatically create the database `insurance_db` on startup if it doesn't exist, using credentials:
   - **Username**: `root`
   - **Password**: `root`
   - *(Note: If your MySQL password is different, please modify it in `backend/src/main/resources/application.properties` before launching).*

### 2. Run the Backend
Navigate to the `backend` directory and run:
```bash
mvn spring-boot:run
```
Once started:
- The server will run at `http://localhost:8080`.
- The database will automatically seed default accounts and catalog items (see Seeder credentials below).
- **Swagger Documentation**: View all API details at `http://localhost:8080/swagger-ui.html`.

### 3. Run the Frontend
Since the frontend is built in Vanilla HTML/CSS/JS, you can launch it using any simple local server.
If you have Node.js installed, launch a simple dev server in the `frontend` folder:
```bash
# Using live-server (if installed globally)
live-server --port=5500

# Or using VS Code's "Live Server" extension (running on http://127.0.0.1:5500 or http://localhost:5500)
```
*Note: CORS is pre-configured on the backend to allow requests from port `5500` or `3000`.*

---

## 🔑 Seeded Accounts (Default Credentials)

Upon application startup, if the database is empty, the seeder automatically populates the system with the following accounts (Passwords are encrypted using BCrypt):

| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@insurance.com` | `Admin@123` |
| **Agent** | `agent@insurance.com` | `Agent@123` |
| **Customer** | `customer@insurance.com` | `Customer@123` |

### Seeded Products
- Health Insurance (Plans: *Family Health Care*, *Senior Citizen Medical Cover*)
- Motor Insurance (Plans: *Comprehensive Car Insurance*, *Two Wheeler Protection*)
- Life Insurance (Plans: *Term Life Standard*, *Whole Life Shield*)
- Travel Insurance (Plans: *Globe Trotter Plus*)

---

## 📂 Project Structure

```
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/insurance/
│       ├── config/          # JWT Security and Swagger settings
│       ├── controller/      # REST API Controllers (all 8 resources)
│       ├── dto/             # Request & Response Payload objects
│       ├── entity/          # JPA entities & Enums
│       ├── exception/       # Custom Exceptions & Global Exception Handler
│       ├── repository/      # JPA Data repositories
│       ├── seeder/          # Database seeder (CommandLineRunner)
│       └── service/         # Business services & implementations
└── frontend/
    ├── css/style.css        # Premium dark theme and design tokens
    ├── js/                  # Page-specific dynamic scripts
    ├── index.html           # Login page
    ├── register.html        # Registration page
    ├── dashboard.html       # Role-based stats & charts dashboard
    ├── users.html           # User & agent accounts list (Admin only)
    ├── customers.html       # Customer profile view & edit page
    ├── products.html        # Products CRUD / catalog listing
    ├── plans.html           # Coverage plans CRUD / catalog listing
    ├── policies.html        # Buying, issuing, & canceling policies
    ├── payments.html        # Recording premium transactions
    └── claims.html          # Submitting, reviewing, deciding claims
```
