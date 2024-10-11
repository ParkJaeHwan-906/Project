package hwannee.project.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint  implements AuthenticationEntryPoint {  // 인증 실채 시 동작

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {  // 401

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "인증이 필요합니다.");
        res.put("Reason", "UNAUTHORIZED");
        res.put("path", request.getRequestURL());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), res);
    }
}

