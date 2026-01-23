package de.feli490.utils.hytale.playerdata.sql;

import de.feli490.utils.core.sql.SQLConnection;
import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqlPlayerDataLoader extends AbstractPlayerDataLoader {

    public SqlPlayerDataLoader(SQLConnection sqlConnection, String tableprefix) throws SQLException {

        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + tableprefix + "_known_players(playerId varchar(36) not null primary key);");
        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + tableprefix//
                                      + "_last_usernames("//
                                      + "playerId varchar(36) NOT NULL, "//
                                      + "firstSeen BIGINT NOT NULL, "//
                                      + "name varchar(30) NOT NULL, "//
                                      + "foreign key (playerId) references " + tableprefix + "_known_players (playerId)"//
                                      + ");");

    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return null;
    }

    @Override
    public boolean isKnownPlayer(UUID uuid) {
        return false;
    }

    @Override
    public PlayerData getPlayerDataByLastName(String lastName) {
        return null;
    }

    @Override
    public Collection<String> getKnownPlayerNames(UUID uuid) {
        return List.of();
    }

    @Override
    public Collection<UUID> getAllKnownPlayerUUIDs() {
        return List.of();
    }

    @Override
    public void addPlayerNameIfUnknown(UUID playerUUID, String name) throws IOException {

    }
}
