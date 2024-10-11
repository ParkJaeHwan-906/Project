package hwannee.project.config.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("security")
public class EncryptProperties {

    private String secretKey;
    private String salt;
    private String localUrl;
    private String serverUrl;
}

