package jp.co.unixon.kousa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import tech.jhipster.config.JHipsterProperties;


@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration
    extends WebSecurityConfigurerAdapter {

    private final JHipsterProperties jHipsterProperties;
    private final CorsFilter corsFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
           .antMatchers(HttpMethod.OPTIONS, "/**")
           .antMatchers("/app/**/*.{js,html}")
           .antMatchers("/i18n/**")
           .antMatchers("/content/**")
           .antMatchers("/swagger-ui/**")
           .antMatchers("/test/**");
    }

    @Override
    public void configure(final HttpSecurity http)
        throws Exception {

        // @formatter:off
        http.csrf()
            .disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .and()
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy(jHipsterProperties.getSecurity().getContentSecurityPolicy())
            .and()
            .referrerPolicy(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .frameOptions()
            .deny()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/hello").permitAll();
    }
}
