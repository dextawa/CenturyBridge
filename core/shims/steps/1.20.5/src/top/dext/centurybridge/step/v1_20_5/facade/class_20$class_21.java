package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4 MapDecoration.Type (net/minecraft/class_20$class_21), which 1.20.5 replaced by the
 * MapDecorationType registry (MapDecorationTypes, Holder&lt;MapDecorationType&gt;). The 34 constants in
 * 1.20.4's order (ordinals, icon bytes and switches over the enum stay right), each with 1.20.4's
 * serialized name, item frame rendering, map color, count tracking and exploration-map flag; the
 * intermediary field names of the constants are kept as aliases, which is what 1.20.4 bytecode reads.
 * {@link #holder()} is the 1.20.5 type registered under the same name (1.20.5 kept the names);
 * {@link #of} goes back. The constants' arguments are 1.20.4's (its static initializer; the
 * 3-argument constructor meant no map color and no exploration flag).
 */
public enum class_20$class_21 implements net.minecraft.class_3542 {
    PLAYER("player", false, -1, true, false),
    FRAME("frame", true, -1, true, false),
    RED_MARKER("red_marker", false, -1, true, false),
    BLUE_MARKER("blue_marker", false, -1, true, false),
    TARGET_X("target_x", true, -1, false, false),
    TARGET_POINT("target_point", true, -1, false, false),
    PLAYER_OFF_MAP("player_off_map", false, -1, true, false),
    PLAYER_OFF_LIMITS("player_off_limits", false, -1, true, false),
    MANSION("mansion", true, 5393476, false, true),
    MONUMENT("monument", true, 3830373, false, true),
    BANNER_WHITE("banner_white", true, -1, true, false),
    BANNER_ORANGE("banner_orange", true, -1, true, false),
    BANNER_MAGENTA("banner_magenta", true, -1, true, false),
    BANNER_LIGHT_BLUE("banner_light_blue", true, -1, true, false),
    BANNER_YELLOW("banner_yellow", true, -1, true, false),
    BANNER_LIME("banner_lime", true, -1, true, false),
    BANNER_PINK("banner_pink", true, -1, true, false),
    BANNER_GRAY("banner_gray", true, -1, true, false),
    BANNER_LIGHT_GRAY("banner_light_gray", true, -1, true, false),
    BANNER_CYAN("banner_cyan", true, -1, true, false),
    BANNER_PURPLE("banner_purple", true, -1, true, false),
    BANNER_BLUE("banner_blue", true, -1, true, false),
    BANNER_BROWN("banner_brown", true, -1, true, false),
    BANNER_GREEN("banner_green", true, -1, true, false),
    BANNER_RED("banner_red", true, -1, true, false),
    BANNER_BLACK("banner_black", true, -1, true, false),
    RED_X("red_x", true, -1, false, false),
    DESERT_VILLAGE("village_desert", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    PLAINS_VILLAGE("village_plains", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    SAVANNA_VILLAGE("village_savanna", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    SNOWY_VILLAGE("village_snowy", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    TAIGA_VILLAGE("village_taiga", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    JUNGLE_TEMPLE("jungle_temple", true, net.minecraft.class_3620.field_15993.field_16011, false, true),
    SWAMP_HUT("swamp_hut", true, net.minecraft.class_3620.field_15993.field_16011, false, true);

    /** PLAYER (1.20.4 field) */
    public static final class_20$class_21 field_91 = PLAYER;
    /** FRAME (1.20.4 field) */
    public static final class_20$class_21 field_95 = FRAME;
    /** RED_MARKER (1.20.4 field) */
    public static final class_20$class_21 field_89 = RED_MARKER;
    /** BLUE_MARKER (1.20.4 field) */
    public static final class_20$class_21 field_83 = BLUE_MARKER;
    /** TARGET_X (1.20.4 field) */
    public static final class_20$class_21 field_84 = TARGET_X;
    /** TARGET_POINT (1.20.4 field) */
    public static final class_20$class_21 field_85 = TARGET_POINT;
    /** PLAYER_OFF_MAP (1.20.4 field) */
    public static final class_20$class_21 field_86 = PLAYER_OFF_MAP;
    /** PLAYER_OFF_LIMITS (1.20.4 field) */
    public static final class_20$class_21 field_87 = PLAYER_OFF_LIMITS;
    /** MANSION (1.20.4 field) */
    public static final class_20$class_21 field_88 = MANSION;
    /** MONUMENT (1.20.4 field) */
    public static final class_20$class_21 field_98 = MONUMENT;
    /** BANNER_WHITE (1.20.4 field) */
    public static final class_20$class_21 field_96 = BANNER_WHITE;
    /** BANNER_ORANGE (1.20.4 field) */
    public static final class_20$class_21 field_92 = BANNER_ORANGE;
    /** BANNER_MAGENTA (1.20.4 field) */
    public static final class_20$class_21 field_97 = BANNER_MAGENTA;
    /** BANNER_LIGHT_BLUE (1.20.4 field) */
    public static final class_20$class_21 field_90 = BANNER_LIGHT_BLUE;
    /** BANNER_YELLOW (1.20.4 field) */
    public static final class_20$class_21 field_93 = BANNER_YELLOW;
    /** BANNER_LIME (1.20.4 field) */
    public static final class_20$class_21 field_94 = BANNER_LIME;
    /** BANNER_PINK (1.20.4 field) */
    public static final class_20$class_21 field_100 = BANNER_PINK;
    /** BANNER_GRAY (1.20.4 field) */
    public static final class_20$class_21 field_101 = BANNER_GRAY;
    /** BANNER_LIGHT_GRAY (1.20.4 field) */
    public static final class_20$class_21 field_107 = BANNER_LIGHT_GRAY;
    /** BANNER_CYAN (1.20.4 field) */
    public static final class_20$class_21 field_108 = BANNER_CYAN;
    /** BANNER_PURPLE (1.20.4 field) */
    public static final class_20$class_21 field_104 = BANNER_PURPLE;
    /** BANNER_BLUE (1.20.4 field) */
    public static final class_20$class_21 field_105 = BANNER_BLUE;
    /** BANNER_BROWN (1.20.4 field) */
    public static final class_20$class_21 field_106 = BANNER_BROWN;
    /** BANNER_GREEN (1.20.4 field) */
    public static final class_20$class_21 field_102 = BANNER_GREEN;
    /** BANNER_RED (1.20.4 field) */
    public static final class_20$class_21 field_99 = BANNER_RED;
    /** BANNER_BLACK (1.20.4 field) */
    public static final class_20$class_21 field_103 = BANNER_BLACK;
    /** RED_X (1.20.4 field) */
    public static final class_20$class_21 field_110 = RED_X;
    /** DESERT_VILLAGE (1.20.4 field) */
    public static final class_20$class_21 field_46177 = DESERT_VILLAGE;
    /** PLAINS_VILLAGE (1.20.4 field) */
    public static final class_20$class_21 field_46178 = PLAINS_VILLAGE;
    /** SAVANNA_VILLAGE (1.20.4 field) */
    public static final class_20$class_21 field_46179 = SAVANNA_VILLAGE;
    /** SNOWY_VILLAGE (1.20.4 field) */
    public static final class_20$class_21 field_46180 = SNOWY_VILLAGE;
    /** TAIGA_VILLAGE (1.20.4 field) */
    public static final class_20$class_21 field_46181 = TAIGA_VILLAGE;
    /** JUNGLE_TEMPLE (1.20.4 field) */
    public static final class_20$class_21 field_46182 = JUNGLE_TEMPLE;
    /** SWAMP_HUT (1.20.4 field) */
    public static final class_20$class_21 field_46183 = SWAMP_HUT;

    /** MapDecoration.Type.CODEC */
    public static final com.mojang.serialization.Codec<class_20$class_21> field_45788 =
        net.minecraft.class_3542.method_28140(class_20$class_21::values);

    /** name (1.20.4's private fields keep their intermediary names, public: a facade is never access-widened) */
    public final String field_45789;
    /** icon */
    public final byte field_81;
    /** renderedOnFrame */
    public final boolean field_111;
    /** mapColor */
    public final int field_82;
    /** trackCount */
    public final boolean field_33990;
    /** explorationMapElement */
    public final boolean field_46184;
    private volatile net.minecraft.class_6880<net.minecraft.class_9428> holder;

    class_20$class_21(String name, boolean renderedOnFrame, boolean trackCount) {
        this(name, renderedOnFrame, -1, trackCount, false);
    }

    class_20$class_21(String name, boolean renderedOnFrame, int mapColor, boolean trackCount,
                      boolean explorationMapElement) {
        this.field_45789 = name;
        this.field_33990 = trackCount;
        this.field_81 = (byte) this.ordinal();
        this.field_111 = renderedOnFrame;
        this.field_82 = mapColor;
        this.field_46184 = explorationMapElement;
    }

    /** getIcon() */
    public byte method_98() {
        return this.field_81;
    }

    /** isExplorationMapElement() */
    public boolean method_53841() {
        return this.field_46184;
    }

    /** isRenderedOnFrame() */
    public boolean method_95() {
        return this.field_111;
    }

    /** hasMapColor() */
    public boolean method_97() {
        return this.field_82 >= 0;
    }

    /** getMapColor() */
    public int method_96() {
        return this.field_82;
    }

    /** byIcon(icon): the constant of that icon, clamped to the range as 1.20.4 did */
    public static class_20$class_21 method_99(byte icon) {
        class_20$class_21[] all = values();
        return all[net.minecraft.class_3532.method_15340(icon, 0, all.length - 1)];
    }

    /** shouldTrackCount() */
    public boolean method_37342() {
        return this.field_33990;
    }

    /** getSerializedName() */
    @Override
    public String method_15434() {
        return this.field_45789;
    }

    /** the 1.20.5 map decoration type registered under this name */
    public net.minecraft.class_6880<net.minecraft.class_9428> holder() {
        net.minecraft.class_6880<net.minecraft.class_9428> h = this.holder;
        if (h == null) {
            net.minecraft.class_5321<net.minecraft.class_9428> key = net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_50082, new net.minecraft.class_2960(this.field_45789));
            this.holder = h = net.minecraft.class_7923.field_50078.method_40290(key);
        }
        return h;
    }

    /**
     * the 1.20.4 constant of a 1.20.5 map decoration type: the one of the same name; null for a type
     * 1.20.4 had no constant for (one a 1.20.5 mod registered)
     */
    public static class_20$class_21 of(net.minecraft.class_6880<net.minecraft.class_9428> type) {
        if (type == null) {
            return null;
        }
        java.util.Optional<net.minecraft.class_5321<net.minecraft.class_9428>> key = type.method_40230();
        if (key.isEmpty() || !key.get().method_29177().method_12836().equals("minecraft")) {
            return null;
        }
        String path = key.get().method_29177().method_12832();
        for (class_20$class_21 t : values()) {
            if (t.field_45789.equals(path)) {
                return t;
            }
        }
        return null;
    }
}
