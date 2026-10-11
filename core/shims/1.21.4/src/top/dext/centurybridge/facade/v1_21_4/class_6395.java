package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_6395 extends net.minecraft.class_1800 {
    default float call(
            net.minecraft.class_1799 stack,
            net.minecraft.class_638 world,
            net.minecraft.class_1309 entity,
            int seed
    ) {
        return Math.max(0.0F, Math.min(1.0F, unclampedCall(stack, world, entity, seed)));
    }

    float unclampedCall(
            net.minecraft.class_1799 stack,
            net.minecraft.class_638 world,
            net.minecraft.class_1309 entity,
            int seed
    );
}