package de.feli490.utils.hytale.playerdata.sql;

import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import java.util.List;
import java.util.UUID;

public record SqlPlayerData(UUID uuid, List<UsedNameData> usedNames) implements PlayerData {

    @Override
    public List<UsedNameData> getSortedUsedNames() {
        return usedNames();
    }
}
