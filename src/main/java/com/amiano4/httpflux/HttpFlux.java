package com.amiano4.httpflux;

import java.net.http.HttpResponse;

public class HttpFlux {
	private final HttpResponse<String> response;
	private final HttpFluxErrorException exception;
	private HttpFluxResponseHandle successHandler;
	private HttpFluxErrorHandle errorHandler;

	public HttpFlux(HttpResponse<String> response, Exception exception) {
		this.response = response;

		// Only set exception if it's actually an error, don't wrap null
		if (exception instanceof HttpFluxErrorException) {
			this.exception = (HttpFluxErrorException) exception;
		} else if (exception != null || (response != null && response.statusCode() >= 400)) {
			this.exception = new HttpFluxErrorException("HTTP encountered an error: ", response);
		} else {
			this.exception = null;
		}
	}

	// Chainable onSuccess method
	public HttpFlux onSuccess(HttpFluxResponseHandle handler) {
		this.successHandler = handler;
		execute();
		return this;
	}

	// Chainable onError method
	public HttpFlux onError(HttpFluxErrorHandle handler) {
		this.errorHandler = handler;
		execute();
		return this;
	}

	// Execute success or error handler based on response status
	private void execute() {
		try {
			if (exception != null && errorHandler != null) {
				errorHandler.handle(exception);
				return;
			}

			if (response == null) {
				return; // No response and no exception, nothing to handle
			}

			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				if (successHandler != null) {
					successHandler.handle(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // Log unexpected handler errors
		}
	}

	// Functional interface for success handling
	@FunctionalInterface
	public interface HttpFluxResponseHandle {
		void handle(HttpResponse<String> response) throws Exception;
	}

	// Functional interface for error handling (now receives an Exception)
	@FunctionalInterface
	public interface HttpFluxErrorHandle {
		void handle(HttpFluxErrorException e) throws Exception;
	}

	// Exception class
	@SuppressWarnings("serial")
	public static class HttpFluxErrorException extends Exception {
		private final HttpResponse<String> response;

		public HttpFluxErrorException(Exception e, HttpResponse<String> response) {
			super(e);
			this.response = response;
		}

		public HttpFluxErrorException(String message, HttpResponse<String> response) {
			super(message);
			this.response = response;
		}

		public HttpResponse<String> getResponse() {
			return response;
		}
	}
}
