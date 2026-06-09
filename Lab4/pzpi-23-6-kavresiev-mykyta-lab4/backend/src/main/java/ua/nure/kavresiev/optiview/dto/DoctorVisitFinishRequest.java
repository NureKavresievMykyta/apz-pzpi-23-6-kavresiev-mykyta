package ua.nure.kavresiev.optiview.dto;

public class DoctorVisitFinishRequest {

    private String complaints;
    private String anamnesis;
    private String diagnosis;
    private String treatment;
    private String visualAcuity;

    public String getComplaints() {
        return complaints;
    }

    public void setComplaints(String complaints) {
        this.complaints = complaints;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getVisualAcuity() {
        return visualAcuity;
    }

    public void setVisualAcuity(String visualAcuity) {
        this.visualAcuity = visualAcuity;
    }
}