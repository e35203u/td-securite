package com.example.banque.config;
import com.example.banque.utilisateur.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig{
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UtilisateurRepository utilisateurs){
        return login -> utilisateurs.findByLogin(login)
                .map(u -> User.withUsername(u.getLogin())
                        .password(u.getMotDePasse())
                        .roles(u.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(login));
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            );
        return http.build();
    }
}

/*Réponse aux questions:
Question 1 : Relancez : que renvoie maintenant l'API ?
Toutes les URL renvoient 401 Unauthorized avec "WW-Authenticate: Basic"

Question 5: décodez l'en-tête `Authorization: Basic …` (`echo … | base64 -d`). Est-ce du chiffrement ?
Non,"echo YnJ1bm86YnJ1bm8xMjM= | base64 -d" donne "alice:alice123", c'est un simple encodage Base64, réversible sans clé
Les identifiants circulent donc en clair et à chaque requete. Basic Auth n'est acceptable qu'en HTTPS*/