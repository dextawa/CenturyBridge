package top.dext.centurybridge.step.v1_21.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4431;
import org.slf4j.Logger;

/**
 * 1.20.6's RealmsServerPlayerList (net/minecraft/class_4881): one Realms server's id and the UUIDs of the players
 * on it, as the live player list reports them. 1.21 gave the intermediary name to its RealmsServerPlayerLists (a
 * map of server id to resolved profiles); a 1.20.6 mod's references to its own class are renamed here
 * (classRenames, s6fin-realms.json). 1.20.6's body.
 */
@Environment(EnvType.CLIENT)
public class class_4881 extends net.minecraft.class_4881 {
    public static final Logger field_22628 = com.mojang.logging.LogUtils.getLogger();
    /** serverId */
    public long field_22626;
    /** players */
    public List<UUID> field_22627;

    public class_4881() {
    }

    /** parse(json): serverId (-1 when absent) and the players of the "playerList" string (a JSON array), else none */
    public static class_4881 method_25074(JsonObject json) {
        class_4881 list = new class_4881();
        try {
            list.field_22626 = class_4431.method_21546("serverId", json, -1L);
            String players = class_4431.method_21547("playerList", json, null);
            if (players != null) {
                JsonElement element = JsonParser.parseString(players);
                if (element.isJsonArray()) {
                    list.field_22627 = method_25073(element.getAsJsonArray());
                } else {
                    list.field_22627 = Lists.newArrayList();
                }
            } else {
                list.field_22627 = Lists.newArrayList();
            }
        } catch (Exception e) {
            field_22628.error("Could not parse RealmsServerPlayerList: {}", e.getMessage());
        }
        return list;
    }

    /** parsePlayers(array): each entry's "playerId" that parses as a UUID */
    public static List<UUID> method_25073(JsonArray array) {
        List<UUID> players = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                UUID id = class_4431.method_49595("playerId", element.getAsJsonObject(), null);
                if (id != null) {
                    players.add(id);
                }
            }
        }
        return players;
    }
}
