# Chatop-back

![Maven Central](https://img.shields.io/maven-central/v/com.example/chatop-back)

## Overview

Chatop-back is the backend for **Chatop**, a rental platform. This application is built using **Spring Boot** and provides RESTful APIs for managing rentals and user authentication.

---

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- **Java 17** ☕ ![Java Version](https://img.shields.io/badge/Java-17-blue)
- **Maven** (preferably version 3.6 or higher) 📦
- **MySQL** (running and accessible) 🐬 ![Maven Central](https://img.shields.io/maven-central/v/com.example/chatop-back)

---

## Setup

Follow the steps below to set up and run the application:

### 1. **Clone the repository** 🛠️

```bash
git clone https://github.com/EmySim/chatop-back.git
cd chatop-back
```

### 2. **Set environment variables** 🌐

You will need to retrieve the environment variables to configure the application (e.g., `DATABASE_URL`, `DATABASE_USERNAME`, etc.).

Visit the following Gist for instructions on how to set these variables:

[https://gist.github.com/EmySim/](https://gist.github.com/EmySim/4950aa154de3e771f0979bc9480b32fb)

### 3. **Configure MySQL connection** 🗄️

Ensure that your MySQL server is running and accessible. Update the following connection details in your `application.properties` (or use `.env` for externalized configuration):

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

### 4. **Build the project** 🏗️

Use Maven to clean and build the project:

```bash
mvn clean install
```

### 5. **Run the application** 🚀

You can start the application using Maven's Spring Boot plugin:

```bash
mvn spring-boot:run
```

---

## Running Tests 🧪

All unit and integration tests are included in the project. To execute them, use this command:

```bash
mvn test
```

---

## API Documentation 📄

Once the application is running, the API documentation is served at:

[http://localhost:3001/swagger-ui.html](http://localhost:3001/swagger-ui.html)

This provides an interactive UI to explore and test the API endpoints.

---

## Environment Variables 🌍

The following environment variables are required for the application:

- `DATABASE_URL`: URL of the MySQL database.
- `DATABASE_USERNAME`: Username for the MySQL database.
- `DATABASE_PASSWORD`: Password for the MySQL database.
- `JWT_SECRET`: Secret key for signing JWT tokens.
- `JWT_EXPIRATION`: Expiration time for JWT tokens.
- `AWS_ACCESS_KEY_ID`: AWS access key for S3.
- `AWS_SECRET_ACCESS_KEY`: AWS secret key for S3.
- `AWS_REGION`: AWS region for S3.
- `AWS_BUCKET_NAME`: AWS S3 bucket name.

---

## AWS S3 Configuration 🗂️

To configure AWS S3 for image storage, ensure you have the following environment variables set:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION`
- `AWS_BUCKET_NAME`

These variables are used to authenticate and interact with your S3 bucket.

---

## Running with Docker 🐳

To run the application using Docker, follow these steps:

1. **Build the Docker image:**

   ```bash
   docker build -t chatop-back .
   ```

2. **Run the Docker container:**

   ```bash
   docker run -p 3001:3001 --env-file .env chatop-back
   ```

Ensure that your `.env` file contains all the necessary environment variables.

---

## Troubleshooting 🛠️

### Common Issues

1. **Database Connection Error:**
   - Ensure that your MySQL server is running and accessible.
   - Verify the database connection details in your `application.properties` or `.env` file.

2. **AWS S3 Configuration Error:**
   - Ensure that your AWS credentials and region are correctly set in the environment variables.
   - Verify that the S3 bucket name is correct and the bucket exists.

3. **JWT Authentication Error:**
   - Ensure that the `JWT_SECRET` environment variable is set correctly.
   - Verify the expiration time for JWT tokens.

If you encounter any other issues, please refer to the logs for more details or open an issue in the repository.

---

## Additional Information ℹ️

### Logging
The application uses Java's built-in logging framework. Logs are configured via the `application.properties` file and are automatically saved during runtime.

### Security
The application uses **JWT (JSON Web Tokens)** for authentication. Ensure that you set the `JWT_SECRET` environment variable in your `.env` file to securely sign JWT tokens.

### Database
The application requires a **MySQL database**. Ensure the following variables are configured correctly:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

You can import preconfigured schema and tables if needed. (Provide a schema file if applicable.)

---

## Contributing 🤝

We welcome contributions! To contribute, please follow these steps:

1. **Fork the repository:**

   ```bash
   git fork https://github.com/EmySim/chatop-back.git
   ```

2. **Create a new branch for your feature:**

   ```bash
   git checkout -b feature-branch
   ```

3. **Implement your changes.**

4. **Commit your changes:**

   ```bash
   git commit -am "Add [your feature here]"
   ```

5. **Push the new branch to your repository:**

   ```bash
   git push origin feature-branch
   ```

6. **Create a Pull Request:**
   Submit a Pull Request (PR) on the original repository for code review.

---

### License 📜

This project is distributed under the MIT license. See the LICENSE file for details.

---

## Community & Support 💬

If you have questions, suggestions, or encounter issues, please feel free to open an issue in the repository or reach out via discussions on [Chatop-back GitHub repository](https://github.com/EmySim/chatop-back).
