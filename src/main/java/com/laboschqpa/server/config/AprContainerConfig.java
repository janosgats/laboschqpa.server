package com.laboschqpa.server.config;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.AprLifecycleListener;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class AprContainerConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        LifecycleListener arpLifecycle = new AprLifecycleListener();
        factory.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
        factory.addContextLifecycleListeners(arpLifecycle);
    }
}