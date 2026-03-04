package com.wms.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "warehouse_owner_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true, foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_user_id"))
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "tax_id",unique = true)
    private String taxId;

    private String address;

    private String city;

    private String country;

    @OneToOne
    @JoinColumn(name = "approved_by",nullable = false,foreignKey = @ForeignKey(name = "fk_warehouse_owner_profiles_users_approved_by"))
    private User approvedBy;

    @JoinColumn(name = "approved_at")
    private LocalDateTime approvedAt;

    @JoinColumn(name = "rejection_reason")
    private String rejectionReason;

    @JoinColumn(name = "deleted_at")
    private LocalDateTime deletedAt;
}
