package lv.agg.repository;

import lv.agg.entity.AvailabilitySlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilitySlotEntity, Long> {

    @Query("select a " +
            "from AvailabilitySlotEntity a " +
            "join fetch a.user as u " +
            "join u.services as service " +
            "where service.id=:serviceId " +
            "  and a.dateFrom between :dateFrom and :dateTo " +
            "  and a.dateTo between :dateFrom and :dateTo")
    List<AvailabilitySlotEntity> findAvailabilitySlots(
            @Param("serviceId") Long serviceId,
            @Param("dateFrom") ZonedDateTime dateFrom,
            @Param("dateTo") ZonedDateTime dateTo
    );

}
