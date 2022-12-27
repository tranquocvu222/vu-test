package com.nals.auction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile({JHipsterConstants.SPRING_PROFILE_PRODUCTION})
public class StaticResourcesWebConfiguration
    implements WebMvcConfigurer {

    protected static final String[] RESOURCE_LOCATIONS = new String[] {
        "classpath:/static/",
        "classpath:/static/content/",
        "classpath:/static/i18n/",
        };
    protected static final String[] RESOURCE_PATHS = new String[] {
        "/*.js",
        "/*.css",
        "/*.svg",
        "/*.png",
        "*.ico",
        "/content/**",
        "/i18n/*",
        };

    private final JHipsterProperties jhipsterProperties;

    public StaticResourcesWebConfiguration(final JHipsterProperties jHipsterProperties) {
        this.jhipsterProperties = jHipsterProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        ResourceHandlerRegistration resourceHandlerRegistration = appendResourceHandler(registry);
        initializeResourceHandler(resourceHandlerRegistration);
    }

    protected ResourceHandlerRegistration appendResourceHandler(final ResourceHandlerRegistry registry) {
        return registry.addResourceHandler(RESOURCE_PATHS);
    }

    protected void initializeResourceHandler(final ResourceHandlerRegistration resourceHandlerRegistration) {
        resourceHandlerRegistration.addResourceLocations(RESOURCE_LOCATIONS).setCacheControl(getCacheControl());
    }

    protected CacheControl getCacheControl() {
        return CacheControl.maxAge(getJHipsterHttpCacheProperty(), TimeUnit.DAYS).cachePublic();
    }

    private int getJHipsterHttpCacheProperty() {
        return jhipsterProperties.getHttp().getCache().getTimeToLiveInDays();
    }
}
