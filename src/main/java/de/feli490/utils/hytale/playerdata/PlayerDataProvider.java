package de.feli490.utils.hytale.playerdata;

import java.util.Collection;
import java.util.UUID;

public interface PlayerDataProvider {

    String getLastPlayerName(UUID uuid);

    boolean isKnownPlayer(UUID uuid);

    UUID getPlayerUUIDByLastName(String lastName);

    Collection<String> getKnownPlayerNames(UUID uuid);

    Collection<UUID> getAllKnownPlayerUUIDs();
}
