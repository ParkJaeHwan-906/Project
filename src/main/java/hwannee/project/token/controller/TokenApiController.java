package hwannee.project.token.controller;

import hwannee.project.token.dto.CreateAccessTokenRequest;
import hwannee.project.token.dto.CreateAccessTokenResponse;
import hwannee.project.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController // @Controller 에 ResponseBody 가 추가된 것 -> 주용도는 Json 형대로 객체 데이터를 반환
public class TokenApiController {
    private final TokenService tokenService;

    // 리프래쉬 토큰으로 새로운 액세스 토큰 발급
    @PostMapping("/api/public/token")
    @PreAuthorize("true")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
            (@RequestBody CreateAccessTokenRequest request){

        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
