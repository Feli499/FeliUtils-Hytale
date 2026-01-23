package de.feli490.utils.hytale;

import de.feli490.utils.hytale.playerdata.PlayerDataProvider;

public class PlayerDataProviderInstance {

    /**
     * Use {@link de.feli490.utils.hytale.playerdata.PlayerDataProviderInstance#get()} instead.
     *
     * @return
     */
    @Deprecated(forRemoval = true)
    public static PlayerDataProvider get() {
        return de.feli490.utils.hytale.playerdata.PlayerDataProviderInstance.get();
    }
}
