package com.projeto.gestock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.projeto.gestock.security.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            // Páginas públicas
            .requestMatchers("/login", "/usuarios/salvar", "/css/**", "/images/**").permitAll()

            // ✅ Permite que qualquer usuário autenticado (USER ou ADMIN) acesse a página de configuração
            .requestMatchers("/usuarios/config", "/usuarios/atualizarSenha").authenticated()

            // Somente ADMIN pode acessar rotas administrativas
            .requestMatchers("/usuarios/**").hasRole("ADMIN")

            // Todas as outras rotas precisam estar logadas
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .usernameParameter("email")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        )
        .exceptionHandling(ex -> ex
            .accessDeniedPage("/acessoNegado")
        )
        .csrf(csrf -> csrf.disable());

    return http.build();
}

}
