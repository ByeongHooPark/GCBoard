package com.green.nowon.securityAndConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	UserDetailsService userDetails() {
		return new MyUserDetailsService();
	}
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
//		.csrf(csrf-> csrf.disable())
		.authorizeHttpRequests(request -> 
										request
										.requestMatchers("/css/**","/js/**","/image/**")
										.permitAll()
										.requestMatchers("/","/signup/**","/chess/**","/my-ws/**","/webjars/**")
										.permitAll()
										.requestMatchers("/argue/**","/board/**","/info/**")
										.hasRole("USER")
										.anyRequest()
										.authenticated()
										)
		.formLogin(form -> 
							form
							.loginPage("/signin")
							.loginProcessingUrl("/signin")
							.usernameParameter("userId")
							.passwordParameter("pass")
							.permitAll()
							)
		.logout(logout -> logout
							.logoutUrl("/signout")
							.logoutSuccessUrl("/")
							.deleteCookies("JSESSIONID")
							.permitAll()
							)
		;
		
		return http.build();
	}
}
