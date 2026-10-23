package top.dext.centurybridge.step.v1_20_5.compat.fapi.mininglevel.v1;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-mining-level-api-v1 FabricMineableTags, a module 1.20.5's Fabric API
 * no longer ships: the block tags swords and shears mine (honoured by CbFapiMiningLevelMixins, as the
 * module's own mixins did).
 */
public final class FabricMineableTags {

    public static final net.minecraft.class_6862<net.minecraft.class_2248> SWORD_MINEABLE = register("mineable/sword");
    public static final net.minecraft.class_6862<net.minecraft.class_2248> SHEARS_MINEABLE = register("mineable/shears");

    private FabricMineableTags() {
    }

    private static net.minecraft.class_6862<net.minecraft.class_2248> register(String id) {
        return net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41254, new net.minecraft.class_2960("fabric", id));
    }
}
