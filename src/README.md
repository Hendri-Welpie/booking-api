# 🏨 Booking API & UI — Dockerized Setup

This project provides a **Booking Management System** with a backend API built in **Spring Boot (Java)** and a modern frontend built with **React + Vite**.  
Both services run in **Docker containers** along with a **PostgreSQL database** and **pgAdmin** for database management.

---

## 🧱 Architecture Overview

The stack consists of the following containers:

| Service | Description | Port |
|----------|--------------|------|
| **postgres** | PostgreSQL 16 database for application data | 5433 |
| **pgadmin** | Database management GUI for PostgreSQL | 8050 |
| **booking-api** | Spring Boot backend providing REST endpoints for user auth and reservations | 9080 |
| **booking-ui** | React frontend for managing reservations | 5173 |

All containers communicate through a shared Docker bridge network: `dbnet`.

---

## ⚙️ How It Works

### **1. Backend (booking-api)**
The backend exposes several endpoints under `/api/v1/` for:
- User authentication (`/auth/login`, `/auth/register`)
- Managing reservations (CRUD)
- Checking available rooms

It connects to PostgreSQL using environment variables from the `docker-compose.yml` file.  
Authentication uses JWT tokens. Once logged in, include the token in all requests with:
```bash
-H "Authorization: Bearer <token>"
```

### **2. Frontend (booking-ui)**
- Built using **React + Vite**.
- Communicates with the backend via REST endpoints on port **9080**.
- Provides a clean modern UI to view, reserve, edit, or cancel room bookings.

---

## 🚀 How to Run

### **Prerequisites**
- Docker & Docker Compose installed
- Ports **5433**, **8050**, **9080**, and **5173** available

### **Steps**

1. Clone this repository:
   ```bash
   git clone https://github.com/hendricoetsee/booking-api.git
   cd booking-system
   ```

2. Build and start all services:
   ```bash
   docker compose up -d
   ```

3. Access the applications:
   - **Backend API** → [http://localhost:9080](http://localhost:9080)
   - **Frontend UI** → [http://localhost:5173](http://localhost:5173)
   - **pgAdmin** → [http://localhost:8050](http://localhost:8050)  
     Login: `admin@admin.com` / `password`

4. (Optional) Stop all services:
   ```bash
   docker compose down
   ```

---

## 🧩 Endpoints & Usage Examples

### 🔐 **Authentication**

#### Register a new user
```bash
curl -X POST http://localhost:9080/api/v1/auth/register -H "Content-Type: application/json" -d '{
  "username": "admin",
  "email": "admin@admin.com",
  "password": "password",
  "firstName": "John",
  "lastName": "Doe"
}'
```

#### Login
```bash
curl -X POST http://localhost:9080/api/v1/auth/login -H "Content-Type: application/json" -d '{
  "username": "admin",
  "password": "password"
}'
```
_Response includes a JWT token._

---

### 🧍‍♂️ **User Info**
```bash
curl -X GET http://localhost:9080/api/v1/user -H "Authorization: Bearer <token>"
```

---

### 🏨 **Reservations**

#### Get all reservations
```bash
curl -X GET http://localhost:9080/api/v1/reservations -H "Authorization: Bearer <token>" -H "X-Trace-Id: $(uuidgen)"
```

#### Get available rooms
```bash
curl -X GET "http://localhost:9080/api/v1/reservations/available-rooms?checkin=2025-10-20&checkout=2025-10-25" -H "Authorization: Bearer <token>" -H "X-Trace-Id: $(uuidgen)"
```

#### Create a reservation
```bash
curl -X POST http://localhost:9080/api/v1/reservations -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -H "X-Trace-Id: $(uuidgen)" -d '{
  "userId": "6e1dad99-c16c-40e2-ad7a-d07187892c45",
  "roomId": "22222222-2222-2222-2222-222222222222",
  "firstname": "John",
  "surname": "Doe",
  "roomNum": 102,
  "checkinDate": "2025-10-15",
  "checkoutDate": "2025-10-16"
}'
```

#### Update a reservation
```bash
curl -X PUT http://localhost:9080/api/v1/reservations/a8aa4686-66ca-418b-9e15-bb3917718815 -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{
  "id": "a8aa4686-66ca-418b-9e15-bb3917718815",
  "userId": "6e1dad99-c16c-40e2-ad7a-d07187892c45",
  "roomId": "22222222-2222-2222-2222-222222222222",
  "firstname": "John",
  "surname": "Doe",
  "roomNum": 102,
  "checkinDate": "2025-10-15",
  "checkoutDate": "2025-10-16"
}'
```

#### Cancel a reservation
```bash
curl -X POST http://localhost:9080/api/v1/reservations/11111111-aaaa-1111-aaaa-111111111111/cancel -H "Authorization: Bearer <token>"
```

---

## 🧰 Tech Stack

- **Backend:** Spring Boot (Java 21), JPA, PostgreSQL
- **Frontend:** React + Vite + TypeScript
- **Database:** PostgreSQL 16
- **Containerization:** Docker & Docker Compose
- **Authentication:** JWT
- **Database Admin:** pgAdmin 4

---

## 🩺 Healthcheck & Monitoring

PostgreSQL is automatically health-checked with:
```yaml
test: ["CMD-SHELL", "pg_isready -d bookingdb -U postgres"]
```
The backend depends on PostgreSQL readiness before starting.  
pgAdmin and frontend start afterward.

---

## 🧾 License

This project is open-source and available under the **MIT License**.
