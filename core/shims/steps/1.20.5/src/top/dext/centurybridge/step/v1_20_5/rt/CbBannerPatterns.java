package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Map;

/**
 * 1.20.4's banner pattern codes, which 1.20.5 dropped when it made banner patterns a datapack registry
 * keyed by id: a 1.20.4 banner's NBT ("Patterns": [{Pattern: "bl", Color: 3}, ...]) names each pattern by
 * its code. Shared by every area of this step that reads or writes that NBT.
 */
public final class CbBannerPatterns {

    /** 1.20.4's code of each pattern, by the id 1.20.5 keys the registry on -- as the game's BannerPatternFormatFix maps them */
    public static final Map<String, String> CODES = Map.ofEntries(
        Map.entry("minecraft:base", "b"), Map.entry("minecraft:square_bottom_left", "bl"),
        Map.entry("minecraft:square_bottom_right", "br"), Map.entry("minecraft:square_top_left", "tl"),
        Map.entry("minecraft:square_top_right", "tr"), Map.entry("minecraft:stripe_bottom", "bs"),
        Map.entry("minecraft:stripe_top", "ts"), Map.entry("minecraft:stripe_left", "ls"),
        Map.entry("minecraft:stripe_right", "rs"), Map.entry("minecraft:stripe_center", "cs"),
        Map.entry("minecraft:stripe_middle", "ms"), Map.entry("minecraft:stripe_downright", "drs"),
        Map.entry("minecraft:stripe_downleft", "dls"), Map.entry("minecraft:small_stripes", "ss"),
        Map.entry("minecraft:cross", "cr"), Map.entry("minecraft:straight_cross", "sc"),
        Map.entry("minecraft:triangle_bottom", "bt"), Map.entry("minecraft:triangle_top", "tt"),
        Map.entry("minecraft:triangles_bottom", "bts"), Map.entry("minecraft:triangles_top", "tts"),
        Map.entry("minecraft:diagonal_left", "ld"), Map.entry("minecraft:diagonal_up_right", "rd"),
        Map.entry("minecraft:diagonal_up_left", "lud"), Map.entry("minecraft:diagonal_right", "rud"),
        Map.entry("minecraft:circle", "mc"), Map.entry("minecraft:rhombus", "mr"),
        Map.entry("minecraft:half_vertical", "vh"), Map.entry("minecraft:half_horizontal", "hh"),
        Map.entry("minecraft:half_vertical_right", "vhr"), Map.entry("minecraft:half_horizontal_bottom", "hhb"),
        Map.entry("minecraft:border", "bo"), Map.entry("minecraft:curly_border", "cbo"),
        Map.entry("minecraft:gradient", "gra"), Map.entry("minecraft:gradient_up", "gru"),
        Map.entry("minecraft:bricks", "bri"), Map.entry("minecraft:globe", "glb"),
        Map.entry("minecraft:creeper", "cre"), Map.entry("minecraft:skull", "sku"),
        Map.entry("minecraft:flower", "flo"), Map.entry("minecraft:mojang", "moj"),
        Map.entry("minecraft:piglin", "pig"));

    private CbBannerPatterns() {
    }
}
