package hwannee.project.libs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataBase {
    private final JdbcTemplate jdbcTemplate;

    // select
    private List<?> select(String query, List<Object> params) {
        if (params == null || params.isEmpty()) {
            return jdbcTemplate.queryForList(query); // 파라미터가 없으면 빈 배열이 아닌 이 메서드 사용
        }
        return jdbcTemplate.queryForList(query, params.toArray());
    }

    // update, insert, delete
    private int update(String query, List<Object> params) {
        if (params == null || params.isEmpty()) {
            return jdbcTemplate.update(query); // 파라미터가 없으면 빈 배열이 아닌 이 메서드 사용
        }
        // 영향 미친 행의 개수가 출력
        return jdbcTemplate.update(query, params.toArray());
    }

    public Map<String, Object> query(String query, List<Object> params){
        Map<String, Object> response = new HashMap<>();

        log.info(query);

        if (!patternCheck(query)) {
            response.put("result", select(query, params));
        } else {
            response.put("result", update(query, params));
        }
        return response;
    }
    private boolean patternCheck(String query){
        String pattern = "^(UPDATE|INSERT|DELETE).*";
        return Pattern.matches(pattern, query);
    }
}