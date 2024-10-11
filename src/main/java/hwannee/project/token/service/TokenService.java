package hwannee.project.token.service;

import hwannee.project.User.domain.User;
import hwannee.project.User.repository.UserRepository;
import hwannee.project.User.service.UserService;
import hwannee.project.libs.Auth;
import hwannee.project.token.domain.RefreshToken;
import hwannee.project.token.dto.TokenRequest;
import hwannee.project.token.repository.RefreshTokenRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {

    private final Auth auth;
    private final RefershTokenService refershTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken){
        User user = null;
        try{
            refershTokenService.findByRefreshToken(refreshToken);
            TokenRequest request = auth.verifyToken(refreshToken);
            Integer userIdx = request.getIdx();
            user = userService.findByIdx(userIdx);

        } catch(IllegalArgumentException e){
            System.out.println(e);
            log.error("리프래쉬 토큰이 존재하지 않습니다.");
            throw new IllegalArgumentException("Invalid Token");
        }
        return auth.generateToken(user, Duration.ofHours(2));
    }
}
