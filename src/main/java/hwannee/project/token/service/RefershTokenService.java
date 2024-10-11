package hwannee.project.token.service;

import hwannee.project.token.domain.RefreshToken;
import hwannee.project.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RefershTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public Boolean save(RefreshToken refreshToken){
        try{
            refreshTokenRepository.save(new RefreshToken(
                    refreshToken.getUserIdx(),
                    refreshToken.getName(),
                    refreshToken.getId(),
                    refreshToken.getRefreshToken()
            ));
            return true;
        } catch(Exception e){
            return false;
        }
    }
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshTokenExpiredAtNULL(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("잘못된 토큰입니다."));
    }

}
