package com.kata.springsecurity.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * Ajoute le token à la blacklist
     * @param token le JWT à invalider
     * @param expirationDate epoch (en ms) indiquant quand le token expire
     */
    public void blacklistToken(String token, long expirationDate) {
        blacklist.put(token, expirationDate);
    }

    /**
     * Vérifie si le token est dans la blacklist
     */
    public boolean isTokenBlacklisted(String token) {
        // on peut également vérifier si l'heure actuelle < expirationDate stockée,
        // et nettoyer la map si besoin
        return blacklist.containsKey(token);
    }

    /**
     * Méthode de nettoyage optionnelle pour retirer les tokens expirés de la blacklist
     */
    @Scheduled(fixedRate = 10000) // par ex. toutes les 30 secondes
    public void cleanupBlacklist() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}