package cb.s215;

import net.fabricmc.api.ModInitializer;

/** registers the test block while the block registry is open (a block made after it froze has no intrusive holder) */
public final class Init implements ModInitializer {
    static FallBlock fall;

    @Override
    public void onInitialize() {
        net.minecraft.class_5321<net.minecraft.class_2248> key = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41254,
            net.minecraft.class_2960.method_60655("cbs215", "fall"));
        fall = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41175, key.method_29177(),
            new FallBlock(net.minecraft.class_4970.class_2251.method_9637().method_63500(key)));
    }
}
