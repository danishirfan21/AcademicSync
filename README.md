# AcademicSync

AcademicSync is a Spring Boot backend integration service that synchronizes academic data across mock university systems.

It demonstrates enterprise integration patterns such as scheduled sync jobs, REST API consumption, validation, upserts, audit logging, retry handling, reporting APIs, and PostgreSQL persistence.

## Why This Project Exists

Universities often depend on multiple systems for student records, courses, enrollments, scheduling, and academic operations.

AcademicSync simulates a backend service that keeps this data consistent across systems while giving engineers visibility into sync runs, failures, retries, stale records, and operational health.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Liquibase
- Docker Compose
- Scheduled Jobs
- REST APIs

## Core Features

- Mock SIS source APIs
- Manual student sync
- Manual course sync
- Manual enrollment sync
- Scheduled academic sync
- Sync run history
- Sync error logging
- Retry failed records
- Reporting APIs
- Stale data detection

## Example Use Case

AcademicSync can simulate data synchronization between:

- Student Information System
- Course Catalog System
- Enrollment System
- Internal Registrar reporting tools

## API Overview

### Health

GET /api/health

### Mock SIS APIs

GET /mock-sis/students 
GET /mock-sis/courses 
GET /mock-sis/enrollments

### Sync APIs

POST /api/sync/students 
POST /api/sync/courses 
POST /api/sync/enrollments 
POST /api/sync/retry-errors

GET /api/sync/runs 
GET /api/sync/runs/{id} 
GET /api/sync/errors

### Scheduler

GET /api/scheduler/status

### Reports

GET /api/reports/students-by-program 
GET /api/reports/enrollments-by-course 
GET /api/reports/sync-health 
GET /api/reports/stale-records?olderThanHours=24

## Running Locally

Start PostgreSQL:

docker compose up -d postgres

Run Spring Boot:

./mvnw spring-boot:run

## Running with Docker Compose

Build the application:

./mvnw clean package -DskipTests

Start services:

docker compose up --build

The API will be available at:

http://localhost:8080


## Suggested Demo Flow

1. Check service health.

GET /api/health


2. View source SIS data.

GET /mock-sis/students 
GET /mock-sis/courses 
GET /mock-sis/enrollments

3. Run sync in dependency order.

POST /api/sync/students 
POST /api/sync/courses 
POST /api/sync/enrollments

4. Review sync history.

GET /api/sync/runs

5. View reports.

GET /api/reports/students-by-program 
GET /api/reports/enrollments-by-course 
GET /api/reports/sync-health

6. Test stale record reporting.

GET /api/reports/stale-records?olderThanHours=1

## Portfolio Relevance

This project demonstrates skills directly relevant to enterprise software engineering roles:

- backend integration development
- ERP/SIS-style data synchronization
- SQL and relational data modeling
- scheduled batch processing
- production support visibility
- retry and failure recovery
- reporting and operational insights
- maintainable Spring Boot architecture

## Demo Flow

### 1. Check API health

GET /api/health

### 2. Inspect mock SIS source data

GET /mock-sis/students  
GET /mock-sis/courses  
GET /mock-sis/enrollments

### 3. Run manual sync

POST /api/sync/students  
POST /api/sync/courses  
POST /api/sync/enrollments

### 4. Review sync history

GET /api/sync/runs

### 5. Review unresolved sync errors

GET /api/sync/errors

### 6. Retry failed records

POST /api/sync/retry-errors

### 7. View operational reports

GET /api/reports/students-by-program  
GET /api/reports/enrollments-by-course  
GET /api/reports/sync-health  
GET /api/reports/stale-records?olderThanHours=24

## CSV Import

AcademicSync supports CSV-based student imports for operational workflows where registrar or academic teams receive bulk data files.











