# Kata JWT avec Spring Security

## Objectifs

Ce kata a pour but de vous apprendre à configurer et à utiliser un système d'authentification basé sur JSON Web Tokens (JWT) avec Spring Security. Vous apprendrez à protéger des API REST, à gérer les utilisateurs, et à valider les requêtes à l'aide de JWT.

---


## Fonctionnalités du projet

1. **Gestion des utilisateurs** :
    - Inscription des utilisateurs avec un mot de passe hashé.
    - Authentification avec un endpoint pour obtenir un JWT valide.

2. **Protection des routes** :
    - Routes publiques accessibles sans authentification.
    - Routes privées accessibles uniquement avec un JWT valide.

3. **Validation et filtrage** :
    - Implémentation d'un filtre Spring Security pour valider les tokens JWT.
    - Gestion des erreurs pour les requêtes non authentifiées ou invalides.

4. **Sécurité** :
    - Utilisation de BCrypt pour le hashage des mots de passe.
    - Validation forte des clés de signature JWT.

---

## Architecture du projet

### Structure des packages

- `config` : Contient les configurations de Spring Security et JWT.
- `controller` : Contient les endpoints REST.
- `filter` : Contient le filtre pour la validation des JWT.
- `service` : Contient la logique métier pour la gestion des utilisateurs.
- `repository` : Contient les interfaces JPA pour accéder aux données.
- `entity` : Contient les entités JPA (utilisateurs, rôles).

---



### Enchaînement d'une requête :
1. **Requête entrante :**
    - La requête arrive sur un endpoint (par ex. `/api/private/*`).
    - Le `SecurityFilterChain` décide de l'autorisation requise (publique, authentifiée, ou avec rôle spécifique).

2. **Validation du token (si requis) :**
    - Le `JWTFilter` extrait et valide le JWT (présent dans l'en-tête `Authorization`).
    - Si valide, il récupère les informations utilisateur (username, rôles) et les ajoute au `SecurityContext`.

3. **Vérification des permissions :**
    - Le `AuthenticationManager` délègue au `AuthenticationProvider` pour valider les informations d'authentification.
    - Les rôles sont comparés à ceux requis pour l'URL cible.

4. **Traitement de la requête :**
    - Si tout est valide, la requête est transmise au contrôleur correspondant.
    - Sinon, une erreur est renvoyée (`403 Forbidden` ou `401 Unauthorized`).

---

## Rôles des composants principaux

### **`AuthenticationManager`**
- Responsable de la gestion des processus d'authentification.
- S'appuie sur les `AuthenticationProvider` pour valider les détails de l'utilisateur.

### **`AuthenticationProvider` (ici : `DaoAuthenticationProvider`)**
- Valide les informations d'authentification (username et password).
- Récupère les détails utilisateur depuis le `UserDetailsService`.

### **`UserDetailsService`**
- Fournit les détails utilisateur (username, password, rôles) à partir d'une source (base de données dans ce kata).

### **`PasswordEncoder`**
- Encode et compare les mots de passe (utilisation de `BCryptPasswordEncoder`).

### **`JWTFilter`**
- Intercepte les requêtes HTTP pour extraire et valider le JWT.
- Ajoute les informations utilisateur au `SecurityContext` en cas de succès.

### **`SecurityFilterChain`**
- Définit les règles de sécurité pour chaque chemin d'accès (public, authentifié, rôle requis).


## Points clés de l'implémentation

### 1. Configuration Spring Security

```java
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JWTUtils jwtUtils;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/*").permitAll()
                        .requestMatchers("/api/public/*").permitAll()
                        .requestMatchers("/api/private/*").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(jwtUtils, customUserDetailService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

### 2. Filtre JWT
Le filtre JWT intercepte les requêtes pour valider les tokens avant qu'elles n'atteignent les endpoints REST.

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String token = getTokenFromRequest(request);

    if (token != null && jwtUtils.validateToken(token)) {
        String username = jwtUtils.getUsernameFromToken(token);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
}
```

### 3. Génération de JWT

```java
public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username);
}

private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
}
```

### 4. Contrôleur AuthController

- Endpoint pour l'authentification :
```java
@PostMapping("/api/auth/login")
public ResponseEntity<?> login(@RequestBody UserPresentation user) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
    );

    String token = jwtUtils.generateToken(user.getUsername());
    return ResponseEntity.ok(new AuthResponse(token));
}
```

---

## Endpoints disponibles

| Méthode | URL               | Description                        | Accès         |
|---------|-------------------|------------------------------------|----------------|
| POST    | `/api/auth/login`  | Authentifie un utilisateur et retourne un JWT. | Public         |
| GET     | `/api/public`      | Données accessibles à tous.         | Public         |
| GET     | `/api/private`     | Données accessibles uniquement avec un JWT valide. | Authentifié    |

---

## Instructions pour exécuter le projet

1. Clonez le projet :
   ```bash
   git clone <repository-url>
   ```

2. Configurez la base de données dans `application.properties` :
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/jwt_kata
   spring.datasource.username=<username>
   spring.datasource.password=<password>
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Lancez l'application :
   ```bash
   ./mvnw spring-boot:run
   ```

4. Testez les endpoints avec Postman ou `curl`.

---

## Améliorations possibles

- [ ] Ajouter la révocation des tokens JWT
- [ ] faire les tests unitaires de bout en bout 

---



## Références

- [Documentation Spring Security](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)
- [BCrypt Password Encoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html)

`