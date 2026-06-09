package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @Column(name = "doctor_user_id")
    private Long doctorUserId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "cabinet_number")
    private String cabinetNumber;
}