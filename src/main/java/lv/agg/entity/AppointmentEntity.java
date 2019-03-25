package lv.agg.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
public class AppointmentEntity {

    public enum Status {
        NEW, CONFIRMED, DECLINED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider")
    private UserEntity serviceProvider;
    @Column(nullable = false)
    private ZonedDateTime dateFrom;
    @Column(nullable = false)
    private ZonedDateTime dateTo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer")
    private UserEntity customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service")
    private ServiceEntity service;
    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    @PrePersist
    public void onPrePersist() {
        setDateCreated(ZonedDateTime.now());
    }
}
