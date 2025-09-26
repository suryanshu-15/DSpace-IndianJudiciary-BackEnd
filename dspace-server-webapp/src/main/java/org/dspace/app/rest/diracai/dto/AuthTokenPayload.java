package org.dspace.app.rest.diracai.dto;

public class AuthTokenPayload {
    private String authorization;  // Bearer <token>
    private String csrfToken;      // Optional, if CSRF is enforced

    // Getters and setters
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }
}
