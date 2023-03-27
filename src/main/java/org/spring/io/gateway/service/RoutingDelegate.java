package org.spring.io.gateway.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RoutingDelegate {
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> redirect(HttpServletRequest request,
                                      String routeUrl, String prefix) {
        try {
            // build up the redirect URL
            String redirectUrl = createRedictUrl(request,routeUrl, prefix);
            RequestEntity<?> requestEntity = createRequestEntity(request, redirectUrl);
            log.info("requestEntity:"+ JSONObject.toJSONString(requestEntity));
            return route(requestEntity);
        } catch (Exception e) {
            log.error("request error:",e);
            return new ResponseEntity("redirect error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createRedictUrl(HttpServletRequest request, String routeUrl, String prefix) {
        String queryString = request.getQueryString();
        return routeUrl + request.getRequestURI().replace(prefix, "") +
                (queryString != null ? "?" + queryString : "");
    }


    private RequestEntity<?> createRequestEntity(HttpServletRequest request,
                                                 String url) throws URISyntaxException, IOException {
        String method = request.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);

        MultiValueMap<String, String> headers = parseRequestHeader(request);

        if(method.equalsIgnoreCase(HttpMethod.GET.name())) {
            return new RequestEntity<>(headers, httpMethod, new URI(url));
        }
        //处理其他带body的情况
        String body = parseRequestBody(request);
        if(StringUtils.isEmpty(body)){
            //add parameters
            MultiValueMap<String, Object> MultiValueBody = parseRequestParameters(request);

            return new RequestEntity<>(MultiValueBody, headers, httpMethod, new URI(url));

        }else {
            return new RequestEntity<>(body, headers,httpMethod, new URI(url));

        }

    }

    private ResponseEntity<?> route(RequestEntity requestEntity) {
        return restTemplate.exchange(requestEntity, String.class);
    }

    @Nullable
    private  String parseRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return  StreamUtils.copyToString(inputStream,Charset.forName("UTF-8"));
    }
    private  MultiValueMap parseRequestParameters(HttpServletRequest request) throws IOException {
        MultiValueMap<String, Object> map =null;
        List<String> parameterNames =Collections.list( request.getParameterNames());
        if(parameterNames!=null&&parameterNames.size()>0){
            map=new LinkedMultiValueMap<>();
            for (String parameterName : parameterNames) {

                map.add(parameterName,request.getParameter(parameterName));
            }
        }

        return  map;
    }

    private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            //将请求头accept-encoding去掉防止用户携带后服务器返回压缩的内容处理比较麻烦
            if ("accept-encoding".equals(headerName.toLowerCase())) {
                continue;
            }
            //将请求头content-length,长度需要重新计算
            if ("content-length".equals(headerName.toLowerCase())) {
                continue;
            }
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {

                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }
}