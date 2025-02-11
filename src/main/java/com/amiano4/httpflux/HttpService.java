package com.amiano4.httpflux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * A simple HTTP client service that supports synchronous and asynchronous GET and POST requests.
 */
public class HttpService {
	public static final boolean ASYNC = true;
	public static final boolean SYNC = false;

	public static final HttpHeaders registeredHeaders = new HttpHeaders();
	private static String baseUrl = null;

	private OnSuccess responseHandler;
	private OnError errorHandler;
	private HttpClient client;
	private HttpRequest request;

	/**
	 * Constructs an HttpService with a specified HttpClient and HttpRequest.
	 * 
	 * @param client  The HttpClient instance.
	 * @param request The HttpRequest instance.
	 */
	public HttpService(HttpClient client, HttpRequest request) {
		this.client = client;
		this.request = request;
	}

	/**
	 * Sets the success callback handler.
	 * 
	 * @param fn The success handler function.
	 * @return The current HttpService instance.
	 */
	public HttpService onSuccess(OnSuccess fn) {
		responseHandler = fn;
		return this;
	}

	/**
	 * Sets the error callback handler.
	 * 
	 * @param fn The error handler function.
	 * @return The current HttpService instance.
	 */
	public HttpService onError(OnError fn) {
		errorHandler = fn;
		return this;
	}

	/**
	 * Executes the request synchronously.
	 * 
	 * @throws Exception If an error occurs during execution.
	 */
	public void executeSync() throws Exception {
		sendHttpRequest(client, request, responseHandler, errorHandler, SYNC);
	}

	/**
	 * Executes the request asynchronously.
	 * 
	 * @throws Exception If an error occurs during execution.
	 */
	public void executeAsync() throws Exception {
		sendHttpRequest(client, request, responseHandler, errorHandler, ASYNC);
	}

	/**
	 * Sends an HTTP request with optional asynchronous execution. Lowest level call to send HTTP requests.
	 * 
	 * @param client    The HttpClient to be used.
	 * @param request   The HttpRequest instance.
	 * @param onSuccess The success callback handler.
	 * @param onError   The error callback handler.
	 * @param isAsync   Whether to execute the request asynchronously.
	 * @throws Exception If an error occurs during execution.
	 */
	public static void sendHttpRequest(HttpClient client, HttpRequest request, OnSuccess onSuccess, OnError onError,
			boolean isAsync) throws Exception {
		CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request,
				HttpResponse.BodyHandlers.ofString());

		if (isAsync == ASYNC) {
			futureResponse.thenAccept(response -> handleResponse(onSuccess, onError, response, null))
					.exceptionally(e -> {
						onError.accept(new HttpErrorException(e.getCause() != null ? e.getCause() : e));
						return null;
					});
		} else {
			try {
				HttpResponse<String> response = futureResponse.join();
				handleResponse(onSuccess, onError, response, null);
			} catch (Exception e) {
				Throwable cause = e.getCause() != null ? e.getCause() : e;
				onError.accept(new HttpErrorException(cause));
			}
		}
	}

	/**
	 * Overloaded POST request methods.
	 */
	public static HttpService post(String url) throws Exception {
		return post(createUrl(url), new FormDataBuilder(), new HttpHeaders());
	}

	public static HttpService post(String url, FormDataBuilder formData) throws Exception {
		return post(createUrl(url), formData, new HttpHeaders());
	}

	public static HttpService post(String url, FormDataBuilder formData, HttpHeaders headers) throws Exception {
		return post(createUrl(url), formData, headers);
	}

	public static HttpService post(URI url) throws Exception {
		return post(url, new FormDataBuilder(), new HttpHeaders());
	}

	public static HttpService post(URI url, FormDataBuilder formData) throws Exception {
		return post(url, formData, new HttpHeaders());
	}

	/**
	 * Creates a POST request with a given URL, form data, and headers.
	 */
	public static HttpService post(URI url, FormDataBuilder formData, HttpHeaders headers) throws Exception {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(url);
		for (HttpHeaders.Header h : HttpHeaders.merge(registeredHeaders, headers)) {
			requestBuilder.header(h.getName(), h.getValue());
		}
		requestBuilder.header("Content-Type", "multipart/form-data; boundary=" + FormDataBuilder.SEPARATOR);
		requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(formData.build().toByteArray()));
		return new HttpService(HttpClient.newHttpClient(), requestBuilder.build());
	}

	public static HttpService get(String url) throws Exception {
		return get(createUrl(url), new HttpHeaders());
	}

	public static HttpService get(String url, HttpHeaders headers) throws Exception {
		return get(createUrl(url), headers);
	}

	public static HttpService get(URI url) throws Exception {
		return get(url, new HttpHeaders());
	}

	/**
	 * Creates a GET request with a given URL and optional headers.
	 */
	public static HttpService get(URI url, HttpHeaders headers) throws Exception {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(url);
		for (HttpHeaders.Header h : HttpHeaders.merge(registeredHeaders, headers)) {
			requestBuilder.header(h.getName(), h.getValue());
		}
		requestBuilder.GET();
		return new HttpService(HttpClient.newHttpClient(), requestBuilder.build());
	}

	/**
	 * Handles the HTTP response, invoking the appropriate callback function.
	 *
	 * @param success   Success callback function.
	 * @param error     Error callback function.
	 * @param response  The HTTP response object.
	 * @param throwable The exception thrown (if any).
	 */
	private static void handleResponse(OnSuccess success, OnError error, HttpResponse<String> response,
			Throwable throwable) {
		try {
			if (throwable != null) {
				throw throwable;
			}
			success.accept(response);
		} catch (Throwable e) {
			error.accept(new HttpErrorException(e, response));
		}
	}

	/**
	 * Creates a URI using the base URL if set.
	 *
	 * @param url The endpoint path.
	 * @return The full URI.
	 */
	public static URI createUrl(String url) {
		String fullUrl = (baseUrl == null ? "" : baseUrl) + url;
		return URI.create(fullUrl);
	}

	/**
	 * Functional interface for handling successful responses.
	 */
	@FunctionalInterface
	public static interface OnSuccess {
		void accept(HttpResponse<String> response) throws Exception;
	}

	/**
	 * Functional interface for handling errors.
	 */
	@FunctionalInterface
	public static interface OnError {
		void accept(Exception e);
	}

	/**
	 * Custom exception class for HTTP errors.
	 */
	public static class HttpErrorException extends Exception {
		private static final long serialVersionUID = 1L;
		private final HttpResponse<String> response;

		public HttpErrorException(Throwable e, HttpResponse<String> response) {
			super(e);
			this.response = response;
		}

		public HttpErrorException(Throwable e) {
			super(e);
			this.response = null;
		}

		public HttpResponse<String> getResponse() {
			return response;
		}
	}
}
