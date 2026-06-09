package ua.nure.kavresiev.optiview.dto;

public class DoctorVisitResponse {

    private Long visitId;
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientBirthDate;
    private String patientPhone;
    private String patientEmail;
    private String patientAddress;
    private String startTime;
    private String endTime;
    private String visitStatus;
    private String visitType;

    public DoctorVisitResponse(Long visitId, Long patientId, String patientFirstName, String patientLastName, String patientBirthDate, String patientPhone, String patientEmail, String patientAddress, String startTime, String endTime, String visitStatus, String visitType) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientBirthDate = patientBirthDate;
        this.patientPhone = patientPhone;
        this.patientEmail = patientEmail;
        this.patientAddress = patientAddress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.visitStatus = visitStatus;
        this.visitType = visitType;
    }

    public Long getVisitId() {
        return visitId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public String getPatientBirthDate() {
        return patientBirthDate;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getVisitStatus() {
        return visitStatus;
    }

    public String getVisitType() {
        return visitType;
    }
}