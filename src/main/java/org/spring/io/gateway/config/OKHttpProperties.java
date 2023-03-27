package org.spring.io.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;
/**
 * @author ：LiuShihao
 * @date ：Created in 2020/12/18 9:38 上午
 * @desc ：okhttp相关配置信息
 */
@ConfigurationProperties(prefix = "okhttp.config")
@Data
public class OKHttpProperties {

    /**
     * Max Idle Connections
     */
    private Integer maxIdleConnections = 200;
    /**
     * 连接持续时间
     */
    private Long keepAliveDuration = 300L;
    /**
     * 复用时间单位
     */
    private TimeUnit keepAliveDurationTimeUnit = TimeUnit.MINUTES;
    /**
     * 连接超时时间
     */
    private Long connectTimeout = 300L;
    /**
     * 连接超时时间单位
     */
    private TimeUnit connectTimeoutTimeUnit = TimeUnit.SECONDS;
    /**
     * 写超时时间
     */
    private Long writeTimeout = 60L;
    /**
     * 写超时时间单位
     */
    private TimeUnit writeTimeoutTimeUnit = TimeUnit.SECONDS;
    /**
     * 读超时时间
     */
    private Long readTimeout = 60L;
    /**
     * 读超时时间单位
     */
    private TimeUnit readTimeoutTimeUnit = TimeUnit.SECONDS;
    /**
     * 线程配置
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;

}

