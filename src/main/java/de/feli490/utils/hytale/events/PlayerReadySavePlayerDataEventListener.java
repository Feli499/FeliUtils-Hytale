package de.feli490.utils.hytale.events;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import de.feli490.utils.hytale.playerdata.PlayerDataSaver;
import de.feli490.utils.hytale.utils.PlayerUtils;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PlayerReadySavePlayerDataEventListener implements Consumer<PlayerReadyEvent> {

    private final HytaleLogger logger;
    private final PlayerDataSaver playerDataSaver;

    public PlayerReadySavePlayerDataEventListener(HytaleLogger logger, PlayerDataSaver playerDataSaver) {
        this.logger = logger;
        this.playerDataSaver = playerDataSaver;
    }

    @Override
    public void accept(PlayerReadyEvent playerReadyEvent) {
        Player player = playerReadyEvent.getPlayer();
        PlayerUtils.getPlayerRef(player)
                   .thenAccept(playerRef -> {
                       try {
                           playerDataSaver.addPlayerNameIfUnknown(playerRef.getUuid(), player.getDisplayName());
                       } catch (IOException e) {
                           logger.at(Level.SEVERE)
                                 .withCause(e)
                                 .log("Failed to save player data for player: " + player.getDisplayName() + " (" + playerRef.getUuid()
                                              + ")");
                       }
                   });

    }
}
