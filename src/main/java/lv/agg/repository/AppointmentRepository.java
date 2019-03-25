package lv.agg.repository;

import lv.agg.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("select a " +
            "from AppointmentEntity a " +
            "where a.id=:appointmentId and a.serviceProvider.id=:serviceProviderId and a.status=:status")
    Optional<AppointmentEntity> findNewAppointment(
            @Param("appointmentId") Long appointmentId,
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("status") AppointmentEntity.Status status
    );

    @Query("select a " +
            "from AppointmentEntity a " +
            "where a.serviceProvider.id=:serviceProviderId " +
            "and a.dateFrom >= :dateFrom " +
            "and a.dateTo <= :dateTo")
    List<AppointmentEntity> findServiceProviderAppointments(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("dateFrom") ZonedDateTime dateFrom,
            @Param("dateTo") ZonedDateTime dateTo
    );

    @Query("select a " +
            "from AppointmentEntity a " +
            "where a.serviceProvider.id=:serviceProviderId " +
            "and ((a.dateFrom<=:dateFrom and a.dateTo>=:dateFrom) " +
            "or (a.dateFrom<=:dateTo and a.dateTo>=:dateTo))")
    List<AppointmentEntity> findClashingAppointments(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("dateFrom") ZonedDateTime dateFrom,
            @Param("dateTo") ZonedDateTime dateTo
    );

    @Query("select a " +
            "from AppointmentEntity a " +
            "where a.customer.id=:customerId " +
            "and a.dateFrom >= :dateFrom " +
            "and a.dateTo <= :dateTo")
    List<AppointmentEntity> findCustomerAppointments(
            @Param("customerId") Long customerId,
            @Param("dateFrom") ZonedDateTime dateFrom,
            @Param("dateTo") ZonedDateTime dateTo
    );
}
