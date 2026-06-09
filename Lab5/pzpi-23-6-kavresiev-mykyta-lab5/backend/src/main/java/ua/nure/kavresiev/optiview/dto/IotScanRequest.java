package ua.nure.kavresiev.optiview.dto;

public class IotScanRequest {

    private Long visitId;
    private Long patientId;
    private String patientFullName;

    public IotScanRequest(Long visitId, Long patientId, String patientFullName) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.patientFullName = patientFullName;
    }

    public Long getVisitId() {
        return visitId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientFullName() {
        return patientFullName;
    }
}