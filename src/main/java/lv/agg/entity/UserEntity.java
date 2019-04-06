package lv.agg.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class UserEntity {

    public enum UserRole {
        ROLE_CUSTOMER, ROLE_MERCHANT, ROLE_ADMIN
    }

    public enum MerchantType {
        INDIVIDUAL, COMPANY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @ManyToMany
    @JoinTable(
            name = "user_service",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id"))
    private List<ServiceEntity> services;
    @Column
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private MerchantType merchantType;
    @Column
    private String companyName;
    @Column
    private String address;
    @Column
    private String firstName;
    @Column
    private String lastName;
}
