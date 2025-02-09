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

## Available Methods in `HttpService`

### **Configuration Methods**
- `setBaseUrl(String url)`: Sets the base URL for all requests.
- `getBaseUrl() -> String`: Retrieves the current base URL.
- `createUrl(String url) -> URI`: Constructs a full URL using the base URL if set.

### **HTTP Methods**
- `get(String url) -> HttpFlux`: Sends a GET request.
- `get(String url, HttpHeaders headers) -> HttpFlux`: Sends a GET request with custom headers.
- `post(String url) -> HttpFlux`: Sends a POST request with an empty form body.
- `post(String url, FormDataBuilder formData) -> HttpFlux`: Sends a POST request with form-data.
- `post(String url, FormDataBuilder formData, HttpHeaders headers) -> HttpFlux`: Sends a POST request with form-data and custom headers.

### **Helper Methods**
- `sendHttpRequest(HttpClient client, HttpRequest request) -> HttpFlux`: Sends an HTTP request and processes the response.

## License

HttpFlux is open-source and available under the MIT License.

## Contributing

Contributions are welcome! Feel free to open issues and submit pull requests.
