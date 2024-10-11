package hwannee.project.config.status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 403 Forbidden (권한이 없는 경우)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return createResponseEntity("권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    // 401 Unauthorized (인증이 실패한 경우)
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException ex) {
        return createResponseEntity("인증이 실패했습니다.", HttpStatus.UNAUTHORIZED);
    }

    // 404 Not Found (존재하지 않는 경로로 접근할 때)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        return createResponseEntity("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }

    // 그 외 다른 예외 처리 (500 Internal Server Error 등)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return createResponseEntity("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<Map<String, Object>> createResponseEntity(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());
        return ResponseEntity.status(status).body(response);
    }
}
