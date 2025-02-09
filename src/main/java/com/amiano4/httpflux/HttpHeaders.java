package com.amiano4.httpflux;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class HttpHeaders extends ArrayList<HttpHeaders.Header> {
	public HttpHeaders() {
		super();
	}

	public HttpHeaders add(String name, String value) {
		this.add(new Header(name, value));
		return this;
	}

	// Updates an existing header by name or adds a new one if not found
	public HttpHeaders update(String name, String value) {
		for (Header header : this) {
			if (header.getName().equalsIgnoreCase(name)) {
				header.setValue(value); // Update existing value
				return this;
			}
		}

		this.add(name, value);
		return this;
	}

	public static HttpHeaders merge(HttpHeaders... headersList) {
		HttpHeaders merged = new HttpHeaders();

		for (HttpHeaders headers : headersList) {
			for (HttpHeaders.Header header : headers) {
				merged.update(header.getName(), header.getValue());
			}
		}

		return merged;
	}

	public void clear() {
		super.clear();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Header header : this) {
			builder.append(header.toString()).append("\n");
		}
		return builder.toString();
	}

	// Header class to represent a single header
	public static class Header {
		private String name;
		private String value;

		public Header(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return name + ": " + value;
		}
	}
}
