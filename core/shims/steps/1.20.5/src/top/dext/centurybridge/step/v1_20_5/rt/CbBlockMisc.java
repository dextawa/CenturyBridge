package top.dext.centurybridge.step.v1_20_5.rt;

/** Single members of the block area whose 1.20.4 shape needs a converter of its own. */
public final class CbBlockMisc {

    /**
     * CandleCakeBlock.byCandle(Block) took any block where 1.20.5 takes a CandleBlock: both look the
     * block up in BY_CANDLE and take the cake's default state. A block that is not a candle has no
     * cake there, so 1.20.4 failed at the default-state call on the missing cake; null passes that
     * failure on unchanged (1.20.5's lookup of null finds no cake either).
     */
    public static net.minecraft.class_5544 candle(net.minecraft.class_2248 block) {
        return block instanceof net.minecraft.class_5544 candle ? candle : null;
    }

    private static net.minecraft.class_1291[][] beaconEffects;

    /**
     * BeaconBlockEntity.BEACON_EFFECTS as 1.20.4's MobEffect[][] (the effects of each beacon tier):
     * 1.20.5's list of holder lists, by value, made once like 1.20.4's static final array.
     */
    public static synchronized net.minecraft.class_1291[][] field_11801() {
        if (beaconEffects == null) {
            java.util.List<java.util.List<net.minecraft.class_6880<net.minecraft.class_1291>>> tiers =
                net.minecraft.class_2580.field_11801;
            net.minecraft.class_1291[][] out = new net.minecraft.class_1291[tiers.size()][];
            for (int i = 0; i < out.length; i++) {
                java.util.List<net.minecraft.class_6880<net.minecraft.class_1291>> tier = tiers.get(i);
                out[i] = new net.minecraft.class_1291[tier.size()];
                for (int j = 0; j < out[i].length; j++) {
                    out[i][j] = tier.get(j).comp_349();
                }
            }
            beaconEffects = out;
        }
        return beaconEffects;
    }

    /** SpawnData(entity, customSpawnRules): 1.20.5 added the equipment, which no 1.20.4 spawn data had */
    public static java.util.Optional<?> noEquipment() {
        return java.util.Optional.empty();
    }

    private CbBlockMisc() {
    }
}
