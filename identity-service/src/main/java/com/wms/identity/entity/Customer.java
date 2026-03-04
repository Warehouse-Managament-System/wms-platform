package com.wms.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(
    name = "customer_profiles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_profiles_tax_id", columnNames = "tax_id"),
        @UniqueConstraint(name = "uk_customer_profiles_user_id", columnNames = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_customer_profiles_users")
    )
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "tax_id", unique = true)
    private String taxId;

    @Column(name = "contact_person_name")
    private String contactPersonName;
}
