package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeIds;

/**
 * The recipe base classes whose 1.20.1 constructors took the recipe id (custom/special,
 * shapeless, shaped, cooking, single-item, smithing transform and trim recipes; every vanilla and
 * mod recipe class built on them runs one of these constructors). A converted 1.20.1 constructor
 * call -- new X(id, ...) or super(id, ...) -- hands its id to CbRecipeIds.stash right before the
 * 1.20.2 constructor runs; the object takes it here, as its constructor returns.
 */
@Mixin({
    net.minecraft.class_1852.class,
    net.minecraft.class_1867.class,
    net.minecraft.class_1869.class,
    net.minecraft.class_1874.class,
    net.minecraft.class_3972.class,
    net.minecraft.class_8060.class,
    net.minecraft.class_8062.class
})
public abstract class CbRecipeIdClaim {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$claimId(CallbackInfo ci) {
        CbRecipeIds.claim(this);
    }
}
