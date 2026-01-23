package de.feli490.utils.hytale.playerdata.json;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import de.feli490.utils.hytale.playerdata.pojo.CachedPlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JsonPlayerData implements CachedPlayerData {

    public static final BuilderCodec<JsonPlayerData> CODEC = //
            BuilderCodec.builder(JsonPlayerData.class, JsonPlayerData::new)
                        .append(new KeyedCodec<>("Uuid", Codec.UUID_STRING),
                                (jsonPlayerData, id, extraInfo) -> jsonPlayerData.uuid = id,
                                (jsonPlayerData, extraInfo) -> jsonPlayerData.getUuid())
                        .add()
                        .append(new KeyedCodec<>("KnownPlayerNames", KnownPlayerName.ARRAY_CODEC),
                                (jsonPlayerData, lastKnownUsername, extraInfo) -> jsonPlayerData.setKnownPlayerNames(lastKnownUsername),
                                (jsonPlayerData, extraInfo) -> jsonPlayerData.getLastKnownUsernameArray())
                        .add()
                        .build();

    private UUID uuid;

    private List<KnownPlayerName> knownPlayerNames;

    public JsonPlayerData(){
        knownPlayerNames = new ArrayList<>();
    }

    public JsonPlayerData(UUID uuid){
        this();
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public List<UsedNameData> getUsedNames() {
        return Collections.unmodifiableList(knownPlayerNames);
    }
    
    private KnownPlayerName[] getLastKnownUsernameArray(){
        return knownPlayerNames.toArray(new KnownPlayerName[0]);
    }

    public void setKnownPlayerNames(KnownPlayerName[] knownPlayerNames) {
        List<KnownPlayerName> list = Arrays.asList(knownPlayerNames);
        this.knownPlayerNames = new ArrayList<>(list);
    }

    public void addKnownPlayerName(String name){
        knownPlayerNames.add(new KnownPlayerName(name, System.currentTimeMillis()));
    }

    public static JsonPlayerData create(UUID playerUUID, String lastName) {

        JsonPlayerData jsonPlayerData = new JsonPlayerData(playerUUID);
        jsonPlayerData.addKnownPlayerName(lastName);

        return jsonPlayerData;
    }

    public static class KnownPlayerName implements UsedNameData {
        public static final BuilderCodec<KnownPlayerName> CODEC = BuilderCodec.builder(KnownPlayerName.class, KnownPlayerName::new)
                                                                              .append(new KeyedCodec<>("Name", Codec.STRING),
                                                                                      (economyBalance, name, extraInfo) -> economyBalance.name = name,
                                                                                      (economyBalance, extraInfo) -> economyBalance.getName())
                                                                              .add()
                                                                              .append(new KeyedCodec<>("FirstSeen", Codec.LONG),
                                                                                      (economyBalance, firstSeen, extraInfo) -> economyBalance.firstSeen = firstSeen,
                                                                                      (economyBalance, extraInfo) -> economyBalance.getFirstSeen())
                                                                              .add()
                                                                              .build();

        public static final ArrayCodec<KnownPlayerName> ARRAY_CODEC = new ArrayCodec<>(CODEC, KnownPlayerName[]::new, KnownPlayerName::new);


        public KnownPlayerName(){}

        private KnownPlayerName(String name, long firstSeen) {
            this.name = name;
            this.firstSeen = firstSeen;
        }

        private String name;
        private long firstSeen;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getFirstSeen() {
            return firstSeen;
        }
    }
}
