package de.feli490.utils.hytale;

import de.feli490.utils.hytale.playerdata.PlayerDataProvider;
import de.feli490.utils.hytale.playerdata.PlayerDataProviderService;

public class PlayerDataProviderInstance {

    /**
     * Use {@link PlayerDataProviderService#get()} instead.
     *
     * @return
     */
    @Deprecated(forRemoval = true)
    public static PlayerDataProvider get() {
        return PlayerDataProviderService.get();
    }
}
