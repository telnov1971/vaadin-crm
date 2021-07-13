package com.vaadin.tutorial.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity      // включает безопасность для приложения
@Configuration          // это класс настройки
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    // настройка безопасности навигации
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // сохранить запрос
                .requestCache().requestCache(new CustomRequestCache())
                // включить авторизацию
                .and().authorizeRequests()
                // внутренние запросы разрешены без проверок
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                // остальные запросы только с аутентификацией
                .anyRequest().authenticated()
                // форма входа по ссылке доступна всем
                .and().formLogin()
                .loginPage(LOGIN_URL).permitAll()
                // адрес обработки входа
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                // адрес при ошибке
                .failureUrl(LOGIN_FAILURE_URL)
                // переход после выхода
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    // определяет способ аутентификации сравнивая с заданным пользователем в памяти
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withUsername("user")
                        .password("{noop}password")
                        .roles("USER")
                        .build();
        return new InMemoryUserDetailsManager(user);
    }

    // исключение из проверок внутренних и статических ресурсов
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",
                "/icons/**",
                "/images/**",
                "/styles/**",
                "/h2-console/**");
    }
}
