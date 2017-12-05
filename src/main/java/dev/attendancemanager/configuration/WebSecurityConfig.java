package dev.attendancemanager.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import dev.attendancemanager.security.JWTAuthenticationFilter;
import dev.attendancemanager.security.JWTLoginFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired DataSource dataSource;
	
	@Autowired PasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests()
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers(HttpMethod.POST, "/auth").permitAll()
			
			.and()
			// We filter the api/login requests
			.addFilterBefore(new JWTLoginFilter("/auth", authenticationManager()),
					UsernamePasswordAuthenticationFilter.class);
			/*.anyRequest().authenticated()
			.and()
			// We filter the api/login requests
			.addFilterBefore(new JWTLoginFilter("/auth", authenticationManager()),
					UsernamePasswordAuthenticationFilter.class)
			// And filter other requests to check the presence of JWT in header
			.addFilterBefore(new JWTAuthenticationFilter(),
					UsernamePasswordAuthenticationFilter.class);*/
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.passwordEncoder(passwordEncoder)
			.usersByUsernameQuery("select MATRICULE, PASSWORD, ACTIF from USER where MATRICULE=?")
			.authoritiesByUsernameQuery("select MATRICULE, ROLE from USER where MATRICULE=?");
	}
	
	/*
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
	    	CorsConfiguration configuration = new CorsConfiguration();
	    	configuration.setAllowedOrigins(Arrays.asList("*"));
	    	configuration.setAllowedMethods(Arrays.asList("POST", "GET",  "PUT", "DELETE", "OPTIONS"));
	    	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    	source.registerCorsConfiguration("/**", configuration);
	    	return source;
    }
    */
}
