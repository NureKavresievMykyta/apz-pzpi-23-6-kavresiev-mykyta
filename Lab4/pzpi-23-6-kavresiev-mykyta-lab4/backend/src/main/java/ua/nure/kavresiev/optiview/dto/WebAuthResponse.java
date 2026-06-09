package ua.nure.kavresiev.optiview.dto;

public class WebAuthResponse {

    private Long userId;
    private String username;
    private String role;
    private String displayName;
    private String message;

    public WebAuthResponse(Long userId, String username, String role, String displayName, String message) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.displayName = displayName;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }
}