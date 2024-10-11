package hwannee.project.User.controller;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.AddUserRequest;
import hwannee.project.User.dto.ResponseUser;
import hwannee.project.User.dto.UserInfo;
import hwannee.project.User.service.UserService;
import hwannee.project.libs.Auth;
import hwannee.project.token.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final Auth auth;

    // 회원가입 API
    @PostMapping("/api/public/user")
    @PreAuthorize("true")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody AddUserRequest request){
        Map<String, Object> response = new HashMap<>();
        try {
            String id = userService.save(request);
            User user = userService.findById(id);

            response.put("user", ResponseUser.builder()
                    .name(user.getName())
                    .id(user.getId())
                    .passWord(user.getPassword())
                    .role(user.getRole())
                    .build());
            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception e){
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 토큰으로 정보 조회
    // 회원 정보 수정 (메인 : 회원 정보 보여주기, [이름, 아이디])
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/api/user")
    public ResponseEntity<TokenRequest> tokenCheck(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal(); // 전체 정보가 들어있는 TokenRequest


        return ResponseEntity.ok().body(TokenRequest.builder()
                .idx(tokenRequest.getIdx())
                .id(tokenRequest.getId())
                .name(tokenRequest.getName())
                .role(tokenRequest.getRole())
                .build());
    }

    // 비밀번호 변경
    @PutMapping("/api/user/modifyPassword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Map<String, Object>> modifyPassWord(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try {
            String modifyPw = (String) request.get("modifyPw");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();
            
            User user = userService.modifyPassword(tokenRequest.getIdx(), modifyPw);
            response.put("user", user);
            response.put("message", "변경되었습니다.");
            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            // 오류 발생 시 400 에러 발생
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    // 주소 변경
    @PutMapping("/api/user/modifyAddress")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Map<String, Object>> modifyAddress(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try {
            String modifyAddress = (String) request.get("modifyAddress");

            String modifyDetail = (String) request.get("modifyDetail");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();

            User user = userService.modifyAddress(tokenRequest.getIdx(), modifyAddress, modifyDetail);

            response.put("user", user);
            response.put("message", "변경되었습니다.");

            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            // 오류 발생 시 400 에러 발생
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 전화번호 변경
    @PutMapping("/api/user/modifyTel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Map<String, Object>> modifyTel(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try {
            String modifyTel = (String) request.get("modifyTel");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();

            User user = userService.modifyTel(tokenRequest.getIdx(), modifyTel);

            response.put("user", user);
            response.put("message", "변경되었습니다.");

            return ResponseEntity.ok().body(response);
        } catch(Exception e){
            // 오류 발생 시 400 에러 발생
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}


/*
<표현식 사용 가능>
@PreAuthorize : 메서드가 실행되기 전에 인증을 거친다.
@PostAuthorize : 메서드가 실행되고 나서 응답을 보내기 전 인증을 거친다.

[사용 가능 표현식]
- hasRole([role]) : 현재 사용자의 권한이 파라미터의 권한과 동일한 경우 true
- hasAnyRole([role1,role2]) : 현재 사용자의 권한디 파라미터의 권한 중 일치하는 것이 있는 경우 true
- principal : 사용자를 증명하는 주요객체(User)를 직접 접근할 수 있다.
- authentication : SecurityContext에 있는 authentication 객체에 접근 할 수 있다.
- permitAll : 모든 접근 허용
- denyAll : 모든 접근 비허용
- isAnonymous() : 현재 사용자가 익명(비로그인)인 상태인 경우 true
- isRememberMe() : 현재 사용자가 RememberMe 사용자라면 true
- isAuthenticated() : 현재 사용자가 익명이 아니라면 (로그인 상태라면) true
- isFullyAuthenticated() : 현재 사용자가 익명이거나 RememberMe 사용자가 아니라면 true

<표현식 사용 불가능>
@Secured
 */