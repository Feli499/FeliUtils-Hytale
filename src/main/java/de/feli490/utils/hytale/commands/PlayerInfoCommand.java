package de.feli490.utils.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.feli490.utils.hytale.playerdata.PlayerDataProvider;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import de.feli490.utils.hytale.utils.MessageUtils;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PlayerInfoCommand extends AbstractAsyncCommand {

    private final RequiredArg<String> playerNameArg;
    private final PlayerDataProvider playerDataProvider;

    public PlayerInfoCommand(PlayerDataProvider playerDataProvider) {
        super("playerinfo", "Displays information about a player");
        addAliases("pinfo");
        requirePermission("feliutils.command.playerinfo");

        playerNameArg = withRequiredArg("player", "The name or UUID of the player to display information for", ArgTypes.STRING);

        this.playerDataProvider = playerDataProvider;
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {

        String player = playerNameArg.get(commandContext);
        PlayerData playerData = null;
        try {
            UUID uuid = UUID.fromString(player);
            playerData = playerDataProvider.getPlayerData(uuid);
        } catch (IllegalArgumentException e) {
            playerData = playerDataProvider.getPlayerDataByLastName(player);
        }

        if (playerData == null) {
            commandContext.sendMessage(MessageUtils.error("Player not found"));
            return CompletableFuture.completedFuture(null);
        }

        commandContext.sendMessage(Message.join(MessageUtils.main("Player-Infos for: "),
                                                MessageUtils.secondary(playerData.getLastKnownUsername()
                                                                                 .name())));
        commandContext.sendMessage(Message.join(MessageUtils.main("UUID: "),
                                                MessageUtils.secondary(playerData.uuid()
                                                                                 .toString())));
        commandContext.sendMessage(MessageUtils.main("Known names: "));
        List<UsedNameData> sortedUsedNames = playerData.getSortedUsedNames();
        for (int i = sortedUsedNames.size() - 1; i >= 0; i--) {
            UsedNameData usedNameData = sortedUsedNames.get(i);
            commandContext.sendMessage(Message.join(MessageUtils.main(usedNameData.name()),
                                                    MessageUtils.secondary(" (since: "),
                                                    MessageUtils.formatTimestamp(usedNameData.firstSeen())
                                                                .color(MessageUtils.MAIN_COLOR),
                                                    MessageUtils.secondary(")")));
        }
        return CompletableFuture.completedFuture(null);
    }
}
