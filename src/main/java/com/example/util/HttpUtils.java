package com.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
@Service
public class HttpUtils {

    public <T> T safeRequest(String url, HttpMethod method, Map<String, String> headers, String body, TypeReference<T> typeReference) {
        try {
            return sendRequest(url, method, headers, body, typeReference);
        } catch (IOException | InterruptedException e) {
            log.error("Request failed. Method: {}, url: {}, message: {} ", method, url, e.getMessage());
        }
        return null;

    }

    private <T> T sendRequest(String url, HttpMethod method, Map<String, String> headers, String body, TypeReference<T> typeReference)
            throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url));
        if (headers != null) {
            headers.forEach(builder::header);
        }
        builder.method(method.name(), body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, typeReference);
    }

    private <T> T handleResponse(HttpResponse<String> response, TypeReference<T> typeReference) throws IOException {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode >= 400 && statusCode < 500) {
            throw new IOException("Client error " + statusCode + ": " + responseBody);
        } else if (statusCode >= 500) {
            throw new IOException("Server error " + statusCode + ": " + responseBody);
        }

        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            return JsonUtils.parse(responseBody, typeReference);
        } catch (Exception e) {
            if (statusCode == 201 || statusCode == 204) {
                return null;
            }
            throw new IOException("Failed to parse JSON", e);
        }
    }
}
