package org.spring.io.gateway.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @desc ：restTemplate配置
 */
@Configuration
@EnableConfigurationProperties(OKHttpProperties.class)
@Slf4j
public class RestTemplateConfig {

    @Autowired
    private OKHttpProperties okHttpProperties;

    /**
     * 声明 RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = httpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        log.info("基于okhttp的RestTemplate构建完成！");
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
                                             @Override
                                             public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                                                 // 只要重写此方法，不去抛出HttpClientErrorException异常即可
                                                 HttpStatus statusCode = clientHttpResponse.getStatusCode();
                                                 log.error("错误码::[{}]",statusCode);
                                                 //inputStream only read once
                                                /* log.error(StreamUtils.copyToString(clientHttpResponse.getBody(),
                                                         Charset.forName("UTF-8")));*/
                                             }
                                         });
        return restTemplate;
    }


    /**
     * 工厂
     * @return
     */
    private ClientHttpRequestFactory httpRequestFactory() {
        return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient());
    }

    /**
     * 客户端
     * @return
     */
    private OkHttpClient okHttpConfigClient() {
        return new OkHttpClient().newBuilder()
                .connectionPool(pool())
                .connectTimeout(okHttpProperties.getConnectTimeout(), okHttpProperties.getConnectTimeoutTimeUnit())
                .readTimeout(okHttpProperties.getReadTimeout(), okHttpProperties.getReadTimeoutTimeUnit())
                .writeTimeout(okHttpProperties.getWriteTimeout(), okHttpProperties.getWriteTimeoutTimeUnit())
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    /**
     * 连接池
     * @return
     */
    private ConnectionPool pool() {
        return new ConnectionPool(okHttpProperties.getMaxIdleConnections(), okHttpProperties.getKeepAliveDuration(), okHttpProperties.getKeepAliveDurationTimeUnit());
    }
}
