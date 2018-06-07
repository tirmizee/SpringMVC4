package com.tirmizee.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tirmizee.backend.dao.MemberDao;
import com.tirmizee.backend.dao.PermissionDao;
import com.tirmizee.core.security.AuthenticationFailureHandlerImpl;
import com.tirmizee.core.security.AuthenticationSuccessHandlerImpl;
import com.tirmizee.core.security.DaoAuthenticationProviderImpl;
import com.tirmizee.core.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired 
	private PermissionDao permissionDao;
	
	@Autowired
	private AuthenticationSuccessHandlerImpl successHandler;
	
	@Autowired
	private AuthenticationFailureHandlerImpl failureHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(11);
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProviderImpl();
	    authProvider.setHideUserNotFoundExceptions(false);
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl(memberDao, permissionDao);
	}
	
	@Bean
	public SessionRegistry sessionRegistry(){
		return new SessionRegistryImpl();
	} 
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.authenticationProvider(authenticationProvider());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.GET, "/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
				.antMatchers( "/login",
							  "/ForgetPassword",
							  "/api/password/forget",
							  "/api/password/reset",
							  "/api/password/forceChange",
							  "/ResetPassword/**").permitAll()
				.anyRequest().authenticated()
			.and()
		    .formLogin().loginPage("/login").permitAll()
		    	.successHandler(successHandler)
		    	.failureHandler(failureHandler)
		    .and()
		    .sessionManagement()
			    .maximumSessions(1)
			    .maxSessionsPreventsLogin(true)
			    .sessionRegistry(sessionRegistry());
	}

}
