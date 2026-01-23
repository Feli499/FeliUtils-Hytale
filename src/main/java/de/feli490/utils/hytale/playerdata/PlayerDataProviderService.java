package de.feli490.utils.hytale.playerdata;

public class PlayerDataProviderService {

    private static PlayerDataProvider INSTANCE;

    public static PlayerDataProvider get() {
        return PlayerDataProviderService.INSTANCE;
    }

    static void set(PlayerDataProvider playerDataProvider) {
        PlayerDataProviderService.INSTANCE = playerDataProvider;
    }
}
