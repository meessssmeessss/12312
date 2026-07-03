package com.minetopia.plugin.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Houdt bij welke spelers op dit moment geboeid zijn door een agent.
 * Geboeide spelers kunnen zich niet bewegen (zie JailListener).
 */
public class JailManager {

    private final Set<UUID> handcuffed = new HashSet<>();

    public boolean isHandcuffed(UUID uuid) {
        return handcuffed.contains(uuid);
    }

    /** Boeit of ontboeit een speler. Retourneert de nieuwe status (true = nu geboeid). */
    public boolean toggle(UUID uuid) {
        if (handcuffed.contains(uuid)) {
            handcuffed.remove(uuid);
            return false;
        } else {
            handcuffed.add(uuid);
            return true;
        }
    }
}
