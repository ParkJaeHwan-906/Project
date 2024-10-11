package hwannee.project.User.service;

import hwannee.project.User.domain.User;
import hwannee.project.User.repository.UserRepository;
import hwannee.project.libs.Encrypt;
import hwannee.project.token.domain.RefreshToken;
import hwannee.project.token.repository.RefreshTokenRepository;
import hwannee.project.token.service.RefershTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SignService {

    private final Encrypt encrypt;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Boolean checkPassWord(User user, String passWord){
        String hashedPassword = user.getPassword();
        return encrypt.checkPassWord(passWord, hashedPassword);
    }

    public RefreshToken logOut(Integer userIdx){
        RefreshToken refreshToken = null;
        try {
            refreshToken = refreshTokenRepository.findByUserIdxExpiredAtNULL(userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

            refreshToken.setExpired_at(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            throw e;
        }
    }
}
