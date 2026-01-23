package de.feli490.utils.hytale.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.feli490.utils.hytale.message.MessageBuilderFactory;
import de.feli490.utils.hytale.playerdata.PlayerDataProvider;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PlayerInfoCommand extends AbstractAsyncCommand {

    private final RequiredArg<String> playerNameArg;
    private final PlayerDataProvider playerDataProvider;
    private final MessageBuilderFactory messageBuilderFactory;

    public PlayerInfoCommand(MessageBuilderFactory messageBuilderFactory, PlayerDataProvider playerDataProvider) {
        super("playerinfo", "Displays information about a player");
        addAliases("pinfo");
        requirePermission("feliutils.command.playerinfo");

        playerNameArg = withRequiredArg("player", "The name or UUID of the player to display information for", ArgTypes.STRING);

        this.messageBuilderFactory = messageBuilderFactory;
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
            commandContext.sendMessage(messageBuilderFactory.error("Player not found"));
            return CompletableFuture.completedFuture(null);
        }

        commandContext.sendMessage(messageBuilderFactory.builder()
                                                        .main("Player-Infos for: ")
                                                        .second(playerData.getLastKnownUsername()
                                                                          .name())
                                                        .build());

        commandContext.sendMessage(messageBuilderFactory.builder()
                                                        .main("UUID: ")
                                                        .second(playerData.uuid()
                                                                          .toString())
                                                        .build());

        commandContext.sendMessage(messageBuilderFactory.main("Known names: "));
        List<UsedNameData> sortedUsedNames = playerData.getSortedUsedNames();
        for (int i = sortedUsedNames.size() - 1; i >= 0; i--) {
            UsedNameData usedNameData = sortedUsedNames.get(i);
            commandContext.sendMessage(messageBuilderFactory.builder()
                                                            .main(usedNameData.name())
                                                            .second(" (since: ")
                                                            .mainTimestamp(usedNameData.firstSeen())
                                                            .second(")")
                                                            .build());
        }
        return CompletableFuture.completedFuture(null);
    }
}
