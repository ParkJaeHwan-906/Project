package hwannee.project.token.repository;

import hwannee.project.token.domain.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByIdx(Integer idx);
    Optional<RefreshToken> findByUserIdx(Integer useridx);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    @Query("SELECT r FROM RefreshToken r WHERE r.userIdx = :user_idx AND r.expired_at IS NULL")
    Optional<RefreshToken> findByUserIdxExpiredAtNULL(@Param("user_idx") Integer user_idx);

    @Query("SELECT r FROM RefreshToken r WHERE r.refreshToken = :refreshToken AND r.expired_at IS NULL")
    Optional<RefreshToken> findByRefreshTokenExpiredAtNULL(@Param("refreshToken") String refreshToken);

//    @Modifying
//    @Transactional
//    @Query("UPDATE RefreshToken r SET r.expired_at = CURRENT_TIMESTAMP WHERE r.userIdx = :user_idx")
//    void setExpiredAtRefreshTokenByUser_idx(@Param("user_idx") Integer user_idx);

}
