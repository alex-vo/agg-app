package lv.agg.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
public class AppointmentEntity {

    public static enum Status {
        NEW, CONFIRMED, DECLINED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private UserEntity merchant;
    @Column(nullable = false)
    private ZonedDateTime dateFrom;
    @Column(nullable = false)
    private ZonedDateTime dateTo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
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
