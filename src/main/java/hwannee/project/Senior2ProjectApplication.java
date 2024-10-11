package hwannee.project;

import hwannee.project.config.encrypt.EncryptProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // created_at, updated_at 자동 업데이트
public class Senior2ProjectApplication {
    public static void main(String[] args){
        SpringApplication.run(Senior2ProjectApplication.class, args);
    }
}
