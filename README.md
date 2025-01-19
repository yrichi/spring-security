## **1. Introduction au Kata**

### Objectif :
- Comprendre les concepts fondamentaux de Spring Security.
- Tester différentes configurations (authentification, autorisation, protection, etc.).
- Manipuler des profils et des scénarios avancés.

### Pré-requis :
- Java 17+ (ou au moins 11 si nécessaire).
- Spring Boot (version récente, ex. 3.x).
- Une base de données relationnelle (PostgreSQL, H2 ou autre).
- Connaissances de base sur Spring (MVC, Beans).

### Structure générale :
Chaque étape doit :
1. Décrire l’objectif (ce que tu vas apprendre/tester).
2. Proposer une configuration ou du code à écrire.
3. Indiquer comment valider (tests, comportement attendu).
4. Fournir des variantes pour enrichir la compréhension.

---

## **2. Étapes du Kata**

### **Étape 1 : Configuration de base avec In-Memory Authentication**
- **Objectif :** Mettre en place une authentification minimale avec des utilisateurs en mémoire.
- **Tâches :**
    1. Ajouter la dépendance Spring Security.
    2. Créer une classe `DevSecurityConfig` pour gérer un utilisateur `admin` et un utilisateur `user`.
    3. Protéger toutes les URLs avec `authenticated()`.
- **Validation :** Tester avec `curl` ou Postman :
    - `/public` est libre d’accès.
    - `/admin` nécessite l’utilisateur `admin`.
- **Variante :** Configurer une hiérarchie de rôles (`ROLE_ADMIN > ROLE_USER`).

---

### **Étape 2 : Authentification JDBC avec profils Spring**
- **Objectif :** Charger les utilisateurs depuis une base de données.
- **Tâches :**
    1. Créer une base avec les tables `users` et `authorities`.
    2. Configurer `JdbcUserDetailsManager` dans `ProdSecurityConfig`.
    3. Activer cette configuration uniquement pour le profil `prod`.
- **Validation :**
    - Lancer l'application avec `-Dspring.profiles.active=prod`.
    - Tester l’authentification avec les utilisateurs stockés dans la base.
- **Variante :** Ajouter des règles d’autorisation pour `/api/**` accessibles uniquement aux utilisateurs avec le rôle `ROLE_API`.

---

### **Étape 3 : Règles d’autorisation avancées**
- **Objectif :** Mettre en place des règles complexes pour contrôler l’accès à des ressources spécifiques.
- **Tâches :**
    1. Configurer une autorisation basée sur des expressions SpEL (`@PreAuthorize`).
    2. Créer un service qui retourne un utilisateur seulement si son ID correspond à l’utilisateur authentifié.
    3. Tester avec des utilisateurs ayant des rôles différents.
- **Validation :** Vérifier que seul l’utilisateur concerné peut accéder à ses données.
- **Variante :** Ajouter des autorisations dynamiques chargées depuis une base de données.

---

### **Étape 4 : Authentification avec JWT**
- **Objectif :** Implémenter une API REST sécurisée avec des tokens JWT.
- **Tâches :**
    1. Ajouter une route `/auth/login` pour générer des tokens JWT.
    2. Configurer un filtre pour valider les tokens à chaque requête.
    3. Désactiver la gestion de session (`stateless`).
- **Validation :**
    - Tester l’émission d’un token via `/auth/login`.
    - Vérifier que les requêtes avec un token valide accèdent aux ressources.
- **Variante :** Ajouter la révocation des tokens (stockage côté serveur).

---

### **Étape 5 : Configuration multi-chaînes de filtres**
- **Objectif :** Appliquer des règles de sécurité spécifiques pour différentes parties de l’application.
- **Tâches :**
    1. Configurer une chaîne pour `/admin/**` avec authentification form-based.
    2. Configurer une chaîne pour `/api/**` avec JWT.
    3. Configurer une chaîne pour `/public/**` accessible à tous.
- **Validation :**
    - Vérifier que les règles sont appliquées correctement selon l’URL.
- **Variante :** Ajouter des tests d’intégration pour valider le comportement.

---

### **Étape 6 : OAuth2 Login**
- **Objectif :** Permettre l’authentification via Google ou GitHub.
- **Tâches :**
    1. Ajouter les configurations OAuth2 pour un fournisseur (Google, GitHub).
    2. Tester le flux de connexion avec redirection.
- **Validation :**
    - Vérifier que l’utilisateur est bien redirigé vers Google/GitHub pour l’authentification.
- **Variante :** Stocker les utilisateurs OAuth2 dans la base de données après leur connexion.

---

### **Étape 7 : Tests et audits**
- **Objectif :** Valider et monitorer la sécurité.
- **Tâches :**
    1. Ajouter des tests unitaires et d’intégration pour valider les configurations.
    2. Configurer un `AuthenticationEventPublisher` pour capturer les connexions réussies ou échouées.
    3. Ajouter des logs pour chaque requête sécurisée.
- **Validation :**
    - Vérifier les logs d’audit.
    - S’assurer que les tests passent pour chaque profil.
- **Variante :** Intégrer un système de monitoring comme ELK pour suivre les événements de sécurité.

---

### **Étape 8 : Sécurité des WebSockets**
- **Objectif :** Sécuriser les communications via WebSockets.
- **Tâches :**
    1. Configurer la sécurité pour des destinations STOMP.
    2. Tester avec des règles d’accès spécifiques.
- **Validation :** Vérifier que seuls les utilisateurs autorisés peuvent envoyer/recevoir des messages.
- **Variante :** Ajouter un mécanisme de jetons pour sécuriser les WebSockets.

---

### **Étape 9 : Protection contre les attaques**
- **Objectif :** Configurer les protections intégrées de Spring Security.
- **Tâches :**
    1. Activer/désactiver la protection CSRF.
    2. Configurer des règles CORS.
    3. Vérifier les en-têtes HTTP (HSTS, X-Frame-Options, etc.).
- **Validation :** Tester manuellement ou avec des outils comme OWASP ZAP.
- **Variante :** Ajouter des protections personnalisées (par exemple, limiter les connexions par IP).

---

### **Étape 10 : Déploiement sécurisé**
- **Objectif :** Préparer l’application pour un environnement de production.
- **Tâches :**
    1. Configurer HTTPS avec un certificat SSL/TLS.
    2. Ajouter des en-têtes de sécurité stricts (HSTS, Content-Security-Policy).
    3. Désactiver les endpoints inutiles en prod.
- **Validation :** Vérifier manuellement ou avec des outils de sécurité.

---

---

## **1. Points forts de la liste :**
- **Progression claire :** La liste suit une montée en complexité cohérente (in-memory → JDBC → JWT → OAuth2).
- **Exhaustivité :** Tu abordes des aspects importants comme CSRF, CORS, monitoring, OAuth2, et les tests.
- **Attention aux détails :** Tu inclues des objectifs précis comme "protéger des WebSockets", "stocker des utilisateurs OAuth2", et "revocation des tokens", qui montrent une vision approfondie du sujet.

---



## **A faire :**

### **Phase 1 : Configuration de base**
- [x] Créer un projet Spring Boot
- [x] Ajouter la dépendance Spring Security
- [x] Configurer l'authentification In-Memory
- [x] Protéger les URLs avec Spring Security
- [x] Tester l'authentification avec curl

---

### **Phase 2 : Authentification avancée**
- [x] Configurer l'authentification JDBC avec une base H2 ou Docker Compose
- [x] Activer la configuration uniquement pour le profil prod
- [x] Créer une base de données avec les tables `users` et `authorities`
- [x] Configurer une API REST sécurisée avec JWT
- [ ] Ajouter la révocation des tokens JWT

---

### **Phase 3 : Autorisation avancée**
- [ ] Configurer une autorisation basée sur des expressions SpEL
- [ ] Créer un service pour retourner un utilisateur par ID
- [ ] Configurer une chaîne de filtres pour différentes parties de l'application
- [ ] Configurer l'authentification OAuth2 avec Google ou GitHub
- [ ] Stocker les utilisateurs OAuth2 dans la base de données

---

### **Phase 4 : Sécurité et protection**
- [ ] Activer la protection CSRF
- [ ] Configurer des règles CORS (pour API publique ou privée)
- [ ] Configurer HTTPS avec un certificat SSL/TLS
- [ ] Ajouter des en-têtes de sécurité stricts (HSTS, Content-Security-Policy, etc.)
- [ ] Ajouter des protections personnalisées (par exemple, limiter les connexions par IP)

---

### **Phase 5 : WebSockets et monitoring**
- [ ] Configurer la sécurité pour des destinations STOMP (WebSockets)
- [ ] Ajouter un mécanisme de jetons pour sécuriser les WebSockets
- [ ] Intégrer un système de monitoring comme ELK
- [ ] Ajouter des logs pour chaque requête sécurisée
- [ ] Configurer un `AuthenticationEventPublisher` pour auditer les connexions

---

### **Phase 6 : Tests et validations**
- [ ] Ajouter des tests unitaires et d'intégration pour valider le comportement
- [ ] Vérifier que seul l'utilisateur concerné peut accéder à ses données
- [ ] Vérifier que les règles sont appliquées correctement selon l'URL
- [ ] Vérifier que les utilisateurs autorisés peuvent envoyer/recevoir des messages (WebSockets)
- [ ] Vérifier manuellement ou avec des outils de sécurité (OWASP ZAP, Postman, etc.)

---
