package org.spring.io.gateway.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CommonProperties {
    @Value("${demo.url:}")
    private String demoUrl;
}
