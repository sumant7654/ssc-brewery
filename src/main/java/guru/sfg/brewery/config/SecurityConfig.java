package guru.sfg.brewery.config;

import guru.sfg.brewery.security.JpaUserDetailsService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import guru.sfg.brewery.security.google.Google2FaFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Google2FaFilter google2FaFilter;

    // Need to use the Spring Data JPA SPel

    /*private JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    public void setJpaUserDetailsService(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }*/
    /*@Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }*/

    /*@Bean
    PasswordEncoder passwordEncoder(){
        return new LdapShaPasswordEncoder();
    }*/

    /*@Bean
    PasswordEncoder passwordEncoder(){
        return new StandardPasswordEncoder();
    }*/
    /*@Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }*/

    /*@Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter restHeaderAuthFilter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api"));
        restHeaderAuthFilter.setAuthenticationManager(authenticationManager);
        return restHeaderAuthFilter;

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterBefore(google2FaFilter, SessionManagementFilter.class);
        /*httpSecurity
                .addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();*/
        httpSecurity
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/", "/beers/**", "/h2-console/**").permitAll() // DO-NOT use h2-console in PRODUCTION
                            //.mvcMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            //.mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
                            .antMatchers("/brewery/breweries").hasAnyRole("ADMIN", "CUSTOMER")
                            .mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("ADMIN", "CUSTOMER");
                })
                .authorizeRequests()//.antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and().formLogin(loginConfigurer -> {
                    loginConfigurer
                            .loginProcessingUrl("/login")
                            .loginPage("/").permitAll()
                            .successForwardUrl("/")
                            .defaultSuccessUrl("/")
                            .failureUrl("/?error");
                })
                .logout(logoutConfigurer ->{
                    logoutConfigurer
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                            .logoutSuccessUrl("/?logout")
                            .permitAll();
                })
                .httpBasic().and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().rememberMe()
                //.key("remember-key")
                //.userDetailsService(userDetailsService);
                        .tokenRepository(persistentTokenRepository);

        // h2 console config
        httpSecurity.headers().frameOptions().sameOrigin();


    }


    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("sumantakumar")
                .password("{bcrypt}$2a$10$oh1nbuU/Voz5O50w5WhioOsa3sDkcTxZssXqQ9a9bPgOuJZkYN9j.")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}89f2679c6f6550aa82fc9802f55dbb40c0a61ee9c0eeb7ccaac1bb5d937ed8d0c0287d490f943fb9")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{bcrypt15}$2a$15$FenwQzQuhTm1y4P5qUcB3.1gHKcI5RhNKEQFSDVJJgBHqnXwoBye2")
                .roles("CUSTOMER");
    }*/

    /*@Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("sumantakumar")
                .password("sumantakumar")
                .roles("ADMIN")
                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();


        return new InMemoryUserDetailsManager(admin, user);
    }*/
}
