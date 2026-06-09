package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @Column(name = "admin_user_id")
    private Long adminUserId; // Це ID співпадає з user_id з таблиці users

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "shift_type")
    private String shiftType; // 'MORNING', 'EVENING'
}