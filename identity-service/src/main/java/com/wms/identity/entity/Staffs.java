package com.wms.identity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_profiles")
@Data
public class Staffs {
    @Id
    @Column(updatable = false,nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "warehouse_id",nullable = false)
    private Warehouse warehouse;

    @Column(name = "position", nullable = false, length = 100)
    private String position;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
