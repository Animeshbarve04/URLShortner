# URL Shortener

A simple Spring Boot application that converts long URLs into short URLs. When a user opens the shortened URL, they are automatically redirected to the original URL.

---

# Prerequisites

Before running the project, make sure you have:

- Java 21 installed
- Git (optional, only if you want to clone the repository)

> **Note:** You do **not** need to install Maven separately because this project includes the Maven Wrapper (`mvnw`).

---

# Running the Project

## Step 1: Get the Project

### Option 1: Clone the Repository

```bash
git clone https://github.com/Animeshbarve04/URLShortner.git
cd URLShortner/url-shortner
```

### Option 2: Download as ZIP

1. Open the GitHub repository.
2. Click the **Code** button.
3. Select **Download ZIP**.
4. Extract the ZIP file.
5. Open a terminal inside the extracted `url-shortner` folder.

---

## Step 2: Build the Project

### macOS / Linux

```bash
./mvnw clean install
```

### Windows

```bash
mvnw.cmd clean install
```

This command will:

- Download all required dependencies
- Compile the project
- Run all automated tests

If everything is successful, you should see:

```text
BUILD SUCCESS
```

---

## Step 3: Run the Application

### macOS / Linux

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

Once the application starts, it will be available at:

```
http://localhost:8080
```

Keep this terminal open while testing the application.

---

# Testing the API

You can test the application using **curl** or any API client such as **Postman**.

---

## 1. Create a Short URL

```bash
curl -X POST http://localhost:8080/shorten \
-H "Content-Type: application/json" \
-d '{"url":"https://www.youtube.com/watch?v=4_HOnhB64Dg"}'
```

Example Response

```json
{
  "shortCode": "4",
  "shortUrl": "http://localhost:8080/4"
}
```

---

## 2. Create a Short URL with a Custom Alias

```bash
curl -X POST http://localhost:8080/shorten \
-H "Content-Type: application/json" \
-d '{"url":"https://www.youtube.com/watch?v=4_HOnhB64Dg","customAlias":"myAlias"}'
```

Example Response

```json
{
  "shortCode": "myAlias",
  "shortUrl": "http://localhost:8080/myAlias"
}
```

---

## 3. Redirect Using the Short URL

For an automatically generated short code:

```bash
curl -i http://localhost:8080/4
```

For a custom alias:

```bash
curl -i http://localhost:8080/myAlias
```

Example Response

```text
HTTP/1.1 301 Moved Permanently
Location: https://www.youtube.com/watch?v=4_HOnhB64Dg
Content-Length: 0
```

The `Location` header contains the original URL.

---

## 4. Test a Non-Existing Short URL

```bash
curl -i http://localhost:8080/does-not-exist
```

Expected Response

```text
HTTP/1.1 404 Not Found
```

---

# Running Tests (Optional)

The tests are already executed during the build (`clean install`).

If you want to run them again:

### macOS / Linux

```bash
./mvnw test
```

### Windows

```bash
mvnw.cmd test
```

Expected output:

```text
Tests run: 28, Failures: 0, Errors: 0
```

---

# Notes

- The application uses an **in-memory H2 database**.
- No database setup is required.
- All stored URLs are deleted when the application stops or restarts.
- For more details about the design and implementation, see `ARCHITECTURE.md`.

---

# Additional Reference


If you are unfamiliar with `curl`, refer to the screenshots/images included in this repository for example requests and responses.
=======
