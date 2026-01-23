package de.feli490.utils.hytale.playerdata.sql;

import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.core.sql.SQLConnection;
import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SqlPlayerDataLoader extends AbstractPlayerDataLoader {

    private final PreparedStatement selectAllKnownPlayersUUIDs;
    private final PreparedStatement insertKnownPlayer;
    private final PreparedStatement insertLastName;
    private final PreparedStatement selectLastUsernamesOfPlayer;
    private final HytaleLogger hytaleLogger;
    private final PreparedStatement selectPlayerByUuid;
    private final PreparedStatement selectPlayerByLastUsedName;

    public SqlPlayerDataLoader(HytaleLogger hytaleLogger, SQLConnection sqlConnection, String tableprefix) throws SQLException {
        this.hytaleLogger = hytaleLogger;

        String knownPlayersTable = tableprefix + "_known_players";
        String lastNamesTable = tableprefix + "_last_usernames";

        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + knownPlayersTable + "(playerId varchar(36) not null primary key);");
        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + lastNamesTable + "("//
                                      + "playerId varchar(36) NOT NULL, "//
                                      + "firstSeen BIGINT NOT NULL, "//
                                      + "name varchar(30) NOT NULL, "//
                                      + "foreign key (playerId) references " + knownPlayersTable + " (playerId)"//
                                      + ");");

        selectAllKnownPlayersUUIDs = sqlConnection.prepareStatement("SELECT * FROM " + knownPlayersTable + ";");
        insertKnownPlayer = sqlConnection.prepareStatement(
                "INSERT INTO " + knownPlayersTable + " (playerId) VALUES (?) ON DUPLICATE KEY UPDATE playerId = VALUES(playerId);");
        insertLastName = sqlConnection.prepareStatement("INSERT INTO " + lastNamesTable + " (playerId, firstSeen, name) VALUES (?, ?, ?);");
        selectLastUsernamesOfPlayer = sqlConnection.prepareStatement(
                "SELECT * FROM " + lastNamesTable + " WHERE playerId = ? ORDER BY firstSeen ASC;");
        selectPlayerByUuid = sqlConnection.prepareStatement("SELECT * FROM " + knownPlayersTable + " WHERE playerId = ?;");
        selectPlayerByLastUsedName = sqlConnection.prepareStatement(
                "SELECT * FROM " + lastNamesTable + " WHERE playerId = (SELECT playerId FROM " + lastNamesTable
                        + " WHERE name = ? ORDER BY firstSeen desc LIMIT 1) ORDER BY firstSeen asc;");
    }

    public void migrateData(Collection<JsonPlayerData> jsonPlayerDataCollection) throws SQLException {

        for (JsonPlayerData jsonPlayer : jsonPlayerDataCollection) {
            String uuidString = jsonPlayer.uuid()
                                          .toString();
            insertKnownPlayer.setString(1, uuidString);
            insertKnownPlayer.executeUpdate();
            for (UsedNameData usedName : jsonPlayer.usedNames()) {
                insertLastName.setString(1, uuidString);
                insertLastName.setLong(2, usedName.firstSeen());
                insertLastName.setString(3, usedName.name());
                insertLastName.executeUpdate();
            }
        }
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {

        try {
            selectLastUsernamesOfPlayer.setString(1, uuid.toString());
            try (ResultSet resultSet = selectLastUsernamesOfPlayer.executeQuery()) {
                return createPlayerData(resultSet);
            }

        } catch (SQLException e) {
            hytaleLogger.atSevere()
                        .withCause(e)
                        .log("Failed SQL for getting last usernames of player.");
        }
        return null;
    }

    @Override
    public boolean isKnownPlayer(UUID uuid) {

        try {
            selectPlayerByUuid.setString(1, uuid.toString());
            try (ResultSet resultSet = selectPlayerByUuid.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            hytaleLogger.atSevere()
                        .withCause(e)
                        .log("Failed SQL for checking if a player is known.");
        }
        return false;
    }

    @Override
    public PlayerData getPlayerDataByLastName(String lastName) {

        try {
            selectPlayerByLastUsedName.setString(1, lastName);
            try (ResultSet resultSet = selectPlayerByLastUsedName.executeQuery()) {
                return createPlayerData(resultSet);
            }
        } catch (SQLException e) {
            hytaleLogger.atSevere()
                        .withCause(e)
                        .log("Failed SQL for getting player by last known name.");
        }

        return null;
    }

    private PlayerData createPlayerData(ResultSet resultSet) throws SQLException {

        List<UsedNameData> usedNameDataList = new ArrayList<>(resultSet.getFetchSize());

        String uuidString = null;
        while (resultSet.next()) {

            if (uuidString == null)
                uuidString = resultSet.getString("playerId");

            String name = resultSet.getString("name");
            long firstSeen = resultSet.getLong("firstSeen");
            usedNameDataList.add(new SqlUsedNameData(firstSeen, name));
        }

        if (uuidString == null)
            return null;

        return new SqlPlayerData(UUID.fromString(uuidString), usedNameDataList);
    }

    @Override
    public Collection<String> getKnownPlayerNames(UUID uuid) {

        PlayerData playerData = getPlayerData(uuid);
        if (playerData == null)
            return Collections.emptyList();

        return playerData.getSortedUsedNames()
                         .stream()
                         .map(UsedNameData::name)
                         .toList();
    }

    @Override
    public Collection<UUID> getAllKnownPlayerUUIDs() {

        try {
            try (ResultSet resultSet = selectAllKnownPlayersUUIDs.executeQuery()) {
                List<UUID> uuidList = new ArrayList<>(resultSet.getFetchSize());
                while (resultSet.next()) {
                    uuidList.add(UUID.fromString(resultSet.getString("playerId")));
                }
                return uuidList;
            }
        } catch (SQLException e) {
            hytaleLogger.atSevere()
                        .withCause(e)
                        .log("Failed SQL for getting all known player UUIDs.");
        }
        return List.of();
    }

    @Override
    public void addPlayerNameIfUnknown(UUID playerUUID, String name) throws IOException {

        UUID playerUUIDByLastName = getPlayerUUIDByLastName(name);
        if (playerUUIDByLastName == playerUUID)
            return;

        try {

            String uuidString = playerUUID.toString();
            insertKnownPlayer.setString(1, uuidString);
            insertKnownPlayer.executeUpdate();

            insertLastName.setString(1, uuidString);
            insertLastName.setLong(2, System.currentTimeMillis());
            insertLastName.setString(3, name);
            insertLastName.executeUpdate();

        } catch (SQLException e) {
            hytaleLogger.atSevere()
                        .withCause(e)
                        .log("Failed saving new player name.");
        }
    }
}
