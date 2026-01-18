package de.feli490.utils.hytale.playerdata;

import java.util.UUID;

public interface PlayerDataProvider {

    String getLastPlayerName(UUID uuid);

    String getPlayerUUIDByLastName(String lastName);

    String getKnownPlayerNames(UUID uuid);
}
