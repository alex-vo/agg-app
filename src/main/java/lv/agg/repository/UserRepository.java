package lv.agg.repository;

import lv.agg.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("select u " +
            "from UserEntity u " +
            "left join fetch u.services " +
            "where u.email=:email")
    Optional<UserEntity> findUserWithServicesByEmail(@Param("email") String email);

    Optional<UserEntity> findByRefreshToken(String token);
}
