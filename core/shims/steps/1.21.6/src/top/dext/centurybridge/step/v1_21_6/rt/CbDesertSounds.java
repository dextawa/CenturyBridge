package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds (TerracottaBlock's animateTick), its tag and its
 * SAND_WIND sound, which 1.21.6 replaced by per-block desert sounds on SandBlock and the dry vegetation blocks (javap).
 */
public final class CbDesertSounds {

    private CbDesertSounds() {
    }

    /** SoundEvents.SAND_WIND (field_56417) */
    public static net.minecraft.class_3414 field_56417() {
        return SAND_WIND;
    }

    /**
     * 1.21.5's BlockTags.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS, which 1.21.6 dropped from BlockTags and from its vanilla
     * data: the same key, so a datapack or mod that still defines the tag fills it (vanilla 1.21.6 data leaves it
     * empty, and its own terracotta makes no ambient sound either).
     */
    static final net.minecraft.class_6862<net.minecraft.class_2248> PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS =
        net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41254,
            net.minecraft.class_2960.method_60656("plays_ambient_desert_block_sounds"));

    /**
     * 1.21.5's SoundEvents.SAND_WIND ("block.sand.wind"), which 1.21.6 dropped: the same id, heard where a resource
     * pack still defines it (1.21.6's vanilla sounds.json does not).
     */
    public static final net.minecraft.class_3414 SAND_WIND =
        net.minecraft.class_3414.method_47908(net.minecraft.class_2960.method_60656("block.sand.wind"));

    /**
     * 1.21.5's AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds (class_10739.method_67408): only for a block in
     * PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS with air above (level.isEmptyBlock); 1 in 1600 SAND_IDLE at the block when 3
     * of the 4 surface blocks 8 away are in the tag, 1 in 10000 SAND_WIND to the player in a DESERT / IS_BADLANDS
     * biome under the same condition.
     */
    public static void method_67408(net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
            net.minecraft.class_5819 random) {
        if (!state.method_26164(PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS) || !level.method_8311(pos.method_10084())) {
            return;
        }
        if (random.method_43048(1600) == 0 && surrounded(level, pos)) {
            level.method_8486(pos.method_10263(), pos.method_10264(), pos.method_10260(), net.minecraft.class_3417.field_56418,
                net.minecraft.class_3419.field_15256, 1.0F, 1.0F, false);
        }
        if (random.method_43048(10000) == 0 && desertish(level.method_23753(pos)) && surrounded(level, pos)) {
            level.method_67392(SAND_WIND, net.minecraft.class_3419.field_15256, 1.0F, 1.0F);
        }
    }

    private static boolean desertish(net.minecraft.class_6880<net.minecraft.class_1959> biome) {
        return biome.method_40225(net.minecraft.class_1972.field_9424) || biome.method_40220(net.minecraft.class_6908.field_36513);
    }

    private static boolean surrounded(net.minecraft.class_1937 level, net.minecraft.class_2338 pos) {
        int found = 0;
        for (net.minecraft.class_2350 d : net.minecraft.class_2350.class_2353.field_11062) {
            net.minecraft.class_2338 at = pos.method_10079(d, 8);
            net.minecraft.class_2680 s = level.method_8320(at.method_33096(level.method_67393(net.minecraft.class_2902.class_2903.field_13202, at) - 1));
            if (s.method_26164(PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS)) {
                found++;
                if (found >= 3) {
                    return true;
                }
            }
        }
        return false;
    }
}
