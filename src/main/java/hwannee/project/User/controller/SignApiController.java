package hwannee.project.User.controller;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.SignRequest;
import hwannee.project.User.service.SignService;
import hwannee.project.User.service.UserService;
import hwannee.project.libs.Auth;
import hwannee.project.token.domain.RefreshToken;
import hwannee.project.token.dto.TokenRequest;
import hwannee.project.token.repository.RefreshTokenRepository;
import hwannee.project.token.service.RefershTokenService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SignApiController {
    private final UserService userService;
    private final SignService signService;
    private final RefershTokenService refershTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Auth auth;

    @PostMapping("/api/public/sign")
    @PreAuthorize("true")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignRequest signRequest){  // 로그인 성공 시 토큰 반환
//    public ResponseEntity<SignResponse> signIn(@RequestBody SignRequest signRequest){
        String id = signRequest.getId();
        String passWord = signRequest.getPassWord();

        // Json 으로 HTTP 응답하기
        // 1. DTO (객체) 이용하여 반환하기
//        SignResponse response = new SignResponse();
        // 2. HashMap 사용하여 반환하기
        Map<String, Object> response = new HashMap<>();

        User user = null;

        // User 정보가 없을 때 IllegalArgumentException 이 발생
        // try catch 문으로 예외처리
        try{
            user = userService.findById(id);
        } catch (IllegalArgumentException e){
//            response.setMessage("아이디가 존재하지 않습니다.");
            response.put("message", "아이디가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(!signService.checkPassWord(user, passWord)){ // 비밀번호가 일치하지 않음
//            response.setMessage("비밀번호가 일치하지 않습니다.");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 유저가 ban 상태인 경우
        if(user.getBan() == 1){
            response.put("message", "일시적으로 사용이 정지된 계정입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // to do : 토큰 발행 로직 ✅
        response.put("message", "ok");
        String token = auth.generateToken(user, Duration.ofHours(2));   // 두시간 동안 유지되는 토큰 발행
        String refrshToken = auth.generateToken(user, Duration.ofHours(14));    // 14시간 동안 유지되는 토큰 발행

        // ⚠ 로그인 시에 기존에 DB 에 저장되어 있는 RefreshToken 삭제 작업 필요
//        refreshTokenRepository.setExpiredAtRefreshTokenByUser_idx(user.getIdx());
        try{
            signService.logOut(user.getIdx());
        }catch(IllegalArgumentException e){
//            System.out.println("해당하는 리프래쉬 토큰이 존재하지 않습니다.");
            log.info("해당하는 리프래쉬 토큰이 존재하지 않습니다.");
        }
        // 리프레쉬 토큰 저장
        refershTokenService.save(new RefreshToken(
                user.getIdx(),
                user.getName(),
                user.getId(),
                refrshToken));

        response.put("token", token);
        response.put("refreshToken", refrshToken);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/api/logout")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Map<String, Object>> logOut(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenRequest request = (TokenRequest) authentication.getPrincipal();

        RefreshToken result = signService.logOut(request.getIdx());

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("message", "로그아웃 하였습니다.");
        return  ResponseEntity.ok().body(response);
    }
}
