package ca.sheridancollege.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
//tells spring this is where security info lies
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoggingAccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() {
		
		JdbcUserDetailsManager jdbcUserDetailsManager =
				new JdbcUserDetailsManager(dataSource);
		
		return jdbcUserDetailsManager;
	}
	
		
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		http.authorizeRequests()
			.antMatchers("/user/**").hasAnyRole("USER", "MANAGER")//to specify the resources and which roles are required for access.
			.antMatchers("/admin/**").hasRole("MANAGER")
			//allows access to h2-console
			.antMatchers("/h2-console/**").permitAll()
			.antMatchers("/", "/**").permitAll()
			.and()
			.formLogin().loginPage("/login")
			.defaultSuccessUrl("/")
			.and() //allows us to chain configuration calls
			.logout().invalidateHttpSession(true)
			.clearAuthentication(true)
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler);
			
		http.csrf().disable();
		http.headers().frameOptions().disable();		
	}
	
	@Override
	protected void configure (AuthenticationManagerBuilder auth) throws Exception {
		
		auth.jdbcAuthentication().dataSource(dataSource).withDefaultSchema().withUser("harry")
		.password(passwordEncoder.encode("potter")).roles("USER")
		.and()
		.withUser("albus").password(passwordEncoder.encode("hogwarts")).roles("USER", "MANAGER");
		
	}
}
