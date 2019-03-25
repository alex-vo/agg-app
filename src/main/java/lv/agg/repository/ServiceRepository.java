package lv.agg.repository;

import lv.agg.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    @Query("select s " +
            "from ServiceEntity s " +
            "left join fetch s.users " +
            "where s.name=:name ")
    Optional<ServiceEntity> findASDdasdasdsad(@Param("name") String name);
}
