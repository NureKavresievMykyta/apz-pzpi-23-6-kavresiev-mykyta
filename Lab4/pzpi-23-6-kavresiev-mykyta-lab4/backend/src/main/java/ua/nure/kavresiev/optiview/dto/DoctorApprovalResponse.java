package ua.nure.kavresiev.optiview.dto;

public class DoctorApprovalResponse {

    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private String cabinetNumber;
    private Boolean isActive;

    public DoctorApprovalResponse(Long userId, String username, String firstName, String lastName, String specialization, String cabinetNumber, Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.cabinetNumber = cabinetNumber;
        this.isActive = isActive;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}