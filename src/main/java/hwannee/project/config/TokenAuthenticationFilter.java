package hwannee.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwannee.project.config.jwt.JwtProperties;
import hwannee.project.libs.Auth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
Filter : 각종 요청을 처리하기 위한 로직으로 전달되기 전후에 URL 패턴에 맞는 모든 요청을 처리하는 기능
 */
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    /*
    요청이 오면 헤더값을 비교해 토큰이 있는지 확인 후, 유효 토큰이라면 📌시큐라티 컨텍스트 홀더 에 인증 정보를 저장한다.

    📌 시큐리티 컨텍스트 홀더
    : 인증 객체가 저장되는 보관소 ( 인증 객체 필요시 언제든 인증 객체를 꺼내 사용 가능 )
     */
    private final JwtProperties jwtProperties;
    private final Auth auth;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private static String TOKEN_PREFIX;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청 경로 가져오기
        String requestUrl = request.getRequestURI();
        // 요청 메서드 가져오기
        String method = request.getMethod();
//        System.out.println("요청 URL : "+requestUrl);
        // 특정 경로는 필터 적용하지 않도록 처리
        // ⚠️ h2 콘솔 조건 추가
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 특정 경로는 필터 적용하지 않도록 처리
        if (pathMatcher.match("/h2-console/**", requestUrl)
                || pathMatcher.match("/favicon.ico", requestUrl)
                || pathMatcher.match("/api/public/**", requestUrl)) {
            filterChain.doFilter(request,response); // 필터를 생략하고 다음 필터로 넘어감
            return;
        }

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거 ( Hwannee )
        String token = getAccessToken(authorizationHeader);
        // 가져온 토큰이 유효한지 확인 후, 유효한 때는 인증 정보 설정
        try{
            // 토큰이 유효하면 Authentication 객체 생성 후 SecurityContext에 저장
            Authentication authentication = auth.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException e){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            Map<String, Object> res = new HashMap<>();
            res.put("message", "잘못된 요청입니다.");
            res.put("Reason", "Bad Request");
            res.put("path", request.getRequestURL());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), res);
        }

        // 다음 필터로 넘어감
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader){
        TOKEN_PREFIX = jwtProperties.getPrefix();

        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
