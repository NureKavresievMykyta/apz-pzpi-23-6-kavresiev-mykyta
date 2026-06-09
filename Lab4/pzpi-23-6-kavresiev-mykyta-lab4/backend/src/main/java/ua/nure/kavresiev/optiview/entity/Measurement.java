package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurements_id")
    private Long measurementsId;

    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @Column(name = "sph_od")
    private BigDecimal sphOd;

    @Column(name = "cyl_od")
    private BigDecimal cylOd;

    @Column(name = "axis_od")
    private Integer axisOd;

    @Column(name = "sph_os")
    private BigDecimal sphOs;

    @Column(name = "cyl_os")
    private BigDecimal cylOs;

    @Column(name = "axis_os")
    private Integer axisOs;

    @Column(name = "iop_od")
    private Integer iopOd;

    @Column(name = "iop_os")
    private Integer iopOs;

    @Column(name = "pd")
    private Integer pd;

    @Column(name = "device_serial")
    private String deviceSerial;
}