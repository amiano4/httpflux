package com.amiano4.httpflux;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpService {
	private static String baseUrl = null;
	public static HttpHeaders registeredHeaders = new HttpHeaders();

	public static void setBaseUrl(String url) {
		baseUrl = url;
	}

	public static String getBaseUrl() {
		return baseUrl;
	}

	public static URI createUrl(String url) {
		String fullUrl = (baseUrl == null ? "" : baseUrl) + url;
		return URI.create(fullUrl);
	}

	public static HttpFlux post(String url) {
		return post(url, new FormDataBuilder(), new HttpHeaders());
	}

	public static HttpFlux post(String url, FormDataBuilder formData) {
		return post(url, formData, new HttpHeaders());
	}

	public static HttpFlux post(String url, FormDataBuilder formData, HttpHeaders headers) {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(createUrl(url));

		// Include headers
		for (HttpHeaders.Header h : HttpHeaders.merge(registeredHeaders, headers)) {
			requestBuilder.header(h.getName(), h.getValue());
		}

		requestBuilder.header("Content-Type", "multipart/form-data; boundary=" + FormDataBuilder.SEPARATOR);

		try {
			requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(formData.build().toByteArray()));
		} catch (Exception e) {
			return new HttpFlux(null, e); // Return early with error
		}

		return sendHttpRequest(HttpClient.newHttpClient(), requestBuilder.build());
	}

	public static HttpFlux get(String url) {
		return get(url, new HttpHeaders());
	}

	public static HttpFlux get(String url, HttpHeaders headers) {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(createUrl(url));

		// Include headers
		for (HttpHeaders.Header h : HttpHeaders.merge(registeredHeaders, headers)) {
			requestBuilder.header(h.getName(), h.getValue());
		}

		requestBuilder.GET();

		return sendHttpRequest(HttpClient.newHttpClient(), requestBuilder.build());
	}

	public static HttpFlux sendHttpRequest(HttpClient client, HttpRequest request) {
		HttpResponse<String> response = null;
		Exception ex = null;

		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 400) {
				ex = new HttpFlux.HttpFluxErrorException(response.body(), response);
			}
		} catch (IOException e) {
			ex = new RuntimeException("Network error: " + e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			ex = new RuntimeException("Request was interrupted", e);
		}

		return new HttpFlux(response, ex);
	}

}
