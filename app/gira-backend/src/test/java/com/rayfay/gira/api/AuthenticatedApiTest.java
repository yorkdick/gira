package com.rayfay.gira.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AuthenticatedApiTest extends BaseApiTest {
    protected HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    protected HttpEntity<?> createAuthEntity() {
        return new HttpEntity<>(getAuthHeaders());
    }

    protected <T> HttpEntity<T> createAuthEntity(T body) {
        return new HttpEntity<>(body, getAuthHeaders());
    }

    protected HttpEntity<?> createJsonAuthEntity() {
        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    protected <T> HttpEntity<T> createJsonAuthEntity(T body) {
        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}