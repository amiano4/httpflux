# HttpFlux

HttpFlux is an **opinionated**, chainable, and lightweight HTTP client abstraction for Java, designed to simplify HTTP requests while providing a clean and fluent API. It supports GET and POST requests, automatic header management, and built-in error handling.

## Features

- **Chainable API** for clean and readable code.
- **Supports GET & POST requests** with proper multipart form-data handling.
- **Automatic header management** with easy merging and updating.
- **Built-in error handling** via exceptions and callbacks.
- **Lightweight & dependency-free** (relies on Java's built-in HttpClient).

## Installation

### Maven

```xml
// not yet
```

## Usage

1. **Download the JAR file**:
   - Download the `.jar` file [here](https://github.com/amiano4/httpflux/releases/download/v1.0/httpflux-1.0.jar)

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

### Basic GET Request

```java
HttpFlux httpFlux = HttpService.get("https://httpbin.org/get");
httpFlux.onSuccess(...);
httpFlux.onError(...);

// or

HttpService.get("https://httpbin.org/get")
    .onSuccess(response -> System.out.println(response.body()))
    .onError(error -> System.out.println("Error: " + error.getMessage()));
```

### Basic POST Request with Form Data

```java
FormDataBuilder formData = new FormDataBuilder()
    .append("key", "value")
    .append("fileKey", "example.txt", "path/to/example.txt");

HttpService.post("https://httpbin.org/post", formData)
    .onSuccess(response -> System.out.println(response.body()))
    .onError(error -> {
      // use error.getResponse() to get the response object 
      System.out.println("Error: " + error.getMessage());
    });
```

### Using `registeredHeaders` for Automatic Header Inclusion

If you need to include specific headers in all requests, you can use `HttpService.registeredHeaders` to set them globally:

```java
HttpService.registeredHeaders.add("Authorization", "Bearer your_token");
HttpService.registeredHeaders.add("User-Agent", "HttpFlux/1.0");

HttpService.get("https://httpbin.org/get")
    .onSuccess(response -> System.out.println(response.body()));
```

Any headers added to `registeredHeaders` will automatically be included in every request.

### Full Usage Example

```java
// Set base URL
HttpService.setBaseUrl("https://httpbin.org");

// Register global headers
HttpService.registeredHeaders.add("Authorization", "Bearer sample_token");
HttpService.registeredHeaders.add("User-Agent", "HttpFlux/1.0");

// Prepare custom headers for a specific request
HttpHeaders customHeaders = new HttpHeaders().add("Custom-Header", "CustomValue");

// Prepare form data
FormDataBuilder formData = new FormDataBuilder()
    .append("username", "test_user")
    .append("profile_picture", "avatar.jpg", "path/to/avatar.jpg");

// Perform GET request
HttpService.get("/get", customHeaders)
    .onSuccess(response -> System.out.println("GET Response: " + response.body()))
    .onError(error -> System.out.println("GET Error: " + error.getMessage()));

// Perform POST request
HttpService.post("/post", formData, customHeaders)
    .onSuccess(response -> System.out.println("POST Response: " + response.body()))
    .onError(error -> System.out.println("POST Error: " + error.getMessage()));
```

## Available Methods in `HttpService`

### **Configuration Methods**
- `setBaseUrl(String url)`: Sets the base URL for all requests.
- `getBaseUrl() -> String`: Retrieves the current base URL.
- `createUrl(String url) -> URI`: Constructs a full URL using the base URL if set.
- `registeredHeaders`: A global `HttpHeaders` instance for automatically including headers in every request.

### **HTTP Methods**
- `get(String url) -> HttpFlux`: Sends a GET request.
- `get(String url, HttpHeaders headers) -> HttpFlux`: Sends a GET request with custom headers.
- `post(String url) -> HttpFlux`: Sends a POST request with an empty form body.
- `post(String url, FormDataBuilder formData) -> HttpFlux`: Sends a POST request with form-data.
- `post(String url, FormDataBuilder formData, HttpHeaders headers) -> HttpFlux`: Sends a POST request with form-data and custom headers.

### **Helper Methods**
- `sendHttpRequest(HttpClient client, HttpRequest request) -> HttpFlux`: Sends an HTTP request and processes the response.

## Available Methods in `HttpHeaders`

### **Header Management**
- `add(String name, String value) -> HttpHeaders`: Adds a new header to the list.
- `update(String name, String value) -> HttpHeaders`: Updates an existing header or adds a new one if not found.
- `merge(HttpHeaders... headersList) -> HttpHeaders`: Merges multiple `HttpHeaders` instances into one.
- `clear()`: Removes all headers from the list.
- `toString() -> String`: Returns a string representation of all headers.

### **Header Class**
Each header is represented by an inner `HttpHeaders.Header` class:
- `getName() -> String`: Retrieves the header name.
- `getValue() -> String`: Retrieves the header value.
- `setName(String name)`: Updates the header name.
- `setValue(String value)`: Updates the header value.

## License

HttpFlux is open-source and available under the MIT License.

## Contributing

Contributions are welcome! Feel free to open issues and submit pull requests.
