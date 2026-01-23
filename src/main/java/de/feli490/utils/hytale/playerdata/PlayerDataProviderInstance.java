package de.feli490.utils.hytale.playerdata;

public class PlayerDataProviderInstance {

    private static PlayerDataProvider INSTANCE;

    public static PlayerDataProvider get() {
        return PlayerDataProviderInstance.INSTANCE;
    }

    static void set(PlayerDataProvider playerDataProvider) {
        PlayerDataProviderInstance.INSTANCE = playerDataProvider;
    }
}
