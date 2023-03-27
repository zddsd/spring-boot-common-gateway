package org.spring.io.gateway.controller;

import org.spring.io.gateway.properties.CommonProperties;
import org.spring.io.gateway.service.RoutingDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(DemoPrefixController.DELEGATE_PREFIX)
public class DemoPrefixController {

    @Autowired
    CommonProperties commonProperties;

    public final static String DELEGATE_PREFIX = "/demo-prefix/";

    @Autowired
    private RoutingDelegate routingDelegate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE})
    public ResponseEntity catchAll(HttpServletRequest request) {
        return routingDelegate.redirect(request, commonProperties.getDemoUrl(), DELEGATE_PREFIX);
    }
}