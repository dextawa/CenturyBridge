package top.dext.centurybridge.shims.v1_20_2;

import java.util.Map;
import net.minecraft.class_1291;
import net.minecraft.class_1320;
import net.minecraft.class_4081;
import net.minecraft.class_8630;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbEffectModifierMap;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;

/**
 * Each effect's attribute-modifier map, known by the effect and the effect known by it
 * (CbEffects): a converted 1.20.1 getAttributeModifiers() call gets a 1.20.1 view of the map it
 * was answered with, and putting a modifier into that view needs the effect it belongs to (the
 * template it becomes computes that effect's value) -- also while the map is still empty.
 */
@Mixin(class_1291.class)
public abstract class CbEffectModifierOwner implements CbEffectModifierMap {

    @Shadow
    @Final
    private Map<class_1320, class_8630> field_5885;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$ownModifiers(class_4081 category, int color, CallbackInfo ci) {
        CbEffects.ownModifiers(this.field_5885, (class_1291) (Object) this);
    }

    @Override
    public Map<class_1320, class_8630> cb$modifierMap() {
        return this.field_5885;
    }
}
