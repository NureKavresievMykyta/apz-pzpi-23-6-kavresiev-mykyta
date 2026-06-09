package ua.nure.kavresiev.optiview.dto;

public class IotScanResponse {

    private String deviceName;
    private String deviceSerial;
    private String scanTime;
    private Long visitId;
    private Long patientId;
    private String patientFullName;
    private Double sphOd;
    private Double cylOd;
    private Integer axisOd;
    private Double sphOs;
    private Double cylOs;
    private Integer axisOs;
    private Integer iopOd;
    private Integer iopOs;
    private Integer pd;

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public String getScanTime() {
        return scanTime;
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

    public Double getSphOd() {
        return sphOd;
    }

    public Double getCylOd() {
        return cylOd;
    }

    public Integer getAxisOd() {
        return axisOd;
    }

    public Double getSphOs() {
        return sphOs;
    }

    public Double getCylOs() {
        return cylOs;
    }

    public Integer getAxisOs() {
        return axisOs;
    }

    public Integer getIopOd() {
        return iopOd;
    }

    public Integer getIopOs() {
        return iopOs;
    }

    public Integer getPd() {
        return pd;
    }
}