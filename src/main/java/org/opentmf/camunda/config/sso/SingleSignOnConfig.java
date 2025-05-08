package org.opentmf.camunda.config.sso;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.net.URI;
import java.util.Collections;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class SingleSignOnConfig {

  private final KeycloakLogoutHandler keycloakLogoutHandler;

  public SingleSignOnConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
    this.keycloakLogoutHandler = keycloakLogoutHandler;
  }

  // 1. UI chain: only for /app/**
  @Bean
  @Order(1)
  public SecurityFilterChain camundaUiChain(HttpSecurity http, ClientRegistrationRepository clients) throws Exception {

    ClientRegistration reg = clients.findByRegistrationId("keycloak");
    String issuerUri = reg.getProviderDetails().getIssuerUri();
    URI parsed = URI.create(issuerUri);
    String keycloakHost = parsed.getScheme() + "://" + parsed.getAuthority();

    return http
        .headers(h -> h.contentSecurityPolicy(csp ->
            csp.policyDirectives("default-src 'self'; connect-src 'self' " + keycloakHost)
        ))
        .csrf(csrf -> csrf.ignoringRequestMatchers(
            new AntPathRequestMatcher("/api/admin/auth/user/**/logout")))
        .securityMatcher(new OrRequestMatcher(
            new AntPathRequestMatcher("/app/**"),
            new AntPathRequestMatcher("/api/**"),
            new AntPathRequestMatcher("/lib/**"),
            new AntPathRequestMatcher("/assets/**"),
            new AntPathRequestMatcher("/oauth2/**"),
            new AntPathRequestMatcher("/login/**"),
            new AntPathRequestMatcher("/logout")
            ))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .oauth2Login(withDefaults())
        .logout(logout -> logout
            .logoutRequestMatcher(new OrRequestMatcher(
                antMatcher("/logout"),
                antMatcher("/api/admin/auth/user/**/logout")))
            .logoutSuccessHandler(keycloakLogoutHandler)
        )
        .build();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Bean
  public FilterRegistrationBean containerBasedAuthenticationFilter(){
    FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
    filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
    filterRegistration.setInitParameters(
        Collections.singletonMap(
            "authentication-provider",
            "org.opentmf.camunda.config.sso.KeycloakAuthenticationProvider"));
    filterRegistration.setOrder(201); // make sure the filter is registered after the Spring Security Filter Chain
    filterRegistration.addUrlPatterns("/app/*");
    return filterRegistration;
  }

  // The ForwardedHeaderFilter is required to correctly assemble the redirect URL for OAUth2 login.
  // Without the filter, Spring generates an HTTP URL even though the container route is accessed through HTTPS.
  @Bean
  public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
    FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
    filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filterRegistrationBean;
  }

  @Bean
  @Order(0)
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  // Modify firewall in order to allow request details for child groups
  @Bean
  public HttpFirewall getHttpFirewall() {
    StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
    strictHttpFirewall.setAllowUrlEncodedPercent(true);
    strictHttpFirewall.setAllowUrlEncodedSlash(true);
    return strictHttpFirewall;
  }
}
