package lv.agg.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
}
