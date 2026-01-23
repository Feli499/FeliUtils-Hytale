package de.feli490.utils.hytale.playerdata.sql;

import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;

public record SqlUsedNameData(long firstSeen, String name) implements UsedNameData {

}
