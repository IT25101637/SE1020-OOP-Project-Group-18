# Online Bookstore — Help & Documentation

A Spring Boot REST API backend for an online bookstore. Data is stored in flat pipe-delimited text files (no database required). The frontend is served as static HTML/CSS/JS from the same application.

---

## Table of Contents

- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Data Storage](#data-storage)
- [API Reference](#api-reference)
  - [Users](#users-apiv1users)
  - [Admin](#admin-apiadmin)
  - [Books](#books-apibooks)
  - [Cart](#cart-apicart)
  - [Payment Cards](#payment-cards-apipayment-cards)
  - [Payments](#payments-apipayments)
  - [Reviews](#reviews-apireviews)
- [Frontend Pages](#frontend-pages)
- [Tech Stack](#tech-stack)

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Run the application

```bash
# Clone the repository
git clone <repo-url>
cd onlinebookstore

# Build and run
./mvnw spring-boot:run
```

The server starts on **port 8091** by default.

```
http://localhost:8091
```

To change the port, edit `src/main/resources/application.yaml`:

```yaml
server:
  port: 8091
```

### Build a JAR

```bash
./mvnw clean package
java -jar target/onlinebookstore-0.0.1-SNAPSHOT.jar
```

---

## Project Structure

```
src/
└── main/
    ├── java/com/bookstore/onlinebookstore/
    │   ├── controller/      # REST controllers (HTTP layer)
    │   ├── service/         # Business logic
    │   ├── repository/      # File I/O and data access
    │   ├── model/           # Domain objects (Book, User, Payment, …)
    │   ├── dto/             # Data Transfer Objects returned to clients
    │   └── util/
    │       ├── FileHandler.java      # Abstract file read/write helper
    │       ├── LinkedListUtil.java   # Custom linked-list utilities
    │       └── SortUtil.java         # Sorting helpers
    └── resources/
        ├── application.yaml
        ├── data/            # Flat-file "database" (pipe-delimited .txt)
        └── static/          # Frontend (HTML, CSS, JS)
```

---

## Data Storage

All data is persisted as **pipe-delimited (`|`) text files** inside `src/main/resources/data/`:

| File | Contents |
|------|----------|
| `users.txt` | Registered users |
| `admins.txt` | Admin accounts |
| `books.txt` | Book catalogue |
| `carts.txt` | Shopping cart items |
| `payments.txt` | Payment records |
| `paymentCards.txt` | Saved payment cards |
| `reviews.txt` | Book reviews |

Each line is one record. Fields are separated by `|`. The `FileHandler` utility handles all reads, writes, and appends.

---

## API Reference

All endpoints return JSON. Successful responses include `"status": "success"`; errors include `"status": "error"` and a `"message"` field.

Base URL: `http://localhost:8091`

---

### Users `/api/users`

#### Register a new user
```
POST /api/users/register
```
**Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "0771234567",
  "password": "secret123"
}
```

#### Login
```
POST /api/users/login
```
**Body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

#### Get user profile
```
GET /api/users/profile/{email}
```

#### Update user profile
```
PUT /api/users/profile/{email}
PUT /api/users/update/{email}
```
**Body:** Same fields as registration (all optional except what you want to change).

#### Delete user account
```
DELETE /api/users/{email}
DELETE /api/users/delete/{email}
```

---

### Admin `/api/admin`

#### Register admin
```
POST /api/admin/register
```

#### Admin login
```
POST /api/admin/login
```
**Body:**
```json
{
  "email": "admin@bookstore.com",
  "password": "adminpass"
}
```

#### Get admin profile
```
GET /api/admin/profile/{email}
```

#### List all admins
```
GET /api/admin/all
```

#### Update admin profile
```
PUT /api/admin/profile/{email}
```

#### Delete admin
```
DELETE /api/admin/{email}
```

---

### Books `/api/books`

#### Get all books
```
GET /api/books
```
**Response:**
```json
{
  "status": "success",
  "count": 12,
  "books": [ ... ]
}
```

#### Get featured books
```
GET /api/books/featured
```

#### Get books by category
```
GET /api/books/category/{category}
```
Valid categories: `Business`, `Technology`, `Romantic`, `Adventure`, `Fictional`, `All Genre`

#### Get a single book
```
GET /api/books/{bookId}
```

#### Create a book *(admin)*
```
POST /api/books
```
**Body:**
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 29.99,
  "category": "Technology",
  "description": "A handbook of agile software craftsmanship.",
  "imageUrl": "/images/clean-code.jpg",
  "isFeatured": true
}
```
Required fields: `title`, `author`, `price`, `category`, `imageUrl`

#### Update a book *(admin)*
```
PUT /api/books/{bookId}
```
**Body:** Same as create.

#### Delete a book *(admin)*
```
DELETE /api/books/{bookId}
```

---

### Cart `/api/cart`

#### Get cart for a user
```
GET /api/cart/{userId}
```

#### Add item to cart
```
POST /api/cart/{userId}/items
```
**Body:**
```json
{
  "bookId": "BOOK001",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 29.99,
  "imageUrl": "/images/clean-code.jpg",
  "quantity": 1
}
```

#### Update item quantity
```
PUT /api/cart/{userId}/items/{bookId}
```
**Body:**
```json
{ "quantity": 3 }
```
Set `quantity` to `0` or less to remove the item.

#### Remove a specific item
```
DELETE /api/cart/{userId}/items/{bookId}
```

#### Clear the entire cart
```
DELETE /api/cart/{userId}/clear
```

---

### Payment Cards `/api/payment-cards`

#### List saved cards for a user
```
GET /api/payment-cards/{userId}
```

#### Add a new card
```
POST /api/payment-cards/{userId}
```
**Body:**
```json
{
  "cardNumber": "4111111111111111",
  "cardHolderName": "John Doe",
  "expiryDate": "12/27",
  "cvv": "123"
}
```

#### Update a card
```
PUT /api/payment-cards/{userId}/{cardId}
```
**Body:** Same as add.

#### Delete a card
```
DELETE /api/payment-cards/{userId}/{cardId}
```

#### Set a card as default
```
POST /api/payment-cards/{userId}/{cardId}/set-default
```

#### Get the default card
```
GET /api/payment-cards/{userId}/default
```

---

### Payments `/api/payments`

#### Process a payment (checkout)
```
POST /api/payments/process
```
**Body:**
```json
{
  "userId": "USER001",
  "cardId": "CARD001",
  "cartItems": [ ... ]
}
```

#### Get a specific payment
```
GET /api/payments/{paymentId}
```

#### Get all payments for a user
```
GET /api/payments/user/{userId}
```

#### Get purchased books for a user
```
GET /api/payments/user/{userId}/books
```

#### Check if a user purchased a specific book
```
GET /api/payments/user/{userId}/books/{bookId}/purchased
```
**Response:**
```json
{ "status": "success", "purchased": true }
```

---

### Reviews `/api/reviews`

#### Get reviews for a book
```
GET /api/reviews/book/{bookId}
```

#### Get reviews by a user
```
GET /api/reviews/user/{userId}
```

#### Get top recent reviews (latest 3)
```
GET /api/reviews/top
```

#### Add a review
```
POST /api/reviews/user/{userId}/book/{bookId}
```
**Body:**
```json
{
  "rating": 5,
  "comment": "Absolutely fantastic read!"
}
```
`rating` must be between 1 and 5.

#### Update a review
```
PUT /api/reviews/user/{userId}/{reviewId}
```
**Body:** Same as add.

#### Delete a review
```
DELETE /api/reviews/user/{userId}/{reviewId}
```

---

## Frontend Pages

Static pages served from `src/main/resources/static/`:

| URL | Page |
|-----|------|
| `/` or `/index.html` | Home / book catalogue |
| `/login.html` | User login |
| `/register.html` | User registration |
| `/profile.html` | User profile |
| `/review.html` | Write / view reviews |
| `/payment-cards.html` | Manage saved cards |
| `/admin/login` | Admin login |
| `/admin/dashboard` | Admin dashboard |
| `/admin/books` | Admin book management |
| `/cart` | Shopping cart page |

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Build | Maven |
| Storage | Flat pipe-delimited text files |
| Frontend | HTML5 · CSS3 · Vanilla JS · jQuery 1.11 |
| Server port | 8091 |

---

## Notes

- **No database** is required. All data lives in `.txt` files under `src/main/resources/data/`.
- All endpoints are **CORS-enabled** (`@CrossOrigin(origins = "*")`), so the frontend can call the API from any origin during development.
- The `points` field on a `User` is a loyalty/reward points balance, incremented on purchase.
- Payment card numbers are stored internally; only the last 4 digits (`cardLast4`) appear in payment records.
