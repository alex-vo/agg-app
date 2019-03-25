package lv.agg.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
public class AvailabilitySlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @Column(nullable = false)
    private ZonedDateTime dateFrom;
    @Column(nullable = false)
    private ZonedDateTime dateTo;
}
