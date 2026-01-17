# booking-service
A Spring Boot–based booking service that demonstrates concurrency-safe room reservations using pessimistic and optimistic locking strategies to prevent double booking under high contention.
# Hotel Booking Concurrency Service

A Spring Boot-based microservice designed to handle high-concurrency hotel room reservations. This project demonstrates how to prevent **Double Booking** using **Pessimistic Locking** and **Database Transaction Management**.

## 🚀 The Problem: Double Booking
When two users attempt to book the last available room at the exact same millisecond, a "Race Condition" occurs. Without proper locking, both users might see the room as "Available" and both bookings might succeed, leading to an overbooked hotel.

## 🛠 The Solution: Pessimistic Locking
This project utilizes JPA's `PESSIMISTIC_WRITE` lock mode.
- When a reservation starts, the service locks the specific inventory rows in MySQL using `SELECT ... FOR UPDATE`.
- Any other concurrent request for the same room must wait until the first transaction commits or rolls back.
- This ensures data integrity and prevents selling the same inventory twice.

[Image of Database locking sequence for concurrent transactions]

## 🏗 Tech Stack
* **Java 21** (Optimized for Apple Silicon/M4)
* **Spring Boot 3.4**
* **Spring Data JPA**
* **MySQL 8.0**
* **Testcontainers** (For isolated integration testing)
* **Lombok**

## 🧪 Integration Testing
The project includes a robust concurrency test suite. Using `Testcontainers` and `CountDownLatch`, we simulate a "race" where multiple threads attempt to book the same room simultaneously.

### Running the Tests
Ensure Docker (or OrbStack) is running on your machine, then execute:
```bash
./mvnw test