package de.feli490.utils.hytale.playerdata.pojo;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public interface PlayerData {

    UUID uuid();

    List<UsedNameData> usedNames();

    default List<UsedNameData> getSortedUsedNames() {
        return usedNames().stream()
                          .sorted(Comparator.comparingLong(UsedNameData::firstSeen))
                          .toList();
    }

    default UsedNameData getLastKnownUsername() {
        return usedNames().getLast();
    }

    default boolean isOnline() {
        return Universe.get()
                       .getPlayer(uuid()) != null;
    }

    default PlayerRef getPlayerRef() {
        return Universe.get()
                       .getPlayer(uuid());
    }

    default World getWorld() {

        PlayerRef playerRef = getPlayerRef();
        if (playerRef == null)
            return null;

        UUID worldUuid = playerRef.getWorldUuid();
        return Universe.get()
                       .getWorld(worldUuid);
    }
}
