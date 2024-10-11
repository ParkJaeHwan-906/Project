package hwannee.project.config;

import hwannee.project.config.encrypt.EncryptProperties;
import hwannee.project.config.handler.CustomAccessDeniedHandler;
import hwannee.project.config.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration  // Spring 의 설정 클래스
@EnableWebSecurity  // Spring Security 를 활성화하는 에너테이션
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final EncryptProperties encryptProperties;
    /*
    HTTP 요청에 대한 보안 설정을 관리
    @param : http HttpSecurity
    @return : SecurityFilterChain
    @throws : Exception
     */
    // WebSecurityCustomizer: 특정 경로를 보안 필터에서 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
//                .requestMatchers(PathRequest.toH2Console())  // H2 콘솔 요청 무시
                .requestMatchers("/static/**")
                .requestMatchers("/favicon.ico")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());  // H2 및 정적 리소스 무시
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(encryptProperties.getLocalUrl())); // 허용할 출처
                    config.setAllowedOrigins(List.of(encryptProperties.getServerUrl())); // 허용할 출처
                    config.setAllowedOrigins(List.of("http://127.0.0.1:5500")); // 허용할 출처
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTION")); // 허용할 HTTP 메서드
                    config.setAllowedHeaders(List.of("*")); // 허용할 헤더
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/favicon.ico").permitAll()
                        // H2 콘솔 관련 모든 경로에 대한 접근을 허용
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/api/public/**").permitAll()  // 인증 없이 접근 허용
                        .requestMatchers("/static/**").permitAll()  // 정적 리소스 허용
                        .anyRequest().authenticated())  // 그 외 모든 요청은 인증 필요
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}
