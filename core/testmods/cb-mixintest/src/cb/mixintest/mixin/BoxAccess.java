package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AABB(BlockPos, BlockPos): gone in 1.20.3 (step 2 superCtorReshapes, the corners converted to six doubles) */
@Mixin(net.minecraft.class_238.class)
public interface BoxAccess {
    @Invoker("<init>")
    static net.minecraft.class_238 cbtest$newBox(net.minecraft.class_2338 a, net.minecraft.class_2338 b) {
        throw new AssertionError();
    }
}
