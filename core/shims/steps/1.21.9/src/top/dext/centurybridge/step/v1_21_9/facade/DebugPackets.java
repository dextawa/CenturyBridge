package top.dext.centurybridge.step.v1_21_9.facade;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_11;
import net.minecraft.class_1275;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1355;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2658;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3449;
import net.minecraft.class_3544;
import net.minecraft.class_3765;
import net.minecraft.class_4099;
import net.minecraft.class_4102;
import net.minecraft.class_4139;
import net.minecraft.class_4140;
import net.minecraft.class_4135;
import net.minecraft.class_4142;
import net.minecraft.class_4156;
import net.minecraft.class_4208;
import net.minecraft.class_4466;
import net.minecraft.class_4482;
import net.minecraft.class_4617;
import net.minecraft.class_4831;
import net.minecraft.class_5281;
import net.minecraft.class_5321;
import net.minecraft.class_5712;
import net.minecraft.class_5714;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_8710;
import net.minecraft.class_8949;
import org.slf4j.Logger;

/**
 * 1.21.8's DebugPackets (class_4209), which 1.21.9 removed when debug information became subscriptions
 * (util/debug: DebugSubscriptions, LevelDebugSynchronizers, ServerDebugSubscribers). 1.21.8's bodies (javap -c):
 * <ul>
 * <li>the public send* statics were already empty in a released 1.21.8 (sendPoiPacketsForChunk, sendPathFindingPacket,
 * sendNeighborsUpdatePacket, sendStructurePacket, sendGoalSelector, sendRaids, sendEntityBrain, sendBeeInfo,
 * sendBreezeInfo, sendGameEventInfo, sendGameEventListenerInfo, sendHiveInfo; the three POI statics reach the empty
 * sendVillageSectionsPacket): they stay empty, what a 1.21.8 mod calling them got;</li>
 * <li>the private helpers keep their bodies (public here: an access widener never reaches a layer class, so a mod that
 * widened one still links), so a mod's mixin into one finds its 1.21.8 code: the brain memory
 * descriptions (getMemoryDescriptions / getShortDescription, every call they make still in 1.21.9), the POI
 * forwarder, the name / string helpers, and sendPacketToAllPlayers (a custom payload to every player of the level,
 * as 1.21.8).</li>
 * </ul>
 * 1.21.9 never calls this class. The members that take or build one of 1.21.8's debug payloads (addGameTestMarker,
 * clearGameTestMarkers, sendWireUpdates, the game event payload helper, the goal list helper) build the layer's
 * payload records ({@code *DebugPayload}, generated from 1.21.8's bytecode), whose types the layer registers in the
 * gameplay custom payload codec as 1.21.8 did (CbDebugPayloadTypesMixin).
 */
public class DebugPackets {
    public static final Logger field_18961 = LogUtils.getLogger();

    public DebugPackets() {
    }

    /** sendPoiPacketsForChunk(level, chunkPos) */
    public static void method_19775(class_3218 level, class_1923 chunkPos) {
    }

    /** sendPoiAddedPacket(level, pos) */
    public static void method_19776(class_3218 level, class_2338 pos) {
        method_24819(level, pos);
    }

    /** sendPoiRemovedPacket(level, pos) */
    public static void method_19777(class_3218 level, class_2338 pos) {
        method_24819(level, pos);
    }

    /** sendPoiTicketCountPacket(level, pos) */
    public static void method_19778(class_3218 level, class_2338 pos) {
        method_24819(level, pos);
    }

    /** sendVillageSectionsPacket(level, pos) */
    public static void method_24819(class_3218 level, class_2338 pos) {
    }

    /** sendPathFindingPacket(level, mob, path, maxDistanceToWaypoint) */
    public static void method_19470(class_1937 level, class_1308 mob, class_11 path, float maxDistanceToWaypoint) {
    }

    /** sendNeighborsUpdatePacket(level, pos) */
    public static void method_19472(class_1937 level, class_2338 pos) {
    }

    /** sendStructurePacket(level, structureStart) */
    public static void method_19474(class_5281 level, class_3449 structureStart) {
    }

    /** sendGoalSelector(level, mob, goalSelector) */
    public static void method_19469(class_1937 level, class_1308 mob, class_1355 goalSelector) {
    }

    /** sendRaids(level, raids) */
    public static void method_20575(class_3218 level, Collection<class_3765> raids) {
    }

    /** sendEntityBrain(entity) */
    public static void method_19774(class_1309 entity) {
    }

    /** sendBeeInfo(bee) */
    public static void method_23855(class_4466 bee) {
    }

    /** sendBreezeInfo(breeze) */
    public static void method_54909(class_8949 breeze) {
    }

    /** sendGameEventInfo(level, gameEvent, pos) */
    public static void method_33139(class_1937 level, class_6880<class_5712> gameEvent, class_243 pos) {
    }

    /** sendGameEventListenerInfo(level, listener) */
    public static void method_33140(class_1937 level, class_5714 listener) {
    }

    /** sendHiveInfo(level, pos, blockState, hive) */
    public static void method_23856(class_1937 level, class_2338 pos, class_2680 blockState, class_4482 hive) {
    }

    /** sendPoiAdded(level, poiRecord) */
    public static void method_36155(class_3218 level, class_4156 poi) {
        method_19776(level, poi.method_19141());
    }

    /** getPoiDebugName(key) */
    public static String method_44135(class_5321<?> key) {
        return key.method_29177().toString();
    }

    /** isVillageSection(holder) -- 1.21.8 answered true */
    public static boolean method_36159(class_6880<?> holder) {
        return true;
    }

    /** cutString(text) */
    public static String method_52275(String text) {
        return class_3544.method_34963(text, 255, true);
    }

    /** gossip lines: "name: type: value" for each gossip type of one target */
    public static void method_52277(List<String> out, UUID target, Object2IntMap<class_4139> gossips) {
        String name = class_4617.method_23267(target);
        gossips.forEach((type, value) -> method_52276(out, name, type, value));
    }

    public static void method_52276(List<String> out, String name, class_4139 type, Integer value) {
        out.add(name + ": " + String.valueOf(type) + ": " + value);
    }

    /** getMemoryDescriptions(entity, gameTime): "memory: value" per brain memory, sorted */
    public static List<String> method_36157(class_1309 entity, long gameTime) {
        Map<class_4140<?>, Optional<? extends class_4831<?>>> memories = entity.method_18868().method_35058();
        List<String> out = Lists.newArrayList();
        for (Map.Entry<class_4140<?>, Optional<? extends class_4831<?>>> entry : memories.entrySet()) {
            class_4140<?> type = entry.getKey();
            Optional<? extends class_4831<?>> memory = entry.getValue();
            String value;
            if (memory.isPresent()) {
                class_4831<?> expirable = memory.get();
                Object held = expirable.method_24637();
                if (type == class_4140.field_19009) {
                    long since = gameTime - (Long) held;
                    value = since + " ticks ago";
                } else if (expirable.method_24914()) {
                    value = method_36156((class_3218) entity.method_73183(), held) + " (ttl: " + expirable.method_35127() + ")";
                } else {
                    value = method_36156((class_3218) entity.method_73183(), held);
                }
            } else {
                value = "-";
            }
            out.add(class_7923.field_41129.method_10221(type).method_12832() + ": " + value);
        }
        out.sort(String::compareTo);
        return out;
    }

    /** getShortDescription(level, value) */
    public static String method_36156(class_3218 level, Object value) {
        if (value == null) {
            return "-";
        } else if (value instanceof UUID uuid) {
            return method_36156(level, level.method_66347(uuid));
        } else if (value instanceof class_1309 living) {
            return class_4617.method_36154((class_1297) living);
        } else if (value instanceof class_1275 nameable) {
            return nameable.method_5477().getString();
        } else if (value instanceof class_4142 walkTarget) {
            return method_36156(level, walkTarget.method_19094());
        } else if (value instanceof class_4102 tracker) {
            return method_36156(level, tracker.method_35066());
        } else if (value instanceof class_4208 globalPos) {
            return method_36156(level, globalPos.comp_2208());
        } else if (value instanceof class_4099 tracker) {
            return method_36156(level, tracker.method_18989());
        } else if (value instanceof class_1282 source) {
            class_1297 cause = source.method_5529();
            return cause == null ? value.toString() : method_36156(level, cause);
        } else if (value instanceof Collection<?> collection) {
            List<String> out = Lists.newArrayList();
            for (Object element : collection) {
                out.add(method_36156(level, element));
            }
            return out.toString();
        } else {
            return value.toString();
        }
    }

    /** addGameTestMarker(level, pos, text, color, lifetimeMillis) */
    public static void method_22318(class_3218 level, class_2338 pos, String text, int color, int lifetimeMillis) {
        method_22319(level, new GameTestAddMarkerDebugPayload(pos, color, text, lifetimeMillis));
    }

    /** clearGameTestMarkers(level) */
    public static void method_22317(class_3218 level) {
        method_22319(level, new GameTestClearMarkersDebugPayload());
    }

    /** sendWireUpdates(level, payload): to every player of a server level, nothing on a client level */
    public static void method_61218(class_1937 level, RedstoneWireOrientationsDebugPayload payload) {
        if (level instanceof class_3218 serverLevel) {
            method_22319(serverLevel, payload);
        }
    }

    /** the game event payload helper (level, pos, gameEvent key) */
    public static void method_55630(class_3218 level, class_243 pos, class_5321<class_5712> gameEvent) {
        method_22319(level, new GameEventDebugPayload(gameEvent, pos));
    }

    /** the goal list helper: one DebugGoal (priority, running, goal class simple name) per wrapped goal */
    public static void method_36162(List<GoalDebugPayload.DebugGoal> out, class_4135 goal) {
        out.add(new GoalDebugPayload.DebugGoal(goal.method_19057(), goal.method_19056(), goal.method_19058().getClass().getSimpleName()));
    }

    /** sendPacketToAllPlayers(level, payload) */
    public static void method_22319(class_3218 level, class_8710 payload) {
        class_2658 packet = new class_2658(payload);
        for (class_3222 player : level.method_18456()) {
            player.field_13987.method_14364(packet);
        }
    }
}
