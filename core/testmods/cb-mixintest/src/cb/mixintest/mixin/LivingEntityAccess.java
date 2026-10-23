package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LivingEntity.lyHeadRot, renamed in 1.20.2 (step 1 fieldRenames); one refmap key for both accessors */
@Mixin(net.minecraft.class_1309.class)
public interface LivingEntityAccess {
    @Accessor("lyHeadRot")
    double cbtest$getLyHeadRot();

    @Accessor("lyHeadRot")
    void cbtest$setLyHeadRot(double v);
}
