package de.feli490.utils.hytale.playerdata.pojo;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public interface PlayerData {

    UUID getUuid();

    List<UsedNameData> getUsedNames();

    default List<UsedNameData> getSortedUsedNames() {
        return getUsedNames().stream()
                             .sorted(Comparator.comparingLong(UsedNameData::getFirstSeen))
                             .toList();
    }

    default UsedNameData getLastKnownUsername() {
        return getUsedNames().getLast();
    }

    default boolean isOnline() {
        return Universe.get()
                       .getPlayer(getUuid()) != null;
    }

    default PlayerRef getPlayerRef() {
        return Universe.get()
                       .getPlayer(getUuid());
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
