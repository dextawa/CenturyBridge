package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_2473;
import net.minecraft.class_2680;
import net.minecraft.class_2794;
import net.minecraft.class_3218;
import net.minecraft.class_5819;
import net.minecraft.class_8813;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.facade.AbstractTreeGrower;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldSaplingAccess;

/** A sapling built through the 1.20.2 constructor grows through its 1.20.2 grower (see CbWorldTrees). */
@Mixin(class_2473.class)
public abstract class CbWorldSaplingMixin implements CbWorldSaplingAccess {

    @Unique
    private AbstractTreeGrower cb$legacyGrower;

    @Override
    public void cb$setLegacyGrower(AbstractTreeGrower grower) {
        this.cb$legacyGrower = grower;
    }

    @Redirect(method = "method_10507", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8813;method_54085(Lnet/minecraft/class_3218;Lnet/minecraft/class_2794;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_5819;)Z"))
    private boolean cb$growLegacy(class_8813 grower, class_3218 level, class_2794 generator, class_2338 pos, class_2680 state,
                                  class_5819 random) {
        return this.cb$legacyGrower != null
            ? this.cb$legacyGrower.method_11431(level, generator, pos, state, random)
            : grower.method_54085(level, generator, pos, state, random);
    }
}
