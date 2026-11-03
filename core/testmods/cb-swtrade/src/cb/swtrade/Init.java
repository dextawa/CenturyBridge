package cb.swtrade;

/** a 1.20.6 mod's own enchantment, registered at init the 1.20.6 way (tradeable by default: isTradeable answers true) */
public final class Init implements net.fabricmc.api.ModInitializer {
    public static net.minecraft.class_1887 STEADY;

    @Override
    public void onInitialize() {
        STEADY = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41176, new net.minecraft.class_2960("cb_swtrade", "steady"),
            new net.minecraft.class_1887(net.minecraft.class_1887.method_58442(net.minecraft.class_3489.field_48310, 5, 3,
                new net.minecraft.class_1887.class_9426(5, 8), new net.minecraft.class_1887.class_9426(55, 8), 2, net.minecraft.class_1304.field_6173)));
    }
}
