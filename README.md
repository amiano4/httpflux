# HttpService

**HttpService** is a lightweight Java HTTP client abstraction built on `java.net.http.HttpClient`. It simplifies sending synchronous and asynchronous HTTP requests with built-in support for headers and form-data.

## Features

- Supports **GET** and **POST** requests.
- Handles both **synchronous** and **asynchronous** execution.
- Allows **global headers** via `registeredHeaders`.
- Provides **callback-based** success and error handling.

## Usage & Installation

1. **Download the JAR file**:

   - Download the `.jar` file [here](https://github.com/amiano4/httpflux/releases/download/v1%2C1.0/httpflux-v1.1.0.jar)

2. **Add the JAR to Your Project**:

   - For non-modular projects:

     - Include the file in your project's **`lib/`** directory or wherever you store your JAR dependencies.
     - Add it to your classpath

   - For **modular projects**:
     - Simply add it to your module dependencies.
     - In your `module-info.java`, add the following:
       ```java
       requires com.amiano4.httpflux;
       ```
     - This will make the `httpflux` library available to your module.

### 1. Set Base URL (Optional)

```java
HttpService.setBaseUrl("https://api.example.com");
```

### 2. Send a GET Request

```java
HttpService.get("/users")
    .onSuccess(response -> System.out.println(response.body()))
    .onError(Throwable::printStackTrace)
    .executeSync();
```

### 3. Send a POST Request with Form Data

```java
FormDataBuilder formData = new FormDataBuilder().add("name", "John Doe");

HttpService.post("/submit", formData)
    .onSuccess(response -> System.out.println(response.body()))
    .onError(Throwable::printStackTrace)
    .executeAsync();

    // Thread.sleep(3000);
```

> **Note:** Be aware of asynchronous calls. The program might end while waiting for its response.

### 4. Using Custom Headers

```java
HttpHeaders headers = new HttpHeaders().add("Authorization", "Bearer token")
    .append("fileKey", "example.txt", "path/to/example.txt");

HttpService.get("/secure-data", headers)
    .onSuccess(response -> System.out.println(response.body()))
    .onError(Throwable::printStackTrace)
    .executeSync();
```

## Available Methods

### GET Requests

```java
HttpService.get(String url);
HttpService.get(URI url);
HttpService.get(String url, HttpHeaders headers);
HttpService.get(URI url, HttpHeaders headers);
```

### POST Requests

```java
HttpService.post(String url);
HttpService.post(URI url);
HttpService.post(String url, FormDataBuilder formData);
HttpService.post(URI url, FormDataBuilder formData);
HttpService.post(String url, FormDataBuilder formData, HttpHeaders headers);
HttpService.post(URI url, FormDataBuilder formData, HttpHeaders headers);
```

## License

This project is open-source. Feel free to modify and use it in your projects.

## Developer Note

I've been too lazy to learn more Java features and badly wanted a straightforward approach to sending HTTP requests. So I created this library, hoping to solve the hassle.
