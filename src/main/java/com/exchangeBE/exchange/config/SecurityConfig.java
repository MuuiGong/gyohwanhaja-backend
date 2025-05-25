    package com.exchangeBE.exchange.config;

    import com.exchangeBE.exchange.security.CustomSuccessHandler;
    import com.exchangeBE.exchange.security.CustomUsernamePasswordAuthenticationFilter;
    import com.exchangeBE.exchange.security.JWTFilter;
    import com.exchangeBE.exchange.security.JWTUtil;
    import lombok.AllArgsConstructor;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.factory.PasswordEncoderFactories;
    import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.servlet.config.annotation.EnableWebMvc;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final AuthenticationConfiguration authenticationConfiguration;
        private final CustomSuccessHandler successHandler;
        private final JWTUtil jwtUtil;

        @Bean
        public AuthenticationManager authenticationManager() throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = authenticationManager();

            // ✅ 커스텀 필터에 SuccessHandler 주입
            CustomUsernamePasswordAuthenticationFilter customFilter =
                    new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
            customFilter.setAuthenticationSuccessHandler(successHandler);
            customFilter.setFilterProcessesUrl("/api/auth/login");

            http
                    .csrf(csrf -> csrf.disable())
                    .formLogin(form -> form.disable())
                    .httpBasic(basic -> basic.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/", "/login", "/join", "/reissue",
                                    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                    "/api/email/send", "/api/email/verify", "/api/register"
                            ).permitAll()
                            .requestMatchers("/user").hasRole("USER")
                            .requestMatchers("/admin").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAt(customFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }
    }