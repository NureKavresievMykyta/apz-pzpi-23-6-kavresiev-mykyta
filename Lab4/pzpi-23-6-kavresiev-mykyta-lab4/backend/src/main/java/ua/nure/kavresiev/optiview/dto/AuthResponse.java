package ua.nure.kavresiev.optiview.dto;

public class AuthResponse {

    private Long userId;
    private Long patientId;
    private String username;
    private String role;
    private String message;

    public AuthResponse(Long userId, Long patientId, String username, String role, String message) {
        this.userId = userId;
        this.patientId = patientId;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}