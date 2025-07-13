package com.voxcargo.shipment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
  }
}

//@Bean
//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//        .cors()
//        .and()
//        .csrf().disable()
//        .authorizeHttpRequests(authz -> authz
//            .anyRequest().permitAll()
//        );
//
//    return http.build();
//}