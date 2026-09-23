# Doctor Appointment Management System

A complete academic Doctor Appointment Management System built with **Java 21, Spring Boot 3.5.5, Spring MVC, Spring Data MongoDB, MongoDB Atlas, Thymeleaf, HTML5, CSS3, Lombok, Jakarta Validation, Spring Security and BCrypt**.

## Features

### Admin
- Secure admin login
- Dashboard statistics
- Doctor CRUD
- Patient CRUD
- Appointment management
- Confirm / complete / cancel appointments
- Search and view details

### Patient
- Registration and login
- Patient dashboard
- Doctor search
- Doctor details
- Appointment booking
- Own appointment list
- Appointment cancellation/editing
- Profile update

### Security
- BCrypt password hashing
- Role-based authorization
- Admin and patient route separation
- CSRF protection
- Environment-based secrets
- User-friendly error handling

## Technology

- Java 21
- Spring Boot 3.5.5
- Spring MVC
- Spring Data MongoDB
- MongoDB Atlas
- Thymeleaf
- HTML5
- CSS3
- Vanilla JavaScript
- Maven
- Lombok
- Jakarta Validation
- Spring Security
- BCrypt

No MySQL, JPA, Hibernate, JpaRepository, Bootstrap, React, Angular, Vue or Tailwind are used.

## MongoDB collections

- doctors
- patients
- appointments
- users

## Environment variables

Required for production:

```text
MONGODB_URI=mongodb+srv://USERNAME:PASSWORD@CLUSTER/doctor_appointment_db
ADMIN_NAME=System Administrator
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=ChangeThisPassword
PORT=8080
```

Do not commit real credentials.

## Local setup

1. Install Java 21 and Maven.
2. Create a MongoDB Atlas cluster.
3. Create a database user.
4. Add your IP address under Atlas Network Access.
5. Set `MONGODB_URI`.
6. Run:

```bash
mvn clean package
java -jar target/doctor-appointment-management-system-1.0.0.jar
```

Or:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Admin bootstrap

On first startup, the application creates the admin account from:

```text
ADMIN_NAME
ADMIN_EMAIL
ADMIN_PASSWORD
```

If those variables are not set, development defaults are used. Change them before deployment.

## Deployment

The application is prepared for Docker-based cloud deployment. Build first:

```bash
mvn clean package
```

Then:

```bash
docker build -t doctor-appointment-management-system .
docker run -p 8080:8080 \
  -e MONGODB_URI="your-mongodb-uri" \
  -e ADMIN_PASSWORD="your-admin-password" \
  doctor-appointment-management-system
```

For Render/Railway/Fly.io or another platform, configure the same environment variables in the service dashboard and use the generated HTTPS domain. Custom-domain DNS and HTTPS are normally configured in the cloud provider dashboard.

## Project architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MongoDB Atlas
```

## Folder structure

```text
src/main/java/com/doctorappointment
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service

src/main/resources
├── static
│   ├── css
│   └── js
├── templates
│   ├── admin
│   ├── auth
│   ├── error
│   ├── fragments
│   └── patient
└── application.properties
```


## Registration troubleshooting

If `/register` previously returned HTTP 500, use this checklist:

1. Confirm `MONGODB_URI` points to the correct MongoDB Atlas connection string.
2. Confirm the Atlas database user and password are correct and URL-encoded when required.
3. Confirm the current machine IP is allowed in Atlas Network Access.
4. Start the application and open `/register` before submitting the form.
5. Registration validates the form, prevents duplicate email accounts, creates the patient profile, then creates the BCrypt-protected login account.
6. The registration page includes an empty BindingResult on the initial GET so Thymeleaf validation messages cannot cause a template error.
7. All POST forms include the Spring Security CSRF token.

For cloud deployment, set `MONGODB_URI`, `PORT`, `ADMIN_NAME`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD` as environment variables. Do not commit credentials.
