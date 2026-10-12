package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2960;

/**
 * 1.20.1 kept the vanilla custom-payload channel ids as constants of the packet classes
 * (CustomPayloadS2CPacket.BRAND, .DEBUG_PATH, ... and CustomPayloadC2SPacket.BRAND). 1.20.2 moved
 * each to the payload record that now carries that channel's data (BrandCustomPayload.ID, ...).
 * The fields below have the 1.20.1 names (fieldRedirects send a mod's GETSTATIC here) and hold the
 * very Identifier objects 1.20.2 uses, so an id compared by identity or equality matches either way.
 * Values checked against both versions' static initializers ("brand", "debug/path", ...).
 */
public final class CbNetChannels {

    // ---- CustomPayloadS2CPacket (class_2658) ----
    /** BRAND: "minecraft:brand" */
    public static final class_2960 field_12158 = net.minecraft.class_8709.field_45697;
    /** DEBUG_PATH: "minecraft:debug/path" */
    public static final class_2960 field_12161 = net.minecraft.class_8721.field_45705;
    /** DEBUG_NEIGHBORS_UPDATE: "minecraft:debug/neighbors_update" */
    public static final class_2960 field_12157 = net.minecraft.class_8720.field_45704;
    /** DEBUG_STRUCTURES: "minecraft:debug/structures" */
    public static final class_2960 field_12163 = net.minecraft.class_8726.field_45710;
    /** DEBUG_WORLDGEN_ATTEMPT: "minecraft:debug/worldgen_attempt" */
    public static final class_2960 field_12164 = net.minecraft.class_8729.field_45712;
    /** DEBUG_POI_TICKET_COUNT: "minecraft:debug/poi_ticket_count" */
    public static final class_2960 field_18957 = net.minecraft.class_8724.field_45708;
    /** DEBUG_POI_ADDED: "minecraft:debug/poi_added" */
    public static final class_2960 field_18958 = net.minecraft.class_8722.field_45706;
    /** DEBUG_POI_REMOVED: "minecraft:debug/poi_removed" */
    public static final class_2960 field_18959 = net.minecraft.class_8723.field_45707;
    /** DEBUG_VILLAGE_SECTIONS: "minecraft:debug/village_sections" */
    public static final class_2960 field_18960 = net.minecraft.class_8728.field_45711;
    /** DEBUG_GOAL_SELECTOR: "minecraft:debug/goal_selector" */
    public static final class_2960 field_18799 = net.minecraft.class_8716.field_45702;
    /** DEBUG_BRAIN: "minecraft:debug/brain" */
    public static final class_2960 field_18800 = net.minecraft.class_8708.field_45696;
    /** DEBUG_BEE: "minecraft:debug/bee" */
    public static final class_2960 field_21559 = net.minecraft.class_8707.field_45695;
    /** DEBUG_HIVE: "minecraft:debug/hive" */
    public static final class_2960 field_21560 = net.minecraft.class_8718.field_45703;
    /** DEBUG_GAME_TEST_ADD_MARKER: "minecraft:debug/game_test_add_marker" */
    public static final class_2960 field_20599 = net.minecraft.class_8714.field_45700;
    /** DEBUG_GAME_TEST_CLEAR: "minecraft:debug/game_test_clear" */
    public static final class_2960 field_20600 = net.minecraft.class_8715.field_45701;
    /** DEBUG_RAIDS: "minecraft:debug/raids" */
    public static final class_2960 field_19331 = net.minecraft.class_8725.field_45709;
    /** DEBUG_GAME_EVENT: "minecraft:debug/game_event" */
    public static final class_2960 field_28284 = net.minecraft.class_8712.field_45698;
    /** DEBUG_GAME_EVENT_LISTENERS: "minecraft:debug/game_event_listeners" */
    public static final class_2960 field_28285 = net.minecraft.class_8713.field_45699;

    // ---- CustomPayloadC2SPacket (class_2817) ----
    /** BRAND: "minecraft:brand", the same channel the client announces its brand on */
    public static final class_2960 field_12831 = net.minecraft.class_8709.field_45697;

    private CbNetChannels() {
    }
}
