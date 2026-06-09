package ua.nure.kavresiev.optiview.dto;

import lombok.Data;

@Data
public class VisitDTO {
    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String treatment;
    private String visualAcuity;
}