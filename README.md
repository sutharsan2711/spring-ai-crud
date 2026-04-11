# 🤖 Spring AI CRUD — Library Management System

> **Spring Boot 3.2.5 · Spring AI 1.0.0-M3 · PostgreSQL · Ollama AI · JWT Security**

A full-stack **REST API** for Library Management with AI-powered database queries, JWT authentication, book buying/returning system, due date tracking, fine calculation, and email notifications.

---

## 🌐 Live Demo

```
Base URL: https://spring-ai-crud-1.onrender.com
```

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.5 | Main framework |
| Spring AI | 1.0.0-M3 | Ollama AI integration |
| Spring Security | 3.2.5 | JWT authentication |
| PostgreSQL | 15+ | Main database |
| Ollama + llama3 | Latest | Local AI model |
| Hibernate / JPA | 6.4.4 | ORM |
| JWT (jjwt) | 0.11.5 | Token generation |
| Lombok | 1.18.32 | Boilerplate reduction |
| Java | 21 | Programming language |
| Maven | 3.x | Build tool |
| Docker | Latest | Containerization |

---

## ✨ Features

- ✅ **Person CRUD** — Create, Read, Update, Delete with image upload (stored as BYTEA in PostgreSQL)
- ✅ **JWT Authentication** — Signup / Login with BCrypt encrypted passwords
- ✅ **Role-Based Access** — USER, ADMIN, MODERATOR roles
- ✅ **Book Management** — Full CRUD with category, price, availability status
- ✅ **Book Buying & Returning** — Track book status (AVAILABLE / BOUGHT)
- ✅ **Due Date + Fine System** — Auto calculate fine (₹10/day after due date)
- ✅ **Email Notifications** — Book issued, reminders, overdue alerts, return confirmation
- ✅ **AI Database Queries** — Ask questions in plain English, Ollama converts to SQL
- ✅ **Auto Deploy** — GitHub push triggers auto-deploy on Render

---

## 📁 Project Structure

```
springaicrud/
│
├── Dockerfile
├── render.yaml
├── pom.xml
│
└── src/main/java/com/example/springaicrud/
    ├── SpringaicrudApplication.java
    ├── DataInitializer.java
    │
    ├── entity/
    │   ├── Person.java
    │   ├── User.java
    │   ├── Role.java
    │   ├── Book.java
    │   ├── BookCategory.java
    │   └── BuyingDetails.java
    │
    ├── repository/
    │   ├── PersonRepository.java
    │   ├── UserRepository.java
    │   ├── RoleRepository.java
    │   ├── BookRepository.java
    │   ├── BookCategoryRepository.java
    │   └── BuyingDetailsRepository.java
    │
    ├── dto/
    │   ├── PersonDTO.java
    │   ├── SignupRequest.java
    │   ├── LoginRequest.java
    │   ├── AuthResponse.java
    │   ├── BookRequest.java
    │   ├── BookResponse.java
    │   ├── BookCategoryRequest.java
    │   ├── BookCategoryResponse.java
    │   ├── BuyingRequest.java
    │   └── BuyingResponse.java
    │
    ├── service/
    │   ├── PersonService.java
    │   ├── AiService.java
    │   ├── BookService.java
    │   ├── BookCategoryService.java
    │   ├── BuyingService.java
    │   └── EmailService.java
    │
    ├── controller/
    │   ├── PersonController.java
    │   ├── AiController.java
    │   ├── BookController.java
    │   ├── BookCategoryController.java
    │   └── BuyingController.java
    │
    ├── auth/
    │   ├── JwtUtil.java
    │   ├── JwtFilter.java
    │   ├── AuthService.java
    │   ├── AuthController.java
    │   └── SecurityConfig.java
    │
    ├── scheduler/
    │   └── FineScheduler.java
    │
    └── exception/
        └── GlobalExceptionHandler.java
```

---

## 🗄️ Database Schema

```
roles ──────────────── users
  id                     id
  name                   name
  created_at             email
  updated_at             password
                         role_id (FK)
                         created_at
                         updated_at

persons                book_category
  id                     id
  name                   category
  mobile_no
  address              book
  image (bytea)          id
  image_url              name
  created_at             author
  updated_at             price
                         is_active
                         category_id (FK)
                         admin_id (FK)
                         created_at
                         updated_at

buying_details
  id
  book_id (FK)
  user_id (FK)
  issue_date
  due_date
  return_date
  fine_amount
  fine_paid
  days_late
  status
  created_at
  updated_at
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.x
- PostgreSQL 15+
- Ollama — https://ollama.com/download
- Git

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/sutharsan2711/spring-ai-crud.git
cd spring-ai-crud
```

**2. Setup PostgreSQL**
```sql
CREATE DATABASE springai_db;
```

**3. Configure application**
```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

Edit `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/springai_db
spring.datasource.username=postgres
spring.datasource.password=your_password
jwt.secret=your_secret_key_min_32_chars
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

**4. Start Ollama**
```bash
ollama pull llama3
ollama serve
```

**5. Run the application**
```bash
mvn clean install
mvn spring-boot:run
```

**6. Open in browser**
```
http://localhost:8080
```

---

## ⚙️ Environment Variables (for Render)

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `JWT_SECRET` | JWT signing secret (min 32 chars) |
| `OLLAMA_BASE_URL` | Ollama server URL |
| `SPRING_MAIL_USERNAME` | Gmail address |
| `SPRING_MAIL_PASSWORD` | Gmail app password |

---

## 📡 API Reference

### 🔐 Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/signup` | ❌ | Register new user |
| POST | `/api/auth/login` | ❌ | Login & get JWT token |

**Signup Request:**
```json
{
  "name": "John Kumar",
  "email": "john@gmail.com",
  "password": "john1234"
}
```

**Login Response:**
```json
{
  "id": 1,
  "name": "John Kumar",
  "email": "john@gmail.com",
  "roleId": 1,
  "roleName": "USER",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "loginTime": "2026-04-09T10:00:00",
  "expTime": "2026-04-10T10:00:00",
  "message": "Login successful!"
}
```

---

### 👤 Person Management

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/persons` | ✅ | Create person with image |
| GET | `/api/persons` | ✅ | Get all persons |
| GET | `/api/persons/{id}` | ✅ | Get person by ID |
| GET | `/api/persons/{id}/image` | ❌ | Get person image |
| PUT | `/api/persons/{id}` | ✅ | Update person |
| DELETE | `/api/persons/{id}` | ✅ | Delete person |

---

### 📚 Book Category

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/categories` | ✅ | Create category |
| GET | `/api/categories` | ❌ | Get all categories |
| GET | `/api/categories/{id}` | ❌ | Get by ID |
| PUT | `/api/categories/{id}` | ✅ | Update category |
| DELETE | `/api/categories/{id}` | ✅ | Delete category |

---

### 📖 Book Management

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/books` | ✅ | Create book |
| GET | `/api/books` | ❌ | Get all books |
| GET | `/api/books/available` | ❌ | Available books |
| GET | `/api/books/bought` | ❌ | Bought books |
| GET | `/api/books/{id}` | ❌ | Get by ID |
| GET | `/api/books/category/{id}` | ❌ | By category |
| GET | `/api/books/admin/{id}` | ❌ | By admin |
| GET | `/api/books/search/name?q=` | ❌ | Search by name |
| GET | `/api/books/search/author?q=` | ❌ | Search by author |
| PUT | `/api/books/{id}` | ✅ | Update book |
| DELETE | `/api/books/{id}` | ✅ | Delete book |

**Create Book Request:**
```json
{
  "name": "Clean Code",
  "author": "Robert C. Martin",
  "categoryId": 1,
  "adminId": 1,
  "price": 499.99
}
```

---

### 🛒 Buying System

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/buying/buy` | ✅ | Buy a book |
| POST | `/api/buying/return` | ✅ | Return a book |
| POST | `/api/buying/pay-fine/{id}` | ✅ | Pay fine |
| GET | `/api/buying` | ✅ | All records |
| GET | `/api/buying/overdue` | ✅ | Overdue books |
| GET | `/api/buying/user/{id}` | ✅ | User purchases |
| GET | `/api/buying/book/{id}` | ✅ | Book history |

**Buy Book Request:**
```json
{
  "bookId": 1,
  "userId": 1,
  "borrowDays": 14
}
```

**Return Book Response:**
```json
{
  "issueDate": "2026-04-09",
  "dueDate": "2026-04-23",
  "returnDate": "2026-04-26",
  "daysLate": 3,
  "fineAmount": 30.00,
  "status": "RETURNED_LATE",
  "message": "Fine: ₹30 for 3 days late"
}
```

---

### 🤖 AI Queries

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/ai/ask` | ✅ | Ask Ollama anything |
| POST | `/api/ai/db` | ✅ | Ask about DB in plain English |

**AI DB Request:**
```json
{ "question": "Show all overdue books" }
```

**AI DB Response:**
```json
{
  "yourQuestion": "Show all overdue books",
  "generatedSQL": "SELECT * FROM buying_details WHERE status = 'OVERDUE'",
  "totalRows": 2,
  "data": [...],
  "aiAnswer": "There are 2 overdue books in the system."
}
```

---

## 🔐 Authentication Guide

All protected APIs need this header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Headers Reference

| API Type | Content-Type | Authorization |
|----------|-------------|---------------|
| Signup / Login | `application/json` | Not needed |
| Create/Update Person | Auto (form-data) | `Bearer {token}` |
| Create Book | `application/json` | `Bearer {token}` |
| Buy / Return Book | `application/json` | `Bearer {token}` |
| GET requests | Not needed | Only if protected |
| DELETE requests | Not needed | `Bearer {token}` |

---

## 💰 Fine System

```
Fine Calculation:
fine = days_late × ₹10 per day

Example:
Due date    = April 23
Return date = April 26
Days late   = 3
Fine        = 3 × 10 = ₹30
```

### Book Status Values

| Status | Meaning |
|--------|---------|
| `ACTIVE` | Book borrowed, not yet returned |
| `OVERDUE` | Past due date, fine accumulating |
| `RETURNED_ONTIME` | Returned before due date |
| `RETURNED_LATE` | Returned after due date |

---

## 📧 Email Notifications

| Trigger | Email Sent |
|---------|-----------|
| Book bought | "Book Issued" with due date |
| 3 days before due | "Due Date Reminder" |
| After due date | "Overdue Alert" with fine amount |
| Book returned | "Return Confirmation" with fine summary |

> Emails run automatically every day at 9:00 AM via scheduler.

---

## 🐳 Docker

```bash
# Build image
docker build -t springaicrud .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e JWT_SECRET=your_secret \
  springaicrud
```

---

## 🚢 Deploy to Render

1. Push code to GitHub
2. Create PostgreSQL on Render (Free)
3. Create Web Service → Select Docker
4. Add environment variables
5. Deploy — auto deploys on every `git push`

**Auto Deploy:**
```bash
git add .
git commit -m "your changes"
git push origin main
# Render auto-deploys in 5-10 mins ✅
```

---

## 🔄 JWT Token Payload

```json
{
  "sub"    : "john@gmail.com",
  "userId" : 1,
  "roleId" : 1,
  "role"   : "USER",
  "iat"    : 1711859494,
  "exp"    : 1711945894
}
```

> Verify your token at https://jwt.io

---

## ❌ Error Responses

| Status | Message | Cause |
|--------|---------|-------|
| 400 | Email already registered | Duplicate signup |
| 400 | Incorrect password | Wrong login password |
| 400 | Category not found | Invalid categoryId |
| 400 | Book already bought | Book not available |
| 400 | Role USER not found | Roles not initialized |
| 401 | Unauthorized | Missing/invalid JWT token |
| 500 | Internal Server Error | DB or server error |

---

## 📊 Database Tables

| Table | Description |
|-------|-------------|
| `roles` | USER, ADMIN, MODERATOR |
| `users` | Registered users with BCrypt password |
| `persons` | Person records with image |
| `book_category` | Book categories |
| `book` | Books with price and availability |
| `buying_details` | Purchase records with fine tracking |

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m "Add new feature"`
4. Push to branch: `git push origin feature/new-feature`
5. Open Pull Request

---

## 📄 License

MIT License — feel free to use this project for learning and development.

---

## 👨‍💻 Author

**Sutharsan**
- GitHub: [@sutharsan2711](https://github.com/sutharsan2711)
- Live API: https://spring-ai-crud-1.onrender.com

---

<div align="center">
  <p>Built with ❤️ using Spring Boot + Spring AI + PostgreSQL + Ollama</p>
</div>
