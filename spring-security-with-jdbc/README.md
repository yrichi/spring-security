# Projet Spring Security : Gestion de l'Authentification et de l'Autorisation

Ce projet est un **kata d'apprentissage** autour de **Spring Security**. Il explore différentes méthodes d'authentification et d'autorisation en utilisant des profils, une base de données relationnelle (PostgreSQL), et des tests d'intégration via **Testcontainers**.

## Objectifs du Projet

1. **Comprendre les bases de Spring Security :**
  - Authentification In-Memory.
  - Authentification JDBC.
  - Gestion des rôles et autorisations.

2. **Configurer des profils (`dev` et `prod`) pour la sécurité :**
  - `dev` utilise une authentification en mémoire.
  - `prod` utilise une base de données pour charger les utilisateurs.

3. **Tester les configurations de sécurité avec Testcontainers :**
  - Simulation d'une base PostgreSQL.
  - Tests automatisés pour valider les autorisations.

4. **Étendre et sécuriser les endpoints de l'application :**
  - Sécurisation des endpoints `/api/public`, `/api/protected`, et `/api/admin`.
  - Gestion des rôles (`ROLE_USER`, `ROLE_ADMIN`).

---

---

## Fonctionnalités Implémentées

### Authentification JDBC avec Spring Security
- Utilisation de la classe `CustomUser` pour représenter les utilisateurs en base :
  ```java
  @Entity
  public class CustomUser {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String username;
      private String password;
      private String roles;
  }
  ```

- Chargement des utilisateurs via `CustomUserRepository` :
  ```java
  public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
      Optional<CustomUser> findByUsername(String username);
  }
  ```


### Profils `dev` et `prod`
- profil dev : Authentification In-Memory via H2.
- profil prod : Authentification JDBC avec PostgreSQL.
- 
### Tests avec Testcontainers
- Utilisation de Testcontainers pour créer un environnement PostgreSQL isolé.
- Initialisation des données via un script `data.sql` :
  ```sql
  INSERT INTO custom_user (username, password, roles)
  VALUES
      ('admin', '{bcrypt}$2a$12$hashedAdminPassword', 'ROLE_ADMIN'),
      ('user', '{bcrypt}$2a$12$hashedUserPassword', 'ROLE_USER');
  ```
---

## Exécution

### Lancer l'application
1. **Profil `dev` :**
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   ```
   Configuration basée sur `InMemoryUserDetailsManager`.

2. **Profil `prod` :**
  - Démarrez une base PostgreSQL (ou utilisez Docker).
  - Ajoutez les utilisateurs à la base via `data.sql`.
  - Lancer l'application :
    ```bash
    ./mvnw spring-boot:run -Dspring.profiles.active=prod
    ```

### Tester l'application
- **Endpoints :**
  - `GET /api/public` : Accessible à tous.
  - `GET /api/protected` : Nécessite une authentification.
  - `GET /api/admin` : Réservé aux utilisateurs avec `ROLE_ADMIN`.

---

## Tests d'Intégration

### Description des tests
Les tests couvrent différents scénarios d'authentification et d'autorisation :
- Accès aux endpoints selon les rôles (`ROLE_USER`, `ROLE_ADMIN`).
- Vérification des erreurs (401 Unauthorized).

### Lancer les tests
```bash
./mvnw test
```
Les tests utilisent **Testcontainers** pour simuler une base PostgreSQL. Les logs détaillés sont activés dans le fichier `application-test.properties` :
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## Structure du Projet

```plaintext
src/
├── main/
│   ├── java/com/kata/springsecurity/
│   │   ├── config/       # Configurations Spring Security
│   │   ├── controller/   # Endpoints REST
│   │   ├── entity/       # Entité CustomUser
│   │   ├── repository/   # Repository JPA
│   │   └── service/      # Service UserDetails
│   └── resources/
│       ├── application.properties   # Configuration globale
│       ├── application-dev.properties
│       ├── application-prod.properties
│       └── data.sql                  # Script d'initialisation
├── test/
│   ├── java/com/kata/springsecurity/
│   │   └── controller/DemoControllerIT.java # Tests d'intégration
│   └── resources/
│       ├── application-test.properties     # Configuration des tests
```

---

## Prochaines Étapes

1. **Étendre la gestion des rôles :**
  - Ajouter des rôles dynamiques depuis la base.
  - Gérer des hiérarchies de rôles.

2. **Configurer JWT :**
  - Ajouter un endpoint `/auth/login` pour générer des tokens JWT.
  - Configurer des filtres pour sécuriser les endpoints.

3. **Sécuriser davantage :**
  - Ajouter la protection CSRF pour les formulaires.
  - Configurer HTTPS pour le déploiement en production.
