package br.com.jorgefigueredoo.agendamento_barbearia_api.config;

import br.com.jorgefigueredoo.agendamento_barbearia_api.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ importante quando tem CORS + Security
            .cors(Customizer.withDefaults())

            // ✅ SEM CSRF para API JWT
            .csrf(csrf -> csrf.disable())

            // ✅ Sem sessão (JWT stateless)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ remove login padrão do Spring (popups)
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())

            .authorizeHttpRequests(auth -> auth
                // libera preflight
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ login público
                .requestMatchers("/auth/**").permitAll()

                // público do cliente
                .requestMatchers(
                    "/services", "/barbers", "/availability",
                    "/agendamentos/**"
                ).permitAll()

                // swagger público
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                ).permitAll()

                // área do barbeiro/admin
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "BARBEIRO")

                .anyRequest().authenticated()
            )

            // ✅ filtro JWT
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
